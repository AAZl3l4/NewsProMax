package com.AAZl3l4.MallService.controller;


import com.AAZl3l4.MallService.pojo.*;
import com.AAZl3l4.MallService.service.IOrderItemService;
import com.AAZl3l4.MallService.service.IOrderService;
import com.AAZl3l4.MallService.service.ProductService;
import com.AAZl3l4.MallService.utils.ExcelUtils;
import com.AAZl3l4.common.feignApi.UserServepi;
import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserServepi userService;

    @PostMapping("/create")
    @Operation(summary = "添加订单")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result create(@RequestBody OrderItemDTO orders) {
        Order order1 = new Order();
        // 计算总价 并设置订单详细信息
        for (OrderItem order : orders.getOrders()) {
            Long productId = order.getProductId();
            Product product = productService.findById(productId);
            if (product.getStock() < order.getQuantity()) {
                return Result.error("库存不足");
            }
            order.setProductName(product.getName());
            order.setProductImage(product.getImageUrl());
            order.setUnitPrice(Double.valueOf(product.getPrice()));
            double v = order.getUnitPrice() * order.getQuantity();
            order.setAmount(v);
            order1.setAmount(order1.getAmount() + v);
        }
        // 添加订单
        order1.setUserId(UserTool.getid());
        order1.setCreateTime(LocalDateTime.now());
        Address data = (Address) userService.getDefault(orders.getAddressId()).getData();
        order1.setUserAddress(data.getAddress());
        order1.setUserPhone(data.getPhone());
        boolean save1 = orderService.save(order1);
        if (save1) {
            //添加订单项
            for (OrderItem order : orders.getOrders()) {
                order.setOrderId(order1.getId());
            }
            boolean save2 = orderItemService.saveBatch(orders.getOrders());
            if (!save2) {
                return Result.error("添加订单项失败");
            }
            rabbitTemplate.convertAndSend(
                    "delay.order", "delay.order",
                    order1.getId(),
                    m -> {
                        m.getMessageProperties().setDelayLong(900000L); // 15分钟后后消费
                        return m;
                    }
            );
            return Result.succeed("下单成功");
        } else {
            return Result.error("下单失败");
        }
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消订单")
    public Result cancel(Integer orderId) {
        Order order = orderService.getById(orderId);
        if (order.getUserId() == UserTool.getid() && order.getStatus() == ('0')) {
            order.setStatus('4');
            orderService.updateById(order);
            orderItemService.remove(new QueryWrapper<OrderItem>().eq("orderId", orderId));
            return Result.succeed("取消成功");
        } else {
            return Result.error("取消失败");
        }
    }

    @PostMapping("/pay")
    @Operation(summary = "支付订单")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result pay(Integer orderId) {
        Order byId = orderService.getById(orderId);
        if (byId.getStatus() != '0') {
            return Result.error("订单已支付");
        }
        Double amount = byId.getAmount();
        User userById = userService.getUserById(UserTool.getid());
        if (userById.getMoney() < amount) {
            return Result.error("余额不足");
        }
        userById.setMoney(userById.getMoney() - amount);
        Result result = userService.updateUser(userById);
        if (result.getCode() == 200) {
            byId.setStatus('1');
            return orderService.updateById(byId) ? Result.succeed("支付成功") : Result.error("支付失败");
        }else {
            return Result.error("支付失败");
        }
    }

    @GetMapping("/list")
    @Operation(summary = "查询本人订单")
    public Result<List<Order>> list() {
        return Result.succeed(orderService.list(new QueryWrapper<Order>().eq("userId", UserTool.getid())));
    }

    @GetMapping("/info")
    @Operation(summary = "查询订单详细")
    public Result<Order> info(Long id) {
        Order byId = orderService.getById(id);
        if (!UserTool.getid().equals(byId.getUserId())) {
            return Result.error("无权限");
        }
        return Result.succeed(byId);
    }

    @GetMapping("/report")
    @Operation(summary = "导出报表")
//    @PreAuthorize("hasAnyRole('MERCHANT')")
    public void report(Integer productId,HttpServletResponse response) throws IOException {
        Product byId = productService.findById(Long.valueOf(productId));
//        if (byId.getMerchantId() != UserTool.getid()) {
//            return;
//        }
        List<OrderItem> product = orderItemService.list(new QueryWrapper<OrderItem>().eq("productId", productId));
        ExcelUtils.exportOrderItem(product,response);
    }



}
