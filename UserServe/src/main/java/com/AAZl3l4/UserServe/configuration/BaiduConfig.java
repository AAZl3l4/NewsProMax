package com.AAZl3l4.UserServe.configuration;

import com.baidu.aip.face.AipFace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaiduConfig {
 
    @Value("${baidu.appId}")
    private String appId;
 
    @Value("${baidu.key}")
    private String key;
 
    @Value("${baidu.secret}")
    private String secret;
 
    @Bean
    public AipFace aipFace(){
        AipFace client = new AipFace(appId, key, secret);
        client.setConnectionTimeoutInMillis(2000); // 连接超时为 2 秒
        client.setSocketTimeoutInMillis(60000); // 读取超时为 60 秒
        return client;
    }
}