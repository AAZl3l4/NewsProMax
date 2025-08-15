package com.AAZl3l4.MallService.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cart_item")
@Tag(name="购物车对象")
public class CartItem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
}
