package com.AAZl3l4.UserServe.controller;

import com.AAZl3l4.UserServe.configuration.AlipayConfig;
import com.AAZl3l4.UserServe.service.IUserService;
import com.AAZl3l4.common.pojo.AopLog;
import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.UserTool;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
@Tag(name = "支付宝付款服务")
public class AliPayController {

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private IUserService userService;

    //bean初始化加载
    @PostConstruct
    public void init() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = alipayConfig.getGatewayUrl();
        config.signType = "RSA2";
        config.appId = alipayConfig.getAppId();
        config.merchantPrivateKey = alipayConfig.getPrivateKey();
        config.alipayPublicKey = alipayConfig.getPublicKey();
        config.notifyUrl = alipayConfig.getNotifyUrl();

        Factory.setOptions(config);
    }

    // 创建支付
    @GetMapping("/create")
    @Operation(summary = "创建支付")
    @AopLog("创建支付宝支付")
    public String createPayment(@RequestParam String orderId, @RequestParam Double amount) {
        System.out.println(orderId);
        try {
            AlipayTradePagePayResponse alipayTradePagePayResponse = Factory.Payment.Page().pay(
                    "NewsProMax充值",  // 商品标题
                    orderId,     // 商户订单号
                    String.valueOf(amount), // 金额
                    alipayConfig.getReturnUrl()
            );
            return alipayTradePagePayResponse.getBody();
        } catch (Exception e) {
            throw new RuntimeException("支付接口调用失败", e);
        }
    }

    // 查询支付状态
    @GetMapping("/status/{orderId}")
    @Operation(summary = "查询支付状态")
    public String queryStatus(@PathVariable String orderId) {
        try {
            return Factory.Payment.Common().query(orderId).getHttpBody();
        } catch (Exception e) {
            return "查询失败：" + e.getMessage();
        }
    }

    // 异步通知处理
    @PostMapping("/callback")
    @Operation(summary = "异步通知处理")
    @AopLog("支付宝支付已经完成 异步通知处理")
    @GlobalTransactional(rollbackFor = Exception.class)
    public String handleCallback(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> params.put(key, values[0]));
        try {
            // 验证回调参数
            boolean verified = Factory.Payment.Common().verifyNotify(params);
            if (verified) {
                // 处理订单逻辑
                String orderId = params.get("out_trade_no"); // 商户订单号
                String tradeStatus = params.get("trade_status"); // 交易状态
                BigDecimal amount = new BigDecimal(params.get("total_amount")); // 订单金额
                // 从orderId中获取用户id
                String s = orderId.split("-")[1];
                // 判断用户id是否为空
                if (s == null || "".equals(s)) {
                    return "failure";
                }
                int userId = Integer.parseInt(s);
                // 判断用户id是否为空
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    // 使用户的积分加上去退款金额
                    User byId = userService.getById(userId);
                    BigDecimal currentMoney = BigDecimal.valueOf(byId.getMoney());
                    byId.setMoney(currentMoney.add(amount).doubleValue());
                    userService.updateById(byId);
                }
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    // 退款接口
    @PostMapping("/refund")
    @Operation(summary = "退款接口")
    @AopLog("支付宝支付退款")
    @GlobalTransactional(rollbackFor = Exception.class)
    public String refund(@RequestParam String orderId, @RequestParam Double refundAmount) {
        try {
            // 调用支付宝的退款接口
            String httpBody = Factory.Payment.Common().refund(orderId, String.valueOf(refundAmount)).getHttpBody();
            // 使用户的积分减去退款金额
            User byId = userService.getById(UserTool.getid());
            BigDecimal currentMoney = BigDecimal.valueOf(byId.getMoney());
            byId.setMoney(currentMoney.subtract(BigDecimal.valueOf(refundAmount)).doubleValue());
            userService.updateById(byId);
            return httpBody;
        } catch (Exception e) {
            return "退款失败：" + e.getMessage();
        }
    }

    // 下载对账单
    @Operation(summary = "下载对账单")
    @GetMapping("/download-bill")
    public String downloadBill(@RequestParam String billDate) {
        try {
            return Factory.Payment.Common().downloadBill("trade", billDate).getHttpBody();
        } catch (Exception e) {
            return "下载失败：" + e.getMessage();
        }
    }


}
