package com.AAZl3l4.UserServe.controller;


import com.AAZl3l4.UserServe.service.FaceService;
import com.AAZl3l4.UserServe.service.impl.UserServiceImpl;
import com.AAZl3l4.common.feignApi.FileServeApi;
import com.AAZl3l4.common.pojo.AopLog;
import com.AAZl3l4.common.pojo.User;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

@RestController
public class Test {

    @Autowired
    private FileServeApi fileServeApi;
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/test")
    @AopLog("测试方法")
    public void test() throws IOException {

    }
}
