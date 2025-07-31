package com.AAZl3l3.NettyWSserve.service;

import com.AAZl3l3.NettyWSserve.Configuration.MqConfig;
import com.AAZl3l3.NettyWSserve.pojo.MsgProtocol;
import com.AAZl3l4.common.pojo.User;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WsMqConsumer {

    private final OnlineUserService onlineUserService; // 维护 uid -> Channel 的映射
    private final MessageService messageService;       // 持久化

    // 处理单聊
    @RabbitListener(queues = MqConfig.SINGLE_QUEUE)
    public void handleSingle(String json) {
        MsgProtocol p = JSON.parseObject(json, MsgProtocol.class);
        Channel ch = onlineUserService.getChannel(p.getTo());
        if (ch != null && ch.isActive()) {
            ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(p)));
        } else {
            // 用户不在线，可记录离线未读，或后续推 Push
            log.info("用户 {} 不在线 上线时即可获取到消息", p.getTo());
        }
    }

    // 处理群聊
    @RabbitListener(queues = MqConfig.GROUP_QUEUE)
    public void handleGroup(String json) {
        MsgProtocol p = JSON.parseObject(json, MsgProtocol.class);
        List<User> members = onlineUserService.getGroupMembers();
        if (members == null) {
            log.info("成员为null", p.getTo());
            return;
        }
        members.forEach(user -> {
            //向除自己外的群成员发送消息
            String userid = String.valueOf(user.getId());
            if (userid.equals(p.getFrom())) {
                return;
            }
            Channel ch = onlineUserService.getChannel(userid);
            if (ch != null && ch.isActive()) {
                ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(p)));
            }
        });
    }
}