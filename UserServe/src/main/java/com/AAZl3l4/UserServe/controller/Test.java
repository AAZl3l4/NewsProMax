package com.AAZl3l4.UserServe.controller;


import com.AAZl3l4.UserServe.service.FaceService;
import com.AAZl3l4.common.pojo.AopLog;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

@RestController
public class Test {

    @Autowired
    private FaceService faceService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/test")
    @AopLog("测试方法")
    public String test() throws IOException {
        rabbitTemplate.convertAndSend(
                "delay.direct",
                "delay",
                "你好",
                m -> {
                    m.getMessageProperties().setDelayLong(5000L); // 5 秒后投递
                    return m;
                }
        );

        return "tset";
    }
}
