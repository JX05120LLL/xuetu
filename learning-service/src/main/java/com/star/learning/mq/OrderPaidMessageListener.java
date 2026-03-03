package com.star.learning.mq;

import com.star.common.mq.OrderPaidMessage;
import com.star.learning.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 订单支付成功消息监听器
 *
 * 当 order-service 完成支付后，会向 ORDER_PAID Topic 发送消息。
 * 本监听器消费该消息，调用 UserCourseService 为用户开通对应课程。
 *
 * 关键配置说明：
 *  - topic：监听的 Topic，与 order-service 发送时保持一致
 *  - consumerGroup：消费者组，同一组内多个实例只有一个会收到同一条消息（负载均衡）
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = OrderPaidMessage.TOPIC,
        consumerGroup = OrderPaidMessage.CONSUMER_GROUP
)
public class OrderPaidMessageListener implements RocketMQListener<OrderPaidMessage> {

    private final UserCourseService userCourseService;

    @Override
    public void onMessage(OrderPaidMessage message) {
        log.info("收到订单支付成功消息，订单号: {}, 用户ID: {}", message.getOrderNo(), message.getUserId());
        try {
            userCourseService.activateCoursesFromMessage(message);
        } catch (Exception e) {
            // 抛出异常后 RocketMQ 会自动重试（默认重试 16 次，间隔递增）
            // 超过重试次数后消息进入死信队列（%DLQ%learning-consumer-group），可人工处理
            log.error("课程开通失败，订单号: {}，等待 RocketMQ 重试，错误: {}",
                    message.getOrderNo(), e.getMessage(), e);
            throw e;
        }
    }
}
