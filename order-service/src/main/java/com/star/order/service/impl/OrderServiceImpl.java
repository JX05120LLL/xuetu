package com.star.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.common.result.R;
import com.star.common.utils.PageUtil;
import com.star.common.utils.RedisUtil;
import com.star.order.dto.CreateOrderRequest;
import com.star.order.dto.OrderDTO;
import com.star.order.dto.OrderItemDTO;
import com.star.order.dto.PaymentRecordDTO;
import com.star.order.entity.Order;
import com.star.order.entity.OrderItem;
import com.star.order.exception.OrderException;
import com.star.order.feign.CourseServiceClient;
import com.star.order.mapper.OrderMapper;
import com.star.order.mq.OrderMessageProducer;
import com.star.order.service.OrderItemService;
import com.star.order.service.OrderService;
import com.star.order.service.PaymentService;
import com.star.order.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemService orderItemService;
    private final PaymentService paymentService;
    private final UserCourseService userCourseService;
    private final CourseServiceClient courseServiceClient;
    private final RedisUtil redisUtil;
    private final RedissonClient redissonClient;
    private final OrderMessageProducer orderMessageProducer;

    // 订单号生成器
    private static final AtomicLong ORDER_SEQUENCE = new AtomicLong(1);

    // 分布式锁 key 前缀，完整 key 示例：lock:create:order:123（123 是用户ID）
    private static final String CREATE_ORDER_LOCK_PREFIX = "lock:create:order:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(Long userId, CreateOrderRequest request) {
        log.info("用户 {} 开始创建订单，课程列表: {}", userId, request.getCourseIds());

        // 1. 验证订单参数
        if (request.getCourseIds() == null || request.getCourseIds().isEmpty()) {
            throw OrderException.createFailed("课程列表不能为空");
        }

        // ========== 分布式锁：防止同一用户并发重复下单 ==========
        // key 示例："lock:create:order:123"，每个用户一把独立的锁，互不影响
        String lockKey = CREATE_ORDER_LOCK_PREFIX + userId;

        // 从 Redisson 拿到这把锁的对象（此时还没加锁，只是拿到了一个"锁的引用"）
        RLock lock = redissonClient.getLock(lockKey);

        // 用 isLocked 记录是否真的加锁成功了，在 finally 里判断要不要释放
        boolean isLocked = false;
        try {
            // tryLock 三个参数：
            //   参数1（0）：等待时间 0 秒，拿不到锁就立刻返回 false，不排队等待
            //   参数2（-1）：持有时间 -1，代表启用"看门狗"，每 10 秒自动续期 30 秒
            //   参数3：时间单位
            isLocked = lock.tryLock(0, -1, TimeUnit.SECONDS);

            if (!isLocked) {
                // 拿锁失败，说明该用户已有一个创建订单的请求正在处理中
                log.warn("用户 {} 重复提交订单，已拒绝", userId);
                throw OrderException.createFailed("您的订单正在处理中，请勿重复提交");
            }

            log.info("用户 {} 获取分布式锁成功，开始处理订单逻辑", userId);

            // 2. 检查用户是否已购买这些课程（锁保护下检查，防止并发场景绕过）
            for (Long courseId : request.getCourseIds()) {
                if (userCourseService.checkUserHasCourse(userId, courseId)) {
                    log.warn("用户 {} 已购买课程 {}", userId, courseId);
                    throw OrderException.createFailed("课程已购买，无需重复购买");
                }
            }

            // 3. 获取课程信息并构建订单项
            List<Object> courses = new java.util.ArrayList<>();
            for (Long courseId : request.getCourseIds()) {
                try {
                    R<Object> courseResult = courseServiceClient.getCourseById(courseId);
                    if (courseResult != null && courseResult.getData() != null) {
                        courses.add(courseResult.getData());
                    } else {
                        log.warn("课程信息不存在: {}", courseId);
                        throw OrderException.courseNotAvailable(courseId);
                    }
                } catch (OrderException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("获取课程信息失败, courseId: {}", courseId, e);
                    throw OrderException.courseNotAvailable(courseId);
                }
            }

            if (courses.isEmpty()) {
                throw OrderException.createFailed("无法获取课程信息");
            }

            // 4. 计算订单金额
            BigDecimal totalAmount = calculateTotalAmount(courses);
            BigDecimal actualAmount = totalAmount;
            BigDecimal discountAmount = BigDecimal.ZERO;

            // 5. 创建订单
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setActualAmount(actualAmount);
            order.setDiscountAmount(discountAmount);
            order.setStatus(Order.Status.PENDING.getValue());
            order.setRemark(request.getRemark());

            save(order);

            // 6. 创建订单项
            List<OrderItem> orderItems = createOrderItems(order.getId(), courses);
            orderItemService.saveBatch(orderItems);

            log.info("订单创建成功，订单号: {}, 订单ID: {}", order.getOrderNo(), order.getId());
            return convertToOrderDTO(order);

        } catch (InterruptedException e) {
            // 极少出现：等待锁的过程中线程被强制中断
            Thread.currentThread().interrupt();
            throw OrderException.createFailed("下单被中断，请重试");
        } finally {
            // ========== 释放锁（无论成功还是报错，finally 里一定会执行）==========
            // isHeldByCurrentThread()：确认这把锁确实是当前线程持有的才释放，
            // 防止万一出现异常时去释放一把不属于自己的锁
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("用户 {} 分布式锁已释放", userId);
            }
        }
    }

    @Override
    public OrderDTO getOrderDetail(Long orderId) {
        // ✅ 第一步：定义Redis缓存的key
        String cacheKey = "order:detail:" + orderId;

        // ✅ 第二步：先尝试从Redis获取缓存
        Object cachedObj = redisUtil.get(cacheKey);
        if (cachedObj != null) {
            log.info("【Redis缓存命中】订单ID: {}", orderId);
            return (OrderDTO) cachedObj; // 缓存命中，直接返回
        }

        // ✅ 第三步：缓存未命中，查询数据库
        log.info("【Redis缓存未命中，查询数据库】订单ID: {}", orderId);
        Order order = getById(orderId);
        if (order == null) {
            throw OrderException.notFound(orderId);
        }

        // ✅ 第四步：转换为DTO
        OrderDTO orderDTO = convertToOrderDTO(order);

        // ✅ 第五步：将结果存入Redis缓存（10分钟过期）
        redisUtil.set(cacheKey, orderDTO, 600); // 600秒 = 10分钟
        log.info("【Redis缓存已更新】订单ID: {}", orderId);

        return orderDTO;
    }

    @Override
    public OrderDTO getOrderDetailByOrderNo(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw OrderException.notFoundByOrderNo(orderNo);
        }
        return convertToOrderDTO(order);
    }

    @Override
    public PageResult<OrderDTO> getUserOrders(Long userId, PageParam pageParam) {
        Page<Order> page = PageUtil.buildPage(pageParam);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedTime);

        page(page, wrapper);

        List<OrderDTO> orderDTOs = page.getRecords().stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());

        return PageUtil.buildPageResult(orderDTOs, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<OrderDTO> getUserOrdersByStatus(Long userId, Integer status, PageParam pageParam) {
        Page<Order> page = PageUtil.buildPage(pageParam);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .eq(Order::getStatus, status)
                .orderByDesc(Order::getCreatedTime);

        page(page, wrapper);

        List<OrderDTO> orderDTOs = page.getRecords().stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());

        return PageUtil.buildPageResult(orderDTOs, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw OrderException.notFound(orderId);
        }

        if (!order.getUserId().equals(userId)) {
            throw OrderException.notBelongToUser(orderId, userId);
        }

        if (!Order.Status.PENDING.getValue().equals(order.getStatus())) {
            throw OrderException.invalidStatus("取消", getStatusName(order.getStatus()));
        }

        order.setStatus(Order.Status.CANCELLED.getValue());
        boolean success = updateById(order);

        if (success) {
            // ✅ 订单状态变更，删除Redis缓存
            String cacheKey = "order:detail:" + orderId;
            redisUtil.del(cacheKey);
            log.info("订单取消成功，订单ID: {}, 用户ID: {}，已删除缓存", orderId, userId);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmOrder(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw OrderException.notFound(orderId);
        }

        if (!order.getUserId().equals(userId)) {
            throw OrderException.notBelongToUser(orderId, userId);
        }

        if (!Order.Status.PAID.getValue().equals(order.getStatus())) {
            throw OrderException.invalidStatus("确认收货", getStatusName(order.getStatus()));
        }

        // 对于课程订单，确认收货可能不需要特殊处理
        log.info("订单确认收货，订单ID: {}, 用户ID: {}", orderId, userId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handlePaymentSuccess(String orderNo, String paymentNo) {
        log.info("处理支付成功回调，订单号: {}, 支付单号: {}", orderNo, paymentNo);

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw OrderException.notFoundByOrderNo(orderNo);
        }

        // 更新订单状态为已支付
        order.setStatus(Order.Status.PAID.getValue());
        boolean orderUpdated = updateById(order);

        if (!orderUpdated) {
            log.error("更新订单状态失败，订单号: {}", orderNo);
            return false;
        }

        // ✅ 订单状态变更，删除Redis缓存
        String cacheKey = "order:detail:" + order.getId();
        redisUtil.del(cacheKey);
        log.info("订单支付成功，已删除缓存，订单ID: {}", order.getId());

        // 获取订单项，异步通知 learning-service 开通课程
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
        orderMessageProducer.sendOrderPaidMessage(order, orderItems);

        log.info("支付成功处理完成，订单号: {}，已发送 MQ 消息通知开通课程", orderNo);
        return true;
    }

    @Override
    public Boolean checkCanCreateOrder(Long userId, List<Long> courseIds) {
        // 检查用户是否已购买这些课程
        for (Long courseId : courseIds) {
            if (userCourseService.checkUserHasCourse(userId, courseId)) {
                log.warn("用户 {} 已购买课程 {}", userId, courseId);
                return false;
            }
        }
        return true;
    }

    @Override
    public Integer countUserOrders(Long userId) {
        return orderMapper.countByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer handleTimeoutOrders() {
        // 查询30分钟前还未支付的订单
        List<Order> timeoutOrders = orderMapper.selectTimeoutOrders(30);

        int cancelCount = 0;
        for (Order order : timeoutOrders) {
            order.setStatus(Order.Status.CANCELLED.getValue());
            if (updateById(order)) {
                cancelCount++;
                log.info("订单支付超时自动取消，订单号: {}", order.getOrderNo());
            }
        }

        log.info("处理超时订单完成，取消订单数量: {}", cancelCount);
        return cancelCount;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long sequence = ORDER_SEQUENCE.getAndIncrement() % 10000;
        return "ORD" + dateTime + String.format("%04d", sequence);
    }

    /**
     * 计算订单总金额
     */
    private BigDecimal calculateTotalAmount(List<Object> courses) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Object courseObj : courses) {
            if (courseObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> courseMap = (Map<String, Object>) courseObj;
                Object priceObj = courseMap.get("price");
                if (priceObj != null) {
                    BigDecimal price;
                    if (priceObj instanceof BigDecimal) {
                        price = (BigDecimal) priceObj;
                    } else if (priceObj instanceof Number) {
                        price = BigDecimal.valueOf(((Number) priceObj).doubleValue());
                    } else {
                        price = new BigDecimal(priceObj.toString());
                    }
                    totalAmount = totalAmount.add(price);
                }
            }
        }
        return totalAmount;
    }

    /**
     * 创建订单项
     */
    private List<OrderItem> createOrderItems(Long orderId, List<Object> courses) {
        return courses.stream().map(courseObj -> {
            OrderItem item = new OrderItem();
            item.setOrderId(orderId);

            if (courseObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> courseMap = (Map<String, Object>) courseObj;

                // 设置课程ID
                Object idObj = courseMap.get("id");
                if (idObj != null) {
                    item.setCourseId(Long.valueOf(idObj.toString()));
                }

                // 设置课程标题
                Object titleObj = courseMap.get("title");
                if (titleObj != null) {
                    item.setCourseTitle(titleObj.toString());
                }

                // 设置课程封面
                Object coverObj = courseMap.get("coverImage");
                if (coverObj != null) {
                    item.setCourseCover(coverObj.toString());
                }

                // 设置价格
                Object priceObj = courseMap.get("price");
                if (priceObj != null) {
                    if (priceObj instanceof BigDecimal) {
                        item.setPrice((BigDecimal) priceObj);
                    } else if (priceObj instanceof Number) {
                        item.setPrice(BigDecimal.valueOf(((Number) priceObj).doubleValue()));
                    } else {
                        item.setPrice(new BigDecimal(priceObj.toString()));
                    }
                }
            }

            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 转换为OrderDTO
     */
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setCreateTime(order.getCreatedTime());
        dto.setUpdateTime(order.getUpdatedTime());
        dto.setStatusName(getStatusName(order.getStatus()));

        // 设置订单项
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList());
        dto.setOrderItems(orderItemDTOs);

        // 设置支付记录
        List<PaymentRecordDTO> paymentRecords = paymentService.getPaymentsByOrderId(order.getId());
        dto.setPaymentRecords(paymentRecords);

        return dto;
    }

    /**
     * 转换为OrderItemDTO
     */
    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(orderItem, dto);
        dto.setCreateTime(orderItem.getCreatedTime());
        return dto;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        for (Order.Status s : Order.Status.values()) {
            if (s.getValue().equals(status)) {
                return s.getLabel();
            }
        }
        return "未知状态";
    }
}