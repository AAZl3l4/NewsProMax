package com.AAZl3l4.MallService.controller;

import com.AAZl3l4.MallService.pojo.CartItem;
import com.AAZl3l4.MallService.service.ICartItemService;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartItemService cartItemService;

    @PostMapping("/add")
    public Result add(@RequestBody CartItem cartItem) {
        CartItem one = cartItemService.getOne(new QueryWrapper<CartItem>().eq("user_id", UserTool.getid()).eq("product_id", cartItem.getProductId()));
        if (one  != null){
            //更新个数
            cartItem.setQuantity(cartItem.getQuantity() +one.getQuantity());
            return cartItemService.update(cartItem, new QueryWrapper<CartItem>().eq("user_id", UserTool.getid()).eq("product_id", cartItem.getProductId())) ? Result.succeed("添加成功") : Result.error("添加失败");
        }
        cartItem.setUserId(UserTool.getid());
        cartItem.setCreatedAt(LocalDateTime.now());
        return cartItemService.save(cartItem) ? Result.succeed("添加成功") : Result.error("添加失败");
    }

    @GetMapping("/list")
    public Result list() {
        Integer userId = UserTool.getid();
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Result.succeed(cartItemService.list(queryWrapper));
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return cartItemService.removeById(id) ? Result.succeed("删除成功") : Result.error("删除失败");
    }
}
