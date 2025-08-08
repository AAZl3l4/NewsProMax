package com.AAZl3l4.UserServe.controller;

import com.AAZl3l4.UserServe.service.IUserService;
import com.AAZl3l4.common.pojo.AopLog;
import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "管理员服务")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IUserService userService;

    @GetMapping("/info/{userid}")
    @Operation(summary = "管理员根据id获取用户的全部信息")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result getUserInfo(@PathVariable("userid") Integer userid) {
        return Result.succeed(userService.getById(userid));
    }

    @PostMapping("/updata")
    @Operation(summary = "管理员更新用户的信息")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @AopLog("管理员更新用户信息")
    public Result updataUser(@RequestBody User user) {
        QueryWrapper<User> eq = new QueryWrapper<User>()
                .eq("email", user.getEmail())
                .or()
                .eq("name", user.getName());
        if (userService.getOne(eq).getId() != user.getId()){
            return Result.error("邮箱或用户名已存在");
        }else {
            boolean b = userService.updateById(user);
            return b ? Result.succeed("更新成功") : Result.error("更新失败");
        }
    }

    @Operation(summary = "封禁/解封用户")
    @PostMapping("/ban")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @AopLog("封禁/解封用户")
    public Result banUser(Integer userid, char status) {
        User user = new User();
        user.setId(userid);
        user.setIsban(status);
        boolean b = userService.updateById(user);
        return b ? Result.succeed("操作成功") : Result.error("操作失败");
    }

}
