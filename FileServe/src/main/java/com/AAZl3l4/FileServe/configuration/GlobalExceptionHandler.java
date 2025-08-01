package com.AAZl3l4.FileServe.configuration;


import com.AAZl3l4.FileServe.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 运行时异常处理
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException ex) {
//        log.error("全局异常处理器触发了 原因:{}", ex.getMessage(), ex);
        System.out.println("全局异常处理器触发了");
        // 抛出异常 让TM可以检测到
        throw ex;
//        return Result.error(ex.getMessage());
    }
}