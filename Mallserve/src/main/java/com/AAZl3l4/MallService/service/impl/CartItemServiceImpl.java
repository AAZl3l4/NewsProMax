package com.AAZl3l4.MallService.service.impl;

import com.AAZl3l4.MallService.mapper.CartItemMapper;
import com.AAZl3l4.MallService.pojo.CartItem;
import com.AAZl3l4.MallService.service.ICartItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements ICartItemService {
}