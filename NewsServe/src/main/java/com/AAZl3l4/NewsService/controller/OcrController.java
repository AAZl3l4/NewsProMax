package com.AAZl3l4.NewsService.controller;

import com.AAZl3l4.NewsService.pojo.OcrResult;
import com.AAZl3l4.NewsService.service.IOcrService;
import com.AAZl3l4.common.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * OCR图片识别控制器
 */
@Slf4j
@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final IOcrService ocrService;

    /**
     * 识别图片中的文字
     * @param file 图片文件
     * @return 识别结果
     */
    @PostMapping("/recognize")
    public Result<OcrResult> recognizeText(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的图片文件");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !isImageFile(filename)) {
            return Result.error("请上传有效的图片文件（支持jpg、png、bmp、gif格式）");
        }

        try {
            OcrResult result = ocrService.recognizeText(file);
            log.info("OCR识别成功，文件: {}, 耗时: {}", filename, result.getProcessTime());
            return Result.succeed(result);
        } catch (Exception e) {
            log.error("OCR识别失败: {}", e.getMessage());
            return Result.error("识别失败: " + e.getMessage());
        }
    }

    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return extension.matches("jpg|jpeg|png|bmp|gif|tiff|webp");
    }
}
