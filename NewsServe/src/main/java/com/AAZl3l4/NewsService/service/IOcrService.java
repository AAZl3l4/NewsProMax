package com.AAZl3l4.NewsService.service;

import com.AAZl3l4.NewsService.pojo.OcrResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * OCR图片识别服务接口
 */
public interface IOcrService {

    /**
     * 识别图片中的文字
     * @param imageFile 图片文件
     * @return 识别结果
     */
    OcrResult recognizeText(MultipartFile imageFile);
}
