package com.AAZl3l4.UserServe.configuration;

import com.AAZl3l4.UserServe.utils.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
// 监听MQ的消息
public class RabbitMQConsumer {
    @Autowired
    private MailService mailService;

    // 监听错误信息发送邮件
    @RabbitListener(queuesToDeclare = @Queue("error"))
    public void errorHandling(Object message) {
        log.error("错误报告:{}", message);
        mailService.sendText("6110536@qq.com", "错误报告", message.toString());
    }
    /* 普通发送消息:
    @Autowired
    private RabbitTemplate rabbitTemplate;
    rabbitTemplate.convertAndSend("error",消息);
    */

    // 监听延迟消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delay", durable = "true"),
            exchange = @Exchange(name = "delay.direct", delayed = "true"), // 关键：delayed="true"
            key = "delay"
    ))
    public void listenDelay(String msg) {
        log.info("收到延迟消息：{}", msg);
    }
     /* 延迟发送消息:
    @Autowired
    private RabbitTemplate rabbitTemplate;
    rabbitTemplate.convertAndSend(
            "delay.direct","delay",
            "消息",
            m -> {
                m.getMessageProperties().setDelayLong(5000L); // 5 秒后投递
                return m;
            }
    );
    */
}