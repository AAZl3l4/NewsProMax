package com.AAZl3l4.common.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitTemplateConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        // 1. 全局 ConfirmCallback（broker -> producer）
        template.setConfirmCallback((correlationData, ack, cause) -> {
            String id = correlationData != null ? correlationData.getId() : "null";
            if (ack) {
                log.debug("确认成功，消息ID:{}", id);
            } else {
                log.error("确认失败，消息ID:{}, 原因:{}", id, cause);
            }
        });

        // 2. 全局 ReturnCallback（交换机 -> 队列 路由失败）
        template.setReturnsCallback(returned -> {
            Message message = returned.getMessage();
            if (message.getMessageProperties().getReceivedDelayLong() > 0) {
                // 是延迟消息，忽略
                log.info("延迟消息，忽略处理");
            } else {
                // 非延迟消息，正常处理
                log.error("消息发送失败，应答码:{}, 原因:{}, 交换机:{}, 路由键:{}, 消息:{}",
                        returned.getReplyCode(),
                        returned.getReplyText(),
                        returned.getExchange(),
                        returned.getRoutingKey(),
                        new String(message.getBody()));
            }
        });

        return template;
    }
}