package com.AAZl3l4.MallService.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_item")
@Tag(name="OrderItem对象")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotNull
    private Integer orderId;

    @NotNull
    private Long productId;

    @NotEmpty
    private String productName;

    @NotEmpty
    private String productImage;

    @NotNull
    private Double unitPrice;

    @NotEmpty
    private Integer quantity;

    @NotNull
    private Double amount;


}
