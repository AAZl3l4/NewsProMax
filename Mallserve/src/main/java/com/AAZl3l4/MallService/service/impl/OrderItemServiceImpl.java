package com.AAZl3l4.MallService.service.impl;

import com.AAZl3l4.MallService.mapper.OrderItemMapper;
import com.AAZl3l4.MallService.pojo.OrderItem;
import com.AAZl3l4.MallService.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;



@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
