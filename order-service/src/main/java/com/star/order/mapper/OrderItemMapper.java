package com.star.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单项数据访问层
 * 
 * @author star
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单ID查询订单项列表
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据课程ID查询订单项列表
     */
    @Select("SELECT * FROM order_item WHERE course_id = #{courseId}")
    List<OrderItem> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计课程销售数量
     */
    @Select("SELECT COUNT(*) FROM order_item oi " +
            "INNER JOIN `order` o ON oi.order_id = o.id " +
            "WHERE oi.course_id = #{courseId} AND o.status IN (1,4)")
    Integer countSoldByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计课程销售金额
     */
    @Select("SELECT COALESCE(SUM(oi.price), 0) FROM order_item oi " +
            "INNER JOIN `order` o ON oi.order_id = o.id " +
            "WHERE oi.course_id = #{courseId} AND o.status IN (1,4)")
    Double sumSalesAmountByCourseId(@Param("courseId") Long courseId);
}