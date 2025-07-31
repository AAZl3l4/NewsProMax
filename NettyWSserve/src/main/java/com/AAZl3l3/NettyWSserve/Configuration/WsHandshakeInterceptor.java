package com.AAZl3l3.NettyWSserve.Configuration;

import com.AAZl3l3.NettyWSserve.utils.JwtUtil;
import com.AAZl3l4.common.pojo.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

@Component
public class WsHandshakeInterceptor extends ChannelInboundHandlerAdapter {
    // 自定义 AttributeKey(给 每个Netty连接 准备的一个 “线程安全的ThreadLocalMap”用来在整个连接生命周期内跨Handler共享数据)
    public static final AttributeKey<String> USERS = AttributeKey.valueOf("userid");

    public static final AttributeKey<Boolean> PING_SENT =
            AttributeKey.valueOf("PING_SENT");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            String userid = getToken(req);        // 自己从 URL 或 Header 里解析
            if (userid == null) {
                ctx.close();
            }
            ctx.channel().attr(USERS).set(userid);
        }
        super.channelRead(ctx, msg);
    }

    private String getToken(FullHttpRequest req) {
//        // 1) 从 URL 参数里取
//        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
//        List<String> tokens = decoder.parameters().get("token");
//        if (tokens != null && !tokens.isEmpty()) {
//            return tokens.get(0);
//        }
        // 2) 从 Header 里取

        String header = req.headers().get("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String substring = header.substring(7);
            User analysis = JwtUtil.analysis(substring);
            return  analysis.getId().toString();
        }

        return null;
    }
}