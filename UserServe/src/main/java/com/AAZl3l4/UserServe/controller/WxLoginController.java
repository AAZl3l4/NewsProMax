package com.AAZl3l4.UserServe.controller;

import com.AAZl3l4.common.pojo.AopLog;
import com.AAZl3l4.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController // REST 控制器
@RequestMapping("/wx") // 统一前缀 /wx
@RequiredArgsConstructor  // 构造器注入
@Slf4j // 日志
@Tag(name = "微信登录服务类") // Swagger 文档
public class WxLoginController {
    private final WxMpService wxMpService; // 微信 SDK 服务
    private final RedisTemplate<String, String> redis; // Redis操作
    private final WxMpMessageRouter router; // 路由

    /* 微信首次验证服务器有效性 */
    @GetMapping("/mp/callback")
    @Operation(summary = "微信验证")
    public void callback(@RequestParam String signature,
                         @RequestParam String timestamp,
                         @RequestParam String nonce,
                         @RequestParam String echostr,
                         HttpServletResponse response) throws IOException {
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) { // 签名校验
            log.error("非法请求"); //token配置错了 或是接收了非微信官方的请求
        }
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(echostr); // 必须原样返回 echostr
        response.getWriter().flush();
        response.getWriter().close();
    }

    /* 接收微信事件/消息 */
    @PostMapping("/mp/callback")
    @Operation(summary = "微信回调")
    public String receive(@RequestBody String xml,
                          @RequestParam String signature,
                          @RequestParam String timestamp,
                          @RequestParam String nonce) {
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) return "fail"; // 签名校验
        WxMpXmlMessage msg = WxMpXmlMessage.fromXml(xml); // 解析 XML
        return router.route(msg).toXml();  // 路由到 ScanHandler
    }

    /* 生成绑定二维码 */
    @GetMapping("/bind/qrcode")
    @Operation(summary = "生成绑定二维码")
    public Result bindQr(HttpServletRequest req) throws WxErrorException {
        String jwt = req.getHeader("Authorization").substring(7); // 取出 Bearer token
        String scene = "bind_" + jwt; // 场景值
        WxMpQrCodeTicket ticket = wxMpService.getQrcodeService()
                .qrCodeCreateTmpTicket(scene, 60 * 5);  // 5 分钟临时二维码
        String url = wxMpService.getQrcodeService().qrCodePictureUrl(ticket.getTicket()); // 图片地址
        return Result.succeed(url);
    }

    /* 生成登录二维码 */
    @GetMapping("/login/qrcode")
    @Operation(summary = "生成登录二维码")
    public Result loginQr() throws WxErrorException {
        String scene = UUID.randomUUID().toString();  // 随机场景值
        WxMpQrCodeTicket ticket = wxMpService.getQrcodeService()
                .qrCodeCreateTmpTicket(scene, 60 * 5); // 5 分钟临时二维码
        String url = wxMpService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());
        return Result.succeed(Map.of("scene", scene, "url", url)); // 返回场景值+地址
    }

    /* 前端轮询拿 JWT */
    @GetMapping("/login/token")
    @Operation(summary = "轮询拿 JWT")
    public Result token(@RequestParam String scene) {
        String token = redis.opsForValue().get("wx_login_token_" + scene); // 取 token
        if (token != null) {
            redis.delete("wx_login_token_" + scene); // 用完即删
            return Result.succeed(token); // 返回 JWT
        }
        return Result.error("waiting");  // 未扫码或已过期
    }
}