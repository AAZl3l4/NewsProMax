package com.AAZl3l4.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 统一响应实体工具类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> succeed(T data) {
        return new Result(200, "success", data);
    }
    public static Result error(String msg) {
        return new Result(500, msg, null);
    }
}
