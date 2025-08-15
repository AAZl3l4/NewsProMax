package com.AAZl3l4.UserServe.controller;


import com.AAZl3l4.common.pojo.Address;
import com.AAZl3l4.UserServe.pojo.RoleReview;
import com.AAZl3l4.UserServe.pojo.UserDTO;
import com.AAZl3l4.UserServe.service.FaceService;
import com.AAZl3l4.UserServe.service.IAddressService;
import com.AAZl3l4.UserServe.service.IRoleReviewService;
import com.AAZl3l4.UserServe.service.IUserService;
import com.AAZl3l4.UserServe.utils.JwtUtil;
import com.AAZl3l4.common.feignApi.FileServeApi;
import com.AAZl3l4.common.feignApi.UserServeApi;
import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserController implements UserServeApi {

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
    @Autowired
    private IAddressService addressService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result login(@RequestBody UserDTO userDTO,
                        @RequestHeader("uuid") String uuid) {
        String imgCode = userDTO.getImgCode();
        String emailCode = userDTO.getEmailCode();
        String imgBase64 = userDTO.getImgBase64();
        User user = userDTO.getUser();
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

        //通过邮箱查询用户 进行登录
        User user1 = userService.getOne(new QueryWrapper<User>().eq("email", user.getEmail()));
        if (user1 == null) {
            return Result.error("用户不存在");
        }
        if (!user1.getPassword().equals(user.getPassword())) {
            return Result.error("用户密码错误");
        }
        if (user1.getIsban()=='1') {
            return Result.error("用户被封禁");
        }
        boolean b = faceService.compareWithUser(imgBase64, "public", String.valueOf(user1.getId()));
        if (!b) {
            return Result.error("人脸对比失败");
        }
        // 设置jwt 不携带密码
        user1.setPassword(null);
        String s = jwtUtil.create(user1);
        // 保存jwt到redis
        redisTemplate.opsForValue().set("jwt:" + user1.getId(), s,jwtExpiration * 60000, TimeUnit.MILLISECONDS);
        return Result.succeed(s);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result register(@RequestBody UserDTO userDTO,
                           @RequestHeader("uuid") String uuid) {
        // 判断验证码和邮箱验证
        //注意 code 是通过 ？的参数传递的
        String imgCode = userDTO.getImgCode().trim();
        String emailCode = userDTO.getEmailCode().trim();
        String imgBase64 = userDTO.getImgBase64();
        User user = userDTO.getUser();

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
                        .eq("name", user.getName())
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
    public User getUserById(@RequestParam(required = false) Integer id) {
        if (id == null|| id == 0) {
            id = UserTool.getid();
        }
        User user = userService.getById(id);
        // 不返回密码
        user.setPassword(null);
        user.setEmail(null);
        user.setWxId(null);
        return user;
    }

    @Operation(summary = "退出登录")
    @GetMapping("/logout")
    public Result logout() {
        Integer id = UserTool.getid();
        redisTemplate.delete("jwt:" + id);
        return Result.succeed("退出成功");
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
                            .eq("name", user.getName())
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
        // 去除""
        avatarUrl = avatarUrl.substring(1, avatarUrl.length() - 1);
        if (avatarUrl == null) {
            return Result.error("上传失败");
        }

        Integer id = UserTool.getid();
        User user = new User();
        user.setId(id);
        user.setAvatarUrl(avatarUrl);
        if (userService.updateById(user)) {
            return Result.succeed(user);
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
    public Result upRole(@RequestBody String role) {
        //将=替换
        role = role.replace("=", "");
        Integer id = UserTool.getid();
        boolean save = roleReviewService.save(new RoleReview()
                .setUserid(id)
                .setRole(role)
                .setStatus('0')
                .setCreateTime(LocalDateTime.now())
        );
        if (save) {
            return Result.succeed("申请成功");
        } else {
            return Result.error("申请失败");
        }
    }

    @GetMapping("/getDefault")
    @Operation(summary = "查询默认地址")
    public Result<Address> getDefault(Integer userId) {
        Address byId = addressService.getOne(new QueryWrapper<Address>().eq("user_id", userId).eq("is_default", '1'));
        return Result.succeed(byId);
    }
}
