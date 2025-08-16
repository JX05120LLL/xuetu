package com.star.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.order.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付记录数据访问层
 * 
 * @author star
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    /**
     * 根据支付单号查询支付记录
     */
    @Select("SELECT * FROM payment_record WHERE payment_no = #{paymentNo} LIMIT 1")
    PaymentRecord selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据订单ID查询支付记录列表
     */
    @Select("SELECT * FROM payment_record WHERE order_id = #{orderId} ORDER BY created_time DESC")
    List<PaymentRecord> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询订单的成功支付记录
     */
    @Select("SELECT * FROM payment_record WHERE order_id = #{orderId} AND status = 1 LIMIT 1")
    PaymentRecord selectSuccessPaymentByOrderId(@Param("orderId") Long orderId);

    /**
     * 更新支付状态
     */
    @Update("UPDATE payment_record SET status = #{status}, updated_time = NOW() WHERE id = #{paymentId}")
    Integer updatePaymentStatus(@Param("paymentId") Long paymentId, @Param("status") Integer status);

    /**
     * 更新支付回调信息
     */
    @Update("UPDATE payment_record SET status = #{status}, transaction_id = #{transactionId}, " +
            "payment_time = #{paymentTime}, callback_time = NOW(), callback_content = #{callbackContent}, " +
            "updated_time = NOW() WHERE payment_no = #{paymentNo}")
    Integer updatePaymentCallback(@Param("paymentNo") String paymentNo, 
                                  @Param("status") Integer status,
                                  @Param("transactionId") String transactionId,
                                  @Param("paymentTime") LocalDateTime paymentTime,
                                  @Param("callbackContent") String callbackContent);

    /**
     * 统计支付方式使用次数
     */
    @Select("SELECT COUNT(*) FROM payment_record WHERE payment_type = #{paymentType} AND status = 1")
    Integer countByPaymentType(@Param("paymentType") Integer paymentType);
}