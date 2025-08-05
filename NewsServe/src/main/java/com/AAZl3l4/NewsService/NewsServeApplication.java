package com.AAZl3l4.NewsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        // 添加请求头认证
        com.AAZl3l4.common.configuration.HeaderAuthenticationFilter.class,
        // 添加权限认证
        com.AAZl3l4.common.configuration.SecurityConfig.class,
        // 添加认证错误处理
        com.AAZl3l4.common.configuration.Nopermission.class,
        // 添加mybatisPlus配置
        com.AAZl3l4.common.configuration.MpConfig.class,
        // 添加mvc配置
        com.AAZl3l4.common.configuration.WebMvcConfig.class,
        // 添加全局异常处理
        com.AAZl3l4.common.configuration.GlobalExceptionHandler.class,
        // 添加redis配置
        com.AAZl3l4.common.configuration.RedisConfig.class,
        // 添加feign客户端配置
        com.AAZl3l4.common.configuration.FeignApiConfig.class,
        // 添加aop配置
        com.AAZl3l4.common.configuration.AopConfig.class,
        // 添加feign fallback
        com.AAZl3l4.common.feignApi.FileServeApiFallbackFactory.class,
        // 添加feign fallback
        com.AAZl3l4.common.feignApi.UserServeApiFallbackFactory.class,
        // 添加异步线程池
        com.AAZl3l4.common.configuration.AsyncConfig.class,
})
@EnableFeignClients(basePackages = "com.AAZl3l4.common.feignApi")
public class NewsServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsServeApplication.class, args);
    }

}
