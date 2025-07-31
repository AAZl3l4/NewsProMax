package com.AAZl3l4.gateway.utils;

import com.AAZl3l4.gateway.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 全局过滤器 身份效验
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 获取当前请求路径
        String path = request.getURI().getPath();
        if ("/user-serve/login".equals(path) ||
                "/user-serve/register".equals(path) ||
                "/user-serve/authcode/getimg".equals(path) ||
                "/user-serve/authcode/getemail".equals(path) ||
                "/user-serve/pay/callback".equals(path)||
                "/user-serve/wx/login/qrcode".equals(path)||
                "/user-serve/wx/login/token".equals(path)||
                "/user-serve/wx/bind/qrcode".equals(path)||
                "/user-serve/wx/mp/callback".equals(path))
            return chain.filter(exchange);

        // 获取JWT
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        // 解析JWT
        try {
            User user = jwtUtil.analysis(auth.substring(7));
            // 给下游信息添加请求头
            ServerWebExchange newEx = exchange
                    .mutate()
                    .request(r -> r
                            .header("X-User", String.valueOf(user.getId()))
                            .header("X-Roles", user.getRoles())
                    )
                    .build();
            return chain.filter(newEx);
        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    // 未携带正确的JWT
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    // 优先级
    @Override public int getOrder() { return -100; }
}