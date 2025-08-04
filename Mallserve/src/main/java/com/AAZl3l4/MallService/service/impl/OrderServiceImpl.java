package com.AAZl3l4.MallService.service.impl;

import com.AAZl3l4.MallService.mapper.OrderMapper;
import com.AAZl3l4.MallService.pojo.Order;
import com.AAZl3l4.MallService.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;



@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
