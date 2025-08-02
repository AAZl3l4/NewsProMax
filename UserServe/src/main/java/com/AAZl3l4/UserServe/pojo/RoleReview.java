package com.AAZl3l4.UserServe.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("role_review")
@Tag(name = "用户审核表")
public class RoleReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    @Schema(description = "用户id")
    private Integer userid;

    @Schema(description = "用户申请的角色")
    private String role;

    @Schema(description = "审核的状态 0：未审核 1审核通过 3审核不通过")
    private char status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "审核更新时间")
    private LocalDateTime updataTime;


}
