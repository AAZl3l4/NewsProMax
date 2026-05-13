package com.AAZl3l4.common.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//认证拦截器 并添加权限
@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    FilterChain chain) throws IOException, ServletException {
        // 跳过登录和注册和验证码
        String path = req.getRequestURI();
        if ("/login".equals(path) || "/register".equals(path) || "/authcode/getimg".equals(path) || "/authcode/getemail".equals(path)) {
            chain.doFilter(req, resp);
            return;
        }

        String user = req.getHeader("X-User");
        String roles = req.getHeader("X-Roles");

        // 添加权限
        if (StringUtils.hasText(user)) {
            List<GrantedAuthority> authorities =
                    Arrays.stream(roles.split(","))
                          .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                          .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

        }
        chain.doFilter(req, resp);
    }
}