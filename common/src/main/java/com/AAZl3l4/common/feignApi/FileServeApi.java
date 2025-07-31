package com.AAZl3l4.common.feignApi;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

// 文件服务接口
@FeignClient(value = "file-serve", fallbackFactory = FileServeApiFallbackFactory.class)
public interface FileServeApi {

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "使用文件服务的upload接口的feign客户端")
    String uploadFile(@RequestPart("file") MultipartFile file);
}