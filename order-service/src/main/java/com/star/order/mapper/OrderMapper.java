package com.star.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 订单数据访问层
 * 
 * @author star
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo} LIMIT 1")
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<Order> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和状态查询订单列表
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND status = #{status} ORDER BY created_time DESC")
    List<Order> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 更新订单状态
     */
    @Update("UPDATE `order` SET status = #{status}, updated_time = NOW() WHERE id = #{orderId}")
    Integer updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 查询待支付超时的订单
     */
    @Select("SELECT * FROM `order` WHERE status = 0 AND created_time < DATE_SUB(NOW(), INTERVAL #{timeoutMinutes} MINUTE)")
    List<Order> selectTimeoutOrders(@Param("timeoutMinutes") Integer timeoutMinutes);

    /**
     * 统计用户订单数量
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE user_id = #{userId}")
    Integer countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户已支付订单金额
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM `order` WHERE user_id = #{userId} AND status IN (1,4)")
    Double sumPaidAmountByUserId(@Param("userId") Long userId);
}