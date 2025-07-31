package com.AAZl3l4.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// 用户工具类
public class UserTool {
    public static Integer getid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Integer.valueOf(authentication.getName());
    }
}
