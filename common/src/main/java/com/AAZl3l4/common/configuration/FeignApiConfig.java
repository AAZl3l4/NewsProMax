package com.AAZl3l4.common.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignApiConfig {
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); //获取请求属性
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    request.getHeaderNames().asIterator().forEachRemaining(name -> {
                        System.out.println(name);
                        System.out.println(request.getHeader(name));
                                if (!"content-length".equalsIgnoreCase(name)) {
                                    template.header(name, request.getHeader(name));
                                }
                            }
                    );
                }
            }
        };
    }
}
