package com.AAZl3l4.FileServe;


import com.AAZl3l4.FileServe.utils.MinioUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
class SpringbootApplicationTests {

    @Autowired
    MinioUtil minioUtil;
    @Test
    void contextLoads() {
// 本地文件路径
        String localPath = "D:\\下载\\default.png";
        File file = new File(localPath);

        if (!file.exists()) {
            throw new RuntimeException("本地文件不存在：" + localPath);
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            // 构造 MultipartFile
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    "default.png",
                    "image/png",
                    inputStream
            );

            // 上传到 MinIO
            String resultUrl = minioUtil.uploadFile("default.png", multipartFile);
            System.out.println("上传成功，访问地址为：" + resultUrl);

        } catch (IOException e) {
            throw new RuntimeException("测试上传失败", e);
        }
    }

}
