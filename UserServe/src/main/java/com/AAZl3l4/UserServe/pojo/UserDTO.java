package com.AAZl3l4.UserServe.pojo;

import com.AAZl3l4.common.pojo.User;
import lombok.Data;

@Data
public class UserDTO {
    private User user;          // JSON 部分
    private String imgCode;
    private String emailCode;
    private String uuid;
    private String imgBase64;   // base64 字符串，无需 MultipartFile
}