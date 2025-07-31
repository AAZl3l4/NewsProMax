package com.AAZl3l4.UserServe.configuration;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 微信事件路由配置类
@Configuration
public class WxMpRouterConfig {
    // 创建并返回 WxMpMessageRouter Bean
    @Bean
    public WxMpMessageRouter router(WxMpService wx, ScanHandler scanHandler) {
        WxMpMessageRouter r = new WxMpMessageRouter(wx); // 新建路由器
        r.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT) // 同步处理事件
                .event(WxConsts.EventType.SUBSCRIBE)      // 关注事件
                .handler(scanHandler).end();              // 交给 ScanHandler 处理
        r.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT) // 同步处理事件
                .event(WxConsts.EventType.SCAN)           // 已关注再扫
                .handler(scanHandler).end();              // 同样交给 ScanHandler
        return r;                                   // 返回路由器实例
    }
}
