package com.star.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.order.entity.OrderItem;

import java.util.List;

/**
 * 订单项服务接口
 * 
 * @author star
 */
public interface OrderItemService extends IService<OrderItem> {

    /**
     * 根据订单ID查询订单项列表
     * 
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> getOrderItemsByOrderId(Long orderId);

    /**
     * 根据课程ID查询订单项列表
     * 
     * @param courseId 课程ID
     * @return 订单项列表
     */
    List<OrderItem> getOrderItemsByCourseId(Long courseId);

    /**
     * 统计课程销售数量
     * 
     * @param courseId 课程ID
     * @return 销售数量
     */
    Integer countCourseSales(Long courseId);

    /**
     * 统计课程销售金额
     * 
     * @param courseId 课程ID
     * @return 销售金额
     */
    Double sumCourseSalesAmount(Long courseId);
}