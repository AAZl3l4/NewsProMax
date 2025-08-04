package com.AAZl3l4.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@Schema(description="User对象")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户id")
    private Integer id;

    @Schema(description = "用户名")
    @NotEmpty
    @Size(min = 1, max = 12, message = "用户名长度在2-12之间")
    private String name;

    @Schema(description = "密码")
    @NotEmpty
    private String password;

    @Schema(description = "头像")
    private String avatarUrl;

    @Schema(description = "邮箱")
    @Email
    private String email;

    @Schema(description = "年龄")
    @NotNull
    private Integer age;

    @Schema(description = "性别 男或女")
    @NotEmpty
    private char sex;

    @Schema(description = "角色 ADMIN 管理员 USER用户 MERCHANT商家 UP UP主")
    @NotEmpty
    private String roles;

    @Schema(description = "是否封禁 0:正常 1:禁止")
    @NotEmpty
    private char isban;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "余额")
    private Double money;

    @Schema(description = "微信id")
    private String wxId;

}
