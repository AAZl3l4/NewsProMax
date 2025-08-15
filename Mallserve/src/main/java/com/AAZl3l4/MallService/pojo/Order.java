package com.AAZl3l4.MallService.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`order`")
@Tag(name="Order对象")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "0:下单 1:支付 2:发货 3:收货 4:退货")
    private char status;

    @NotNull
    private Double amount;

    @NotNull
    private Integer userId;

    @NotEmpty
    private String userAddress;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @NotEmpty
    private String userPhone;


}
