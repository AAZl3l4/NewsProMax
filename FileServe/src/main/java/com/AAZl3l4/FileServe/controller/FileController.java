package com.AAZl3l4.FileServe.controller;

import com.AAZl3l4.FileServe.utils.MinioUtil;
import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@RestController
@Tag(name = "文件服务")
public class FileController {

    @Autowired
    private MinioUtil minioUtil;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    // 保存文件到minio
    private String saveFile(@RequestPart("file") MultipartFile file) {
        System.out.println("开始上传文件");
        System.out.println("文件名：" + file.getOriginalFilename());
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return "文件名获取失败";
        }
        String fileName = "";
        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            fileName = originalFilename.substring(0, lastDotIndex);
            fileExtension = originalFilename.substring(lastDotIndex);
        }
        String randomFilename = UUID.randomUUID().toString().replace("-", "") + "--" + fileName + fileExtension;
        return minioUtil.uploadFile(randomFilename, file);
    }

}

