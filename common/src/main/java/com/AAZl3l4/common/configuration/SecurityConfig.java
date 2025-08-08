package com.AAZl3l4.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,HeaderAuthenticationFilter headerFilter,Nopermission nopermission) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    //配置login和register可以被所有用户访问 不需要认证 支持/**通配符
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/register").permitAll();
                    auth.requestMatchers("/authcode/getimg").permitAll();
                    auth.requestMatchers("/authcode/getemail").permitAll();
                    auth.requestMatchers("/pay/callback").permitAll();
                    auth.requestMatchers("/wx/login/qrcode").permitAll();
                    auth.requestMatchers("/wx/login/token").permitAll();
                    auth.requestMatchers("/wx/bind/qrcode").permitAll();
                    auth.requestMatchers("/wx/mp/callback").permitAll();
                    auth.requestMatchers("/sse/subscribe").permitAll();
                    auth.requestMatchers("/list").permitAll();
                    auth.anyRequest().authenticated();
                })
                //关闭默认的登录
                .formLogin(AbstractHttpConfigurer::disable)
                //关闭默认的退出登录
                .logout(AbstractHttpConfigurer::disable)
                //禁用CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.addAllowedOriginPattern("*"); //允许全部地址
                    cfg.addAllowedHeader("*"); //允许携带任意请求头
                    cfg.addAllowedMethod("*"); //允许任意方法
                    cfg.setAllowCredentials(true); //允许带凭证(cookie等)
                    return cfg;
                }))
                //禁用session(用JWT不需要这个 可节省性能)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //自定义异常处理
                .exceptionHandling(handling -> {
                    //没有权限的处理类 传入实现AccessDeniedHandler接口的类 实现方法自定义没有权限时的输出
                    handling.accessDeniedHandler(nopermission);
                    //没有登录的处理类(未映射时也会进去) 传入实现AuthenticationEntryPoint接口的类 可配置成bean
                    handling.authenticationEntryPoint(nopermission);
                })
                .addFilterBefore(headerFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}