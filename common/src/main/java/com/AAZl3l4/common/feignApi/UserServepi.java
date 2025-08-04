package com.AAZl3l4.common.feignApi;

import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

// 用户服务接口
@FeignClient(value = "user-serve", fallbackFactory = UserServeApiFallbackFactory.class)
public interface UserServepi {

    @GetMapping("/list")
    @Operation(summary = "返回用户列表的feign客户端")
    public Result<List<User>> list();

    @PostMapping("/getDefault")
    @Operation(summary = "查询默认地址")
    public Result getDefault(Integer userId);

    @GetMapping("/info")
    @Operation(summary = "通过id查询用户信息")
    public User getUserById(Integer getid);

    @PostMapping("/update")
    @Operation(summary = "更新用户信息")
    public Result updateUser(@RequestBody User user);
}
