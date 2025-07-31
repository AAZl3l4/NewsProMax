package com.AAZl3l4.UserServe.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    private String appId; // 应用ID
    private String privateKey; // 商户私钥
    private String publicKey; // 支付宝公钥
    private String notifyUrl; // 异步回调 并且配置到 支付宝开放平台中的应用网关地址
    private String returnUrl; // 同步回调
    private String gatewayUrl; // 沙盒环境 正式环境是https://openapi.alipay.com/gateway.do
}
