package com.AAZl3l4.NewsService.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCR识别结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResult {

    /**
     * 识别出的文字内容
     */
    private String text;

    /**
     * 原始文件名
     */
    private String filename;

    /**
     * 文件大小(字节)
     */
    private Long size;

    /**
     * 处理耗时
     */
    private String processTime;
}
