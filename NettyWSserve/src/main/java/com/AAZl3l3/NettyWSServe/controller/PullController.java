package com.AAZl3l3.NettyWSServe.controller;

import com.AAZl3l3.NettyWSServe.pojo.Message;
import com.AAZl3l3.NettyWSServe.service.MessageService;
import com.AAZl3l3.NettyWSServe.service.OnlineUserService;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/msg")
@RequiredArgsConstructor
public class PullController {
    private final MessageService messageService;
    private final OnlineUserService onlineUserService;
    private final RedisTemplate redisTemplate;

    @GetMapping("/single")
    @Operation(summary = "获取私聊聊天记录")
    /*
    | other | String | 是 | 对方用户 id |
    | last | Long | 否 | 上一次最后一条消息的 id（分页游标，首次传 null/0） |
    | size | int | 是 | 本次拉取条数 |
    */
    public Result single(String other, Long last, int size) {
        String me = String.valueOf(UserTool.getid());
        return Result.succeed(messageService.pullSingle(me, other, last, size));
    }

    @GetMapping("/group")
    @Operation(summary = "获取群聊聊天记录")
    /*
    | last | Long | 否 | 上一次最后一条消息的 id（分页游标，首次传 null/0） |
    | size | int | 是 | 本次拉取条数 |
    */
    public Result group(Long last, int size) {
        List<Message> messages = messageService.pullGroup(String.valueOf(1), last, size);
        return Result.succeed(messages);
    }

    @GetMapping("/list")
    @Operation(summary = "获取在线用户列表")
    public Result list() {

        return Result.succeed(onlineUserService.getOnlineUsers());
    }
}