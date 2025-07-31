package com.AAZl3l4.UserServe.configuration;

import com.AAZl3l4.UserServe.service.IUserService;
import com.AAZl3l4.UserServe.utils.JwtUtil;
import com.AAZl3l4.common.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component // 注册为 Spring 组件
@RequiredArgsConstructor // 自动生成构造器注入
public class ScanHandler implements WxMpMessageHandler {
    private final IUserService userService; // 操作用户表
    private final JwtUtil jwtUtil; // 解析 & 生成 JWT的bean
    private final RedisTemplate<String, String> redis; // 临时存储登录 token

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage msg, Map<String, Object> ctx, WxMpService wx, WxSessionManager sm) {
        String openId = msg.getFromUser(); // 扫码用户 openId
        String scene = msg.getEventKey();  // 二维码场景值
        if (scene != null && scene.startsWith("qrscene_")) {
            scene = scene.substring(8); // 去掉前缀 qrscene_
        }
        // 1. 如果是“绑定”二维码，则把 openId 写进当前登录用户
        if (scene != null && scene.startsWith("bind_")) { // 绑定场景
            String jwt = scene.substring(5); // 取出 jwt
            Integer userId = Integer.valueOf(jwtUtil.analysis(jwt).getId().toString()); // 解析用户 id
            User user = userService.getById(userId); // 查用户
            if (user != null) { // 用户存在
                user.setWxId(openId); // 写入微信 openId
                userService.updateById(user); // 更新数据库
                return WxMpXmlOutMessage.TEXT() // 回复公众号消息
                        .content("✅ 微信绑定成功，以后可直接扫码登录")
                        .fromUser(msg.getToUser())
                        .toUser(openId).build();
            }
        }
        // 2. 如果是“登录”二维码，则直接查库发 JWT
        User user = userService.getOne(new QueryWrapper<User>().eq("wx_id", openId)); // 登录场景查用户
        if (user != null) { // 已绑定
            String token = jwtUtil.create(user);// 生成 JWT         // 把 token 临时放到 redis 前端轮询拉取
            redis.opsForValue().set("wx_login_token_" + scene, token, Duration.ofMinutes(5)); // 缓存 5 分钟
            return WxMpXmlOutMessage.TEXT()
                    .content("登录成功，即将返回网站")  //登录时公众号的提示信息
                    .fromUser(msg.getToUser())
                    .toUser(openId).build();
        }
        // 未绑定
        return WxMpXmlOutMessage.TEXT() //未绑定登录时公众号的提示信息
                .content("❌ 请先前往网站绑定微信")
                .fromUser(msg.getToUser())
                .toUser(openId).build();
    }
}