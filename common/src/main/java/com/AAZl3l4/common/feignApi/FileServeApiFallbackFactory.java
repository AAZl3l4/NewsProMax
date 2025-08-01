package com.AAZl3l4.common.feignApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileServeApiFallbackFactory implements FallbackFactory<FileServeApi> {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public FileServeApi create(Throwable cause) {
        return new FileServeApi() {
            @Override
            public String uploadFile(MultipartFile file) {
                log.error("调用 file-serve 服务失败，降级处理", cause);
                rabbitTemplate.convertAndSend("error","调用 file-serve 服务失败，降级处理"+ cause);
                throw new RuntimeException("服务降级：文件上传失败，请稍后重试", cause);
            }
        };
    }
}