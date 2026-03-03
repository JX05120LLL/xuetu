package com.star.common.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单支付成功 MQ 消息体
 * order-service 支付成功后发送，learning-service 消费后开通课程
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaidMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** RocketMQ Topic 名称 */
    public static final String TOPIC = "ORDER_PAID";

    /** RocketMQ 消费者组名称 */
    public static final String CONSUMER_GROUP = "learning-consumer-group";

    /** 订单ID */
    private Long orderId;

    /** 订单号（业务单号，如 ORD20260303001） */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 本次购买的课程列表 */
    private List<CourseItem> courseItems;

    /**
     * 订单中每门课程的信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseItem implements Serializable {

        /** 课程ID */
        private Long courseId;

        /** 购买价格（记录快照，防止课程价格后续变更） */
        private BigDecimal purchasePrice;
    }
}
