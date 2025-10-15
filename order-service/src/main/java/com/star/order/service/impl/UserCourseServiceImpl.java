package com.star.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.common.result.R;
import com.star.common.utils.PageUtil;
import com.star.order.entity.Order;
import com.star.order.entity.OrderItem;
import com.star.order.entity.UserCourse;
import com.star.order.feign.CourseServiceClient;
import com.star.order.mapper.OrderMapper;
import com.star.order.mapper.UserCourseMapper;
import com.star.order.service.OrderItemService;
import com.star.order.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户课程关系服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCourseServiceImpl extends ServiceImpl<UserCourseMapper, UserCourse> implements UserCourseService {

    private final UserCourseMapper userCourseMapper;
    private final OrderMapper orderMapper;
    private final OrderItemService orderItemService;
    private final CourseServiceClient courseServiceClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createUserCourse(Long userId, Long courseId) {
        log.info("创建用户课程关系，用户ID: {}, 课程ID: {}", userId, courseId);
        
        // 检查是否已存在
        if (checkUserHasCourse(userId, courseId)) {
            log.warn("用户课程关系已存在，用户ID: {}, 课程ID: {}", userId, courseId);
            return true;
        }
        
        // 获取课程信息
        BigDecimal coursePrice = BigDecimal.ZERO;
        try {
            R<Object> courseResult = courseServiceClient.getCourseById(courseId);
            if (courseResult != null && courseResult.getData() != null) {
                Object courseObj = courseResult.getData();
                if (courseObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> courseMap = (Map<String, Object>) courseObj;
                    Object priceObj = courseMap.get("price");
                    if (priceObj != null) {
                        if (priceObj instanceof BigDecimal) {
                            coursePrice = (BigDecimal) priceObj;
                        } else if (priceObj instanceof Number) {
                            coursePrice = BigDecimal.valueOf(((Number) priceObj).doubleValue());
                        } else {
                            coursePrice = new BigDecimal(priceObj.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取课程价格失败，使用默认价格0，课程ID: {}", courseId, e);
        }
        
        // 创建用户课程关系
        UserCourse userCourse = new UserCourse();
        userCourse.setUserId(userId);
        userCourse.setCourseId(courseId);
        userCourse.setPurchasePrice(coursePrice);
        userCourse.setPurchaseTime(LocalDateTime.now());
        userCourse.setStatus(UserCourse.Status.NORMAL.getValue());
        userCourse.setProgress(0);
        
        boolean success = save(userCourse);
        if (success) {
            log.info("用户课程关系创建成功，ID: {}", userCourse.getId());
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean activateUserCoursesFromOrder(Long orderId) {
        log.info("从订单开通用户课程，订单ID: {}", orderId);
        
        // 获取订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            log.error("订单不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 获取订单项
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        if (orderItems == null || orderItems.isEmpty()) {
            log.warn("订单项为空，订单ID: {}", orderId);
            return false;
        }
        
        // 为每个课程创建用户课程关系
        for (OrderItem orderItem : orderItems) {
            // 检查是否已存在
            if (checkUserHasCourse(order.getUserId(), orderItem.getCourseId())) {
                log.info("用户课程关系已存在，跳过创建，用户ID: {}, 课程ID: {}", order.getUserId(), orderItem.getCourseId());
                continue;
            }
            
            UserCourse userCourse = new UserCourse();
            userCourse.setUserId(order.getUserId());
            userCourse.setCourseId(orderItem.getCourseId());
            userCourse.setOrderId(orderId);
            userCourse.setPurchasePrice(orderItem.getPrice());
            userCourse.setPurchaseTime(LocalDateTime.now());
            userCourse.setStatus(UserCourse.Status.NORMAL.getValue());
            userCourse.setProgress(0);
            
            boolean success = save(userCourse);
            if (!success) {
                log.error("开通用户课程失败，订单ID: {}, 课程ID: {}", orderId, orderItem.getCourseId());
                return false;
            }
            
            log.info("课程开通成功，用户ID: {}, 课程ID: {}", order.getUserId(), orderItem.getCourseId());
        }
        
        log.info("订单课程全部开通成功，订单ID: {}, 用户ID: {}", orderId, order.getUserId());
        return true;
    }

    @Override
    public Boolean checkUserHasCourse(Long userId, Long courseId) {
        Integer count = userCourseMapper.checkUserHasCourse(userId, courseId);
        return count != null && count > 0;
    }

    @Override
    public PageResult<UserCourse> getUserCourses(Long userId, PageParam pageParam) {
        Page<UserCourse> page = PageUtil.buildPage(pageParam);
        LambdaQueryWrapper<UserCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCourse::getUserId, userId)
               .eq(UserCourse::getStatus, UserCourse.Status.NORMAL.getValue())
               .orderByDesc(UserCourse::getPurchaseTime);
        
        page(page, wrapper);
        
        return PageUtil.buildPageResult(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProgress(Long userId, Long courseId, Integer progress) {
        if (progress < 0 || progress > 100) {
            log.warn("学习进度参数无效: {}", progress);
            return false;
        }
        
        Integer result = userCourseMapper.updateProgress(userId, courseId, progress);
        boolean success = result != null && result > 0;
        
        if (success) {
            log.info("学习进度更新成功，用户ID: {}, 课程ID: {}, 进度: {}%", userId, courseId, progress);
        }
        
        return success;
    }

    @Override
    public Integer countUserCourses(Long userId) {
        return userCourseMapper.countByUserId(userId);
    }

    @Override
    public Integer countCourseUsers(Long courseId) {
        return userCourseMapper.countByCourseId(courseId);
    }
}
