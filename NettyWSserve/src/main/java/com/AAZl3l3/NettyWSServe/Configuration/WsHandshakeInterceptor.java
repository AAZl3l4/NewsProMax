package com.AAZl3l3.NettyWSServe.Configuration;

import com.AAZl3l3.NettyWSServe.utils.JwtUtil;
import com.AAZl3l4.common.pojo.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WsHandshakeInterceptor extends ChannelInboundHandlerAdapter {
    // 自定义 AttributeKey(给 每个Netty连接 准备的一个 “线程安全的ThreadLocalMap”用来在整个连接生命周期内跨Handler共享数据)
    // 后续通过ctx.channel().attr(WsHandshakeInterceptor.USERS).get();获取(所以需要每次请求都new)
    public static final AttributeKey<String> USERS = AttributeKey.valueOf("userid");
    public static final AttributeKey<Boolean> PING_SENT =
            AttributeKey.valueOf("PING_SENT"); // 是否发送了PING 用来验证是否掉线的

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            String userid = getToken(req);        // 从URL或 Header里解析
            if (userid == null) {
                ctx.close();
            }
            ctx.channel().attr(USERS).set(userid);
        }
        super.channelRead(ctx, msg);
    }

    private String getToken(FullHttpRequest req) {
        // 1) 从 URL 参数里取
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        List<String> tokens = decoder.parameters().get("token");
        if (tokens != null && !tokens.isEmpty()) {
            User analysis = JwtUtil.analysis(tokens.get(0));
            // 去除全部查询参数
            String cleanUri = decoder.path();          // 只保留 /ws
            req.setUri(cleanUri);                      // 覆盖原始 URI
            return analysis.getId().toString();
        }
//        //2) 从 Header 里取
//        String header = req.headers().get("Authorization");
//        if (header != null && header.startsWith("Bearer ")) {
//            String substring = header.substring(7);
//            User analysis = JwtUtil.analysis(substring);
//            return analysis.getId().toString();
//        }
        return null;
    }
}