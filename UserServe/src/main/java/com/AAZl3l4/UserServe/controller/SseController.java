package com.AAZl3l4.UserServe.controller;

import com.AAZl3l4.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/sse")
@Tag(name = "sse服务")
@Slf4j
public class SseController {
    // 存储sse连接(线程安全的集合)
    private final CopyOnWriteArrayList<SseEmitter> clients = new CopyOnWriteArrayList<>();

    // 订阅sse连接
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)  //声明返回的是SSE格式的文本流
    @Operation(summary = "订阅sse连接")
    public SseEmitter subscribe() {
        // 创建sse连接(指定过期时间为long最大值)
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        try {
            emitter.send("retry: 5000\n\n"); // 设置5秒自动重连(默认3秒) \n\n是SSE的事件结束符 表示一条指令结束
        } catch (IOException e) {
            e.printStackTrace();
        }        // 添加到列表里
        clients.add(emitter);
        //当连接完成/超时/出错时 自动从列表里移除 避免内存泄漏
        emitter.onCompletion(() -> clients.remove(emitter));
        emitter.onTimeout(() -> clients.remove(emitter));
        emitter.onError(e -> clients.remove(emitter));
        return emitter;
    }

    @PostMapping("/send")
    @Operation(summary = "发送消息")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result send(@RequestParam String message) {
        // 异步发送消息
        sendMessage(message);
        return Result.succeed("发送成功");
    }

    // 发送消息
    @Async("asyncExecutor")
    public void sendMessage(String message) {
        for (SseEmitter emitter : clients) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notice") // 指定事件名称
                        .data(message));
            } catch (IOException e) {
                log.error("发送sse消息失败", e);
                clients.remove(emitter);
            }
        }
    }
}