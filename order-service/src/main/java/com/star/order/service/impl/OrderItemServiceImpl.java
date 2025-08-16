package com.star.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.order.entity.OrderItem;
import com.star.order.mapper.OrderItemMapper;
import com.star.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单项服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderItem> getOrderItemsByCourseId(Long courseId) {
        return orderItemMapper.selectByCourseId(courseId);
    }

    @Override
    public Integer countCourseSales(Long courseId) {
        return orderItemMapper.countSoldByCourseId(courseId);
    }

    @Override
    public Double sumCourseSalesAmount(Long courseId) {
        return orderItemMapper.sumSalesAmountByCourseId(courseId);
    }
}