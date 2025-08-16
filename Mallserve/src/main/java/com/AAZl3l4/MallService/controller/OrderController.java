package com.AAZl3l4.MallService.controller;


import com.AAZl3l4.MallService.pojo.*;
import com.AAZl3l4.MallService.service.ICartItemService;
import com.AAZl3l4.MallService.service.IOrderItemService;
import com.AAZl3l4.MallService.service.IOrderService;
import com.AAZl3l4.MallService.service.ProductService;
import com.AAZl3l4.MallService.utils.ExcelUtils;
import com.AAZl3l4.common.feignApi.UserServeApi;
import com.AAZl3l4.common.pojo.Address;
import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private UserServeApi userService;
    @Autowired
    private ICartItemService cartService;

    @PostMapping("/create")
    @Operation(summary = "添加订单")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result create(@RequestBody OrderItemDTO orders) {
        System.out.println(orders.getOrders());
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
            if (order1.getAmount() == null){
                order1.setAmount(order.getAmount());
            }else {
                order1.setAmount(order1.getAmount() + v);
            }
        }
        // 添加订单
        order1.setUserId(UserTool.getid());
        order1.setCreateTime(LocalDateTime.now());
        Address data = (Address) userService.getDefault(UserTool.getid()).getData();
        order1.setUserAddress(data.getAddress());
        order1.setUserPhone(data.getPhone());
        order1.setStatus('0');
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
            //清空购物车
            cartService.remove(new QueryWrapper<CartItem>().eq("user_id", UserTool.getid()));
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
            return Result.succeed("取消成功");
        } else {
            return Result.error("取消失败");
        }
    }

    @GetMapping("/Mlist")
    @Operation(summary = "查询本商家的订单")
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    public Result Mlist() {
        List<Product> list1 = productService.list(UserTool.getid());
        ArrayList<Order> orders = new ArrayList<>();
        for (Product product : list1) {
            List<OrderItem> productId = orderItemService.list(new QueryWrapper<OrderItem>().eq("product_id", product.getProductId()));
            for (OrderItem orderItem : productId) {
                Order order = orderService.getById(orderItem.getOrderId());
                if (order.getStatus() == ('1')) {
                    orders.add(order);
                }
            }
        }
        return Result.succeed(orders);
    }
    @Operation(summary = "发货")
    @PostMapping("/deliver")
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result deliver(Integer orderId) {
        Order order = orderService.getById(orderId);
        System.out.println(order);
        if (order.getStatus() == ('1')) {
            order.setStatus('2');
            order.setUpdateTime(LocalDateTime.now());
            return orderService.updateById(order) ? Result.succeed("发货成功") : Result.error("发货失败");
        } else {
            return Result.error("发货失败");
        }
    }

    @Operation(summary = "收货")
    @PostMapping("/receive")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result receive(Integer orderId) {
        Order order = orderService.getById(orderId);
        if (Objects.equals(order.getUserId(), UserTool.getid()) && order.getStatus() == ('2')) {
            order.setStatus('3');
            order.setUpdateTime(LocalDateTime.now());
            return orderService.updateById(order) ? Result.succeed("收货成功") : Result.error("收货失败");
        } else {
            return Result.error("收货失败");
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
        userById.setName(null);
        userById.setPassword(null);
        userById.setEmail(null);
        Result result = userService.updateUser(userById);
        System.out.println(result);
        if (result.getCode() == 200) {
            byId.setStatus('1');
            byId.setUpdateTime(LocalDateTime.now());
            return orderService.updateById(byId) ? Result.succeed("支付成功") : Result.error("支付失败");
        }else {
            return Result.error("支付失败");
        }
    }

    @GetMapping("/list")
    @Operation(summary = "查询本人订单")
    public Result<List<Order>> list() {
        return Result.succeed(orderService.list(new QueryWrapper<Order>().eq("user_id", UserTool.getid())));
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "查询订单详细")
    public Result info(@PathVariable("id")Long id) {
        OrderItem orderId = orderItemService.getOne(new QueryWrapper<OrderItem>().eq("order_id", id));
        return Result.succeed(orderId);
    }

    @GetMapping("/report")
    @Operation(summary = "导出报表")
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    public void report(Integer productId,HttpServletResponse response) throws IOException {
        Product byId = productService.findById(Long.valueOf(productId));
        if (byId.getMerchantId() != UserTool.getid()) {
            return;
        }
        List<OrderItem> product = orderItemService.list(new QueryWrapper<OrderItem>().eq("product_id", productId));
        ExcelUtils.exportOrderItem(product,response);
    }



}
