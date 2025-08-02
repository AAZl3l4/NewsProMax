package com.AAZl3l4.UserServe.controller;


import com.AAZl3l4.UserServe.pojo.RoleReview;
import com.AAZl3l4.UserServe.service.FaceService;
import com.AAZl3l4.UserServe.service.IRoleReviewService;
import com.AAZl3l4.UserServe.service.IUserService;
import com.AAZl3l4.UserServe.utils.JwtUtil;
import com.AAZl3l4.common.feignApi.FileServeApi;
import com.AAZl3l4.common.feignApi.UserServepi;
import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@Tag(name = "用户服务")
public class UserController implements UserServepi {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private FileServeApi fileServeApi;
    @Autowired
    private FaceService faceService;
    @Value("${Jwt.expiration}")
    private Long jwtExpiration;
    @Autowired
    private IRoleReviewService roleReviewService;

    @PostMapping("/login")
    //TODO 模拟登录
    public Result login(Integer userid) {
        //模拟用户 测试登录 返回token
        User user1 = new User();
        user1.setId(userid);
        if (userid == 1) {
            user1.setName("admin");
            user1.setRoles("ADMIN");
        }else{
            user1.setName("user");
            user1.setRoles("USER");
        }

        user1.setPassword(null);
        String s = jwtUtil.create(user1);
        redisTemplate.opsForValue().set("jwt:" + user1.getId(), s,jwtExpiration * 60000, TimeUnit.MILLISECONDS);
        return Result.succeed(s);
    }

//    @PostMapping("/login")
//    @Operation(summary = "用户登录")
//    public Result login(@RequestBody User user,
//                        @RequestParam("imgCode") String imgCode,
//                        @RequestParam("emailCode") String emailCode,
//                        @RequestHeader("uuid") String uuid,
//                        @RequestPart("imgBase64") String imgBase64) {
//        // 判断验证码和邮箱验证
//        //注意 code 是通过 ？的参数传递的
//        String iCode = (String) redisTemplate.opsForValue().get("imgCode:" + uuid);
//        if (iCode == null || !iCode.equals(imgCode)) {
//            return Result.error("验证码错误或已过期");
//        }
//        String eCode = (String) redisTemplate.opsForValue().get("emailCode:" + user.getEmail());
//        if (eCode == null || !eCode.equals(emailCode)) {
//            return Result.error("邮箱验证码错误或已过期");
//        }
//
//        //判断人脸图片是否存在
//        if (imgBase64 == null || imgBase64.isEmpty()) {
//            return Result.error("请上传人脸图片");
//        }
//
//        //通过邮箱查询用户 进行登录
//        User user1 = userService.getOne(new QueryWrapper<User>().eq("email", user.getEmail()));
//        if (user1 == null) {
//            return Result.error("用户不存在");
//        }
//        if (!user1.getPassword().equals(user.getPassword())) {
//            return Result.error("用户密码错误");
//        }
//
//        boolean b = faceService.compareWithUser(imgBase64, "public", String.valueOf(user1));
//        if (!b) {
//            return Result.error("人脸对比失败");
//        }
//        // 设置jwt 不携带密码
//        user1.setPassword(null);
//        String s = jwtUtil.create(user1);
//        // 保存jwt到redis
//        redisTemplate.opsForValue().set("jwt:" + user1.getId(), s,jwtExpiration * 60000, TimeUnit.MILLISECONDS);
//        return Result.succeed(s);
//    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result register(@RequestBody User user,
                           @RequestParam("imgCode") String imgCode,
                           @RequestParam("emailCode") String emailCode,
                           @RequestHeader("uuid") String uuid,
                           @RequestPart("imgBase64") String imgBase64) {
        // 判断验证码和邮箱验证
        //注意 code 是通过 ？的参数传递的
        String iCode = (String) redisTemplate.opsForValue().get("imgCode:" + uuid);
        if (iCode == null || !iCode.equals(imgCode)) {
            return Result.error("验证码错误或已过期");
        }
        String eCode = (String) redisTemplate.opsForValue().get("emailCode:" + user.getEmail());
        if (eCode == null || !eCode.equals(emailCode)) {
            return Result.error("邮箱验证码错误或已过期");
        }

        //判断人脸图片是否存在
        if (imgBase64 == null || imgBase64.isEmpty()) {
            return Result.error("请上传人脸图片");
        }

        // 查询邮箱或用户名是否已存在
        boolean exists = userService.exists(
                new QueryWrapper<User>()
                        .eq("email", user.getEmail())
                        .or()
                        .eq("username", user.getName())
        );
        if (exists) {
            return Result.error("邮箱或用户名已存在");
        }

        // 设置默认头像
        user.setAvatarUrl("http://192.168.188.188:9000/public/default.png");
        user.setCreateTime(LocalDateTime.now());

        if (userService.save(user)) {
            boolean b = faceService.registerFace(imgBase64, "public", String.valueOf(user.getId()));
            if (!b) {
                return Result.error("人脸注册失败");
            }
            return Result.succeed("注册成功");
        } else {
            return Result.error("注册失败");
        }
    }

    @GetMapping("/info")
    @Operation(summary = "通过id查询用户信息")
    public User getUserById() {
        Integer id = UserTool.getid();
        User user = userService.getById(id);
        // 不返回密码
        user.setPassword(null);
        user.setEmail(null);
        user.setMoney(null);
        user.setWxId(null);
        return user;
    }

    @PostMapping("/update")
    @Operation(summary = "更新用户信息")
    public Result updateUser(@RequestBody User user) {
        Integer id = UserTool.getid();
        user.setId(id);
        // 如果是修改邮箱或者用户名 需要判断是否已存在
        if (user.getEmail() != null || user.getName() != null) {
            boolean exists = userService.exists(
                    new QueryWrapper<User>()
                            .eq("email", user.getEmail())
                            .or()
                            .eq("username", user.getName())
            );
            if (exists) {
                return Result.error("邮箱或用户名已存在");
            }
        }

        if (userService.updateById(user)) {
            return Result.succeed("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @PostMapping("/updateAvatar")
    @Operation(summary = "修改头像")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result updateAvatar(@RequestPart("file") MultipartFile file) {
        String avatarUrl = fileServeApi.uploadFile(file);
        if (avatarUrl == null) {
            return Result.error("上传失败");
        }

        Integer id = UserTool.getid();
        User user = new User();
        user.setId(id);
        user.setAvatarUrl(avatarUrl);
        if (userService.updateById(user)) {
            return Result.succeed("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }

    @PostMapping("/updateFace")
    @Operation(summary = "更新用户人脸信息")
    public Result updateFace(String imgBase64) throws IOException {
        Integer getid = UserTool.getid();
        boolean b = faceService.updateFace(imgBase64, "public", String.valueOf(getid));
        if (b) {
            return Result.succeed("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }

    // 返回用户列表
    @GetMapping("/list")
    @Operation(summary = "返回用户列表")
    public Result<List<User>> list() {
        List<User> list = userService.list(new QueryWrapper<User>().select("id", "name", "avatar_url"));
        for (User user : list) {
            user.setPassword(null);
            user.setEmail(null);
            user.setMoney(null);
            user.setWxId(null);
        }
        return Result.succeed(list);
    }

    // 申请修改角色
    @PostMapping("/uprole")
    @Operation(summary = "申请修改角色")
    public Result upRole(String role) {
        Integer id = UserTool.getid();
        boolean save = roleReviewService.save(new RoleReview()
                .setUserid(id)
                .setRole(role)
                .setCreateTime(LocalDateTime.now())
        );
        if (save) {
            return Result.succeed("申请成功");
        } else {
            return Result.error("申请失败");
        }
    }
}
