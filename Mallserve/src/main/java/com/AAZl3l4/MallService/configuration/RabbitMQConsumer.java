package com.AAZl3l4.MallService.configuration;


import com.AAZl3l4.MallService.pojo.OrderItem;
import com.AAZl3l4.MallService.service.IOrderItemService;
import com.AAZl3l4.MallService.service.IOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
// 监听MQ的消息
public class RabbitMQConsumer {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderItemService orderItemService;

    // 监听错误信息发送邮件
//    @RabbitListener(queuesToDeclare = @Queue("error"))
//    public void errorHandling(Object message) {
//        log.error("错误报告:{}", message);
//    }
    /* 普通发送消息:
    @Autowired
    private RabbitTemplate rabbitTemplate;
    rabbitTemplate.convertAndSend("error",消息);
    */

    // 监听延迟消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delay.order", durable = "true"),
            exchange = @Exchange(name = "delay.order", delayed = "true"), // 关键：delayed="true"
            key = "delay.order"
    ))
    public void listenDelay(int msg) {
        log.info("收到延迟消息：{},已超时,已取消订单", msg);
        if (orderService.getById(msg).getStatus()==('0')){
            orderService.removeById(msg);
            orderItemService.remove(new QueryWrapper<OrderItem>().eq("order_id", msg));
        }
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