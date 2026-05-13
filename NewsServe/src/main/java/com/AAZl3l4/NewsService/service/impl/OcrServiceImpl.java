package com.AAZl3l4.NewsService.service.impl;

import com.AAZl3l4.NewsService.pojo.OcrResult;
import com.AAZl3l4.NewsService.service.IOcrService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * OCR图片识别服务实现类
 */
@Slf4j
@Service
public class OcrServiceImpl implements IOcrService {

    @Value("${ocr.tessdata.path}")
    private String tessdataPath;

    @Value("${ocr.language}")
    private String language;

    private Tesseract tesseract;

    @PostConstruct
    public void init() {
        tesseract = new Tesseract();
        try {
            tesseract.setDatapath(tessdataPath);
            tesseract.setLanguage(language);
            tesseract.setPageSegMode(1);
            log.info("OCR服务初始化成功，tessdata路径: {}, 语言: {}", tessdataPath, language);
        } catch (Exception e) {
            log.error("OCR服务初始化失败: {}", e.getMessage());
            tesseract = null;
        }
    }

    @Override
    public OcrResult recognizeText(MultipartFile imageFile) {
        if (tesseract == null) {
            throw new RuntimeException("OCR服务未初始化，请检查tessdata配置");
        }

        try {
            BufferedImage image = ImageIO.read(imageFile.getInputStream());
            if (image == null) {
                throw new RuntimeException("无法读取图片文件，请确保上传的是有效的图片格式");
            }
            
            long startTime = System.currentTimeMillis();
            String text = tesseract.doOCR(image);
            long endTime = System.currentTimeMillis();
            
            String cleanedText = cleanText(text);
            
            return OcrResult.builder()
                    .text(cleanedText)
                    .filename(imageFile.getOriginalFilename())
                    .size(imageFile.getSize())
                    .processTime((endTime - startTime) + "ms")
                    .build();
        } catch (IOException e) {
            log.error("读取图片失败: {}", e.getMessage());
            throw new RuntimeException("读取图片失败: " + e.getMessage());
        } catch (TesseractException e) {
            log.error("OCR识别失败: {}", e.getMessage());
            throw new RuntimeException("OCR识别失败: " + e.getMessage());
        }
    }

    /**
     * 清理识别结果中的多余空白和特殊字符
     */
    private String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        text = text.replaceAll("[\\r\\n]+", "\n");
        text = text.replaceAll("[ \\t]+", " ");
        text = text.trim();
        return text;
    }
}
