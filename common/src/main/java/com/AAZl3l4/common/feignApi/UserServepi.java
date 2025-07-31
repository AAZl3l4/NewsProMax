package com.AAZl3l4.common.feignApi;

import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// 用户服务接口
@FeignClient(value = "user-serve", fallbackFactory = UserServeApiFallbackFactory.class)
public interface UserServepi {

    @GetMapping("/list")
    @Operation(summary = "返回用户列表的feign客户端")
    public Result<List<User>> list();

}
