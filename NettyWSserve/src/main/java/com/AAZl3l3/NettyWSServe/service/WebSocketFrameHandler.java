package com.AAZl3l3.NettyWSServe.service;

import com.AAZl3l3.NettyWSServe.Configuration.WsHandshakeInterceptor;
import com.AAZl3l3.NettyWSServe.pojo.MsgProtocol;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageService messageService;
    @Autowired
    private OnlineUserService OnlineUserService;

    /*消息格式:
    {"type":1,"to":"targetUid","body":"hello"}      // 单聊
    {"type":3,"to":"targetUid","body":"hello","time":"秒"}      // 延迟单聊(仅限在线用户)
    {"type":2,"body":"hello"}      // 群聊
    信令服务:type为10:offer||11:answer||12:ice
    * */
    // 接收到消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String json = frame.text();
        MsgProtocol p = JSON.parseObject(json, MsgProtocol.class);
        String fromUid = ctx.channel().attr(WsHandshakeInterceptor.USERS).get();
        p.setFrom(fromUid);

        // 信令服务 接收到 WebRTC 信令
        if (p.getType() == 10 || p.getType() == 11 || p.getType() == 12) {
            // 透传给目标用户
            Channel target = OnlineUserService.getChannel(p.getTo());
            if (target != null && target.isActive()) {
                //把一条WebRTC信令(offer/answer/ice)原封不动地通过Netty网络连接转发给目标用户
                target.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(p)));
            }
            return;
        }

        //聊天服务
        if (p.getType() == 1 || p.getType() == 3) {                    // 单聊
            messageService.saveSingle(fromUid, p.getTo(), p.getBody());
        } else if (p.getType() == 2) {             // 群聊
            messageService.saveGroup(fromUid, p.getBody());
        }

        if (p.getType() == 3) {
            // 延迟单聊
            rabbitTemplate.convertAndSend(
                    "wx.delay","delay",
                    JSON.toJSONString(p),
                    m -> {
                        m.getMessageProperties().setDelayLong(Integer.parseInt(p.getTime())*1000L); // 指定秒后投递
                        return m;
                    }
            );
         }else {
            // 实时消息发送到 RabbitMQ
            rabbitTemplate.convertAndSend("ws.exchange",
                    routeKey(p),   // 单聊/群聊不同是key(通过方法获取)
                    JSON.toJSONString(p));
        }


    }
    private String routeKey(MsgProtocol p) {
        return p.getType() == 1 ? "single.msg" : "group.msg";
    }

    // 连接建立(此时还获取不到消息)
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
//      System.out.println("客户端连接: " + ctx.channel().id());
    }

    // 断开连接
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        OnlineUserService.remove(ctx.channel().attr(WsHandshakeInterceptor.USERS).get());
        ctx.close();
    }

    // 异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        System.out.println("异常: " + ctx.channel().id());
        cause.printStackTrace();
        OnlineUserService.remove(ctx.channel().attr(WsHandshakeInterceptor.USERS).get());
        ctx.close();
    }

    // 心跳检测
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果是空闲
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                Boolean alreadySent = ctx.channel().attr(WsHandshakeInterceptor.PING_SENT).get();
                if (Boolean.TRUE.equals(alreadySent)) {
                    // 上一次 Ping 没收到任何数据 -> 掉线
                    OnlineUserService.remove(ctx.channel().attr(WsHandshakeInterceptor.USERS).get());
                    ctx.close();
                } else {
                    // 第一次空闲，发 Ping 并做标记
                    ctx.writeAndFlush(new PingWebSocketFrame());
                    ctx.channel().attr(WsHandshakeInterceptor.PING_SENT).set(true);
                }
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 握手成功时 获取用户ID填入在线用户列表
            OnlineUserService.put(ctx.channel().attr(WsHandshakeInterceptor.USERS).get(), ctx.channel());
        }
    }
}