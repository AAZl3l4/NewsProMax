package com.AAZl3l3.NettyWSserve.service;

import com.AAZl3l3.NettyWSserve.Configuration.WsHandshakeInterceptor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NettyServe {
    // 定义WebSocket服务监听端口常量 9000
    private static final int PORT = 9000;

    @Autowired
    private WebSocketFrameHandler webSocketFrameHandler;

    @PostConstruct
    public void start() {
        //异步线程
        new Thread(() -> {
            // 创建bossGroup负责接收客户端连接 只处理 accept 事件 线程数为 1
            EventLoopGroup boss = new NioEventLoopGroup(1);
            // 创建workerGroup负责I/O读写及业务处理，线程数默认 CPU * 2
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                // 创建Netty服务端启动辅助类
                ServerBootstrap b = new ServerBootstrap();
                // 给bootstrap绑定两个线程组boss负责accept worker负责后续I/O
                b.group(boss, worker)
                        // 指定使用NIO传输通道实现类
                        .channel(NioServerSocketChannel.class)
                        // 配置子Channel的初始化逻辑
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            // 当新连接接入时 Netty会回调此方法
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                // 获取当前 Channel 的流水线
                                ChannelPipeline p = ch.pipeline();
                                // 添加HTTP编解码器 将字节解码为HttpRequest/HttpResponse
                                p.addLast(new HttpServerCodec());
                                // 支持大数据流式传输，常用于文件/大报文
                                p.addLast(new ChunkedWriteHandler());
                                // 将多个HTTP片段聚合成完整的FullHttpRequest 最大64KB
                                p.addLast(new HttpObjectAggregator(65536));
                                //心态检测 读空闲120秒 写空闲和读写空闲0秒(禁用)
                                p.addLast(new IdleStateHandler(120, 0, 0, TimeUnit.SECONDS));
                                //拦截器 获取认证信息
                                p.addLast(new WsHandshakeInterceptor());
                                // WebSocket协议升级处理器 若URI为/ws则完成握手并升级协议
                                p.addLast(new WebSocketServerProtocolHandler("/ws"));
                                // 自定义业务处理器 处理文本帧TextWebSocketFrame
                                p.addLast(webSocketFrameHandler);
                            }
                        });
                // 绑定端口并同步等待启动成功
                ChannelFuture f = b.bind(PORT).sync();
                // 阻塞等待服务器Channel关闭（优雅停机）
                f.channel().closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                // 优雅释放boss线程组资源
                boss.shutdownGracefully();
                // 优雅释放worker线程组资源
                worker.shutdownGracefully();
            }
        }).start();

    }
}