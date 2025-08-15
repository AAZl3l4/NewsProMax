package com.AAZl3l4.UserServe.controller;

import com.AAZl3l4.UserServe.utils.MailService;
import com.AAZl3l4.common.utils.Result;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
@Tag(name = "验证码")
@RequestMapping("/authcode")
public class AuthCodeController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MailService mailService;

    @Operation(summary = "获取图片验证码")
    @GetMapping("/getimg")
    public Result getAuthCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(200,120);
        String code = captcha.text();
        // 验证码存入redis 并设置60s过期
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("imgCode:"+uuid, code, 120, TimeUnit.SECONDS);
        // 返回图片base64
        HashMap<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("img", captcha.toBase64());
        return Result.succeed(map);
    }

    @Operation(summary = "获取邮箱验证码")
    @PostMapping("/getemail")
    public Result<String> getEmailAuthCode(@RequestBody String email) {
        // 限流
        if (redisTemplate.opsForValue().get("emailCode"+email) != null) {
            return Result.error("请勿频繁获取验证码");
        }
        // 生成6位验证码
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // 发送验证码邮件
        mailService.sendText(email, "验证码", code);
//        System.out.println(code);
        // 验证码存入redis 并设置60s过期
        redisTemplate.opsForValue().set("emailCode:"+email, code, 120, TimeUnit.SECONDS);

        return Result.succeed("发送成功");
    }

}
