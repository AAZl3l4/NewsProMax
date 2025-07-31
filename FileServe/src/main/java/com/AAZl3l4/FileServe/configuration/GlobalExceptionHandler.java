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
        return Result.error(ex.getMessage());
    }
}