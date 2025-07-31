package com.AAZl3l3.NettyWSserve.Configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    // 定义交换机名称常量，方便后续引用
    public static final String EXCHANGE = "ws.exchange";
    // 定义单聊队列名称常量
    public static final String SINGLE_QUEUE = "ws.single.queue";
    // 定义群聊队列名称常量
    public static final String GROUP_QUEUE = "ws.group.queue";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareExchange(new TopicExchange(EXCHANGE, true, false));
        admin.declareQueue(new Queue(SINGLE_QUEUE, true));
        admin.declareQueue(new Queue(GROUP_QUEUE, true));
        admin.declareBinding(BindingBuilder
                .bind(new Queue(SINGLE_QUEUE))
                .to(new TopicExchange(EXCHANGE))
                .with("single.msg"));
        admin.declareBinding(BindingBuilder
                .bind(new Queue(GROUP_QUEUE))
                .to(new TopicExchange(EXCHANGE))
                .with("group.msg"));
        return admin;
    }
}