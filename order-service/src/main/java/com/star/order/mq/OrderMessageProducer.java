package com.star.order.mq;

import com.star.common.mq.OrderPaidMessage;
import com.star.order.entity.Order;
import com.star.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单消息生产者
 * 负责将订单相关事件发送到 RocketMQ
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送订单支付成功消息
     * learning-service 收到后会自动开通对应课程权限
     *
     * @param order      已支付的订单
     * @param orderItems 订单包含的课程列表
     */
    public void sendOrderPaidMessage(Order order, List<OrderItem> orderItems) {
        // 构造消息体
        List<OrderPaidMessage.CourseItem> courseItems = orderItems.stream()
                .map(item -> OrderPaidMessage.CourseItem.builder()
                        .courseId(item.getCourseId())
                        .purchasePrice(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        OrderPaidMessage message = OrderPaidMessage.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .courseItems(courseItems)
                .build();

        try {
            // 发送同步消息（确保消息一定发出去再返回）
            rocketMQTemplate.convertAndSend(OrderPaidMessage.TOPIC, message);
            log.info("订单支付成功消息发送完成，Topic: {}, 订单号: {}, 用户ID: {}, 课程数: {}",
                    OrderPaidMessage.TOPIC, order.getOrderNo(), order.getUserId(), courseItems.size());
        } catch (Exception e) {
            // 消息发送失败时记录错误日志，不抛异常（避免影响主流程事务回滚）
            // 生产环境建议：落库记录失败消息，由补偿任务重试
            log.error("订单支付成功消息发送失败，订单号: {}, 错误: {}", order.getOrderNo(), e.getMessage(), e);
        }
    }
}
