package com.star.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.order.dto.CreateOrderRequest;
import com.star.order.dto.OrderDTO;
import com.star.order.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 * 
 * @author star
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     * 
     * @param userId 用户ID
     * @param request 创建订单请求
     * @return 订单详情
     */
    OrderDTO createOrder(Long userId, CreateOrderRequest request);

    /**
     * 根据订单ID查询订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDTO getOrderDetail(Long orderId);

    /**
     * 根据订单号查询订单详情
     * 
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderDTO getOrderDetailByOrderNo(String orderNo);

    /**
     * 查询用户订单列表
     * 
     * @param userId 用户ID
     * @param pageParam 分页参数
     * @return 订单列表
     */
    PageResult<OrderDTO> getUserOrders(Long userId, PageParam pageParam);

    /**
     * 根据状态查询用户订单列表
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @param pageParam 分页参数
     * @return 订单列表
     */
    PageResult<OrderDTO> getUserOrdersByStatus(Long userId, Integer status, PageParam pageParam);

    /**
     * 取消订单
     * 
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    Boolean cancelOrder(Long userId, Long orderId);

    /**
     * 确认收货（对于课程可能不需要，但保留接口）
     * 
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    Boolean confirmOrder(Long userId, Long orderId);

    /**
     * 处理支付成功回调
     * 
     * @param orderNo 订单号
     * @param paymentNo 支付单号
     * @return 是否成功
     */
    Boolean handlePaymentSuccess(String orderNo, String paymentNo);

    /**
     * 检查用户是否可以创建订单
     * 
     * @param userId 用户ID
     * @param courseIds 课程ID列表
     * @return 是否可以创建
     */
    Boolean checkCanCreateOrder(Long userId, List<Long> courseIds);

    /**
     * 统计用户订单数量
     * 
     * @param userId 用户ID
     * @return 订单数量
     */
    Integer countUserOrders(Long userId);

    /**
     * 处理超时未支付订单
     * 
     * @return 处理的订单数量
     */
    Integer handleTimeoutOrders();
}