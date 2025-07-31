package com.AAZl3l3.NettyWSserve.controller;

import com.AAZl3l3.NettyWSserve.pojo.Message;
import com.AAZl3l3.NettyWSserve.service.MessageService;
import com.AAZl3l3.NettyWSserve.service.OnlineUserService;
import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/msg")
@RequiredArgsConstructor
public class PullController {
    private final MessageService messageService;
    private final OnlineUserService onlineUserService;

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
    | gid | String | 是 | 群 id | gid=groupA |
    | last | Long | 否 | 上一次最后一条消息的 id（分页游标，首次传 null/0） |
    | size | int | 是 | 本次拉取条数 |
    */
    public Result group(String gid, Long last, int size) {
        return Result.succeed(messageService.pullGroup(gid, last, size));
    }

    @GetMapping("/list")
    @Operation(summary = "获取在线用户列表")
    public Result list() {
        return Result.succeed(onlineUserService.getOnlineUsers());
    }
}