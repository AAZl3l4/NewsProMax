package com.AAZl3l4.UserServe.controller;

import com.AAZl3l4.UserServe.pojo.RoleReview;
import com.AAZl3l4.UserServe.service.IRoleReviewService;
import com.AAZl3l4.UserServe.utils.MailService;
import com.AAZl3l4.common.pojo.AopLog;
import com.AAZl3l4.common.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "角色审核服务")
@RequestMapping("/roleReview")
public class RoleReviewController {
    @Autowired
    private IRoleReviewService roleReviewService;
    @Autowired
    private MailService mailService;

    @GetMapping("/list")
    @Operation(summary = "获取所有审核信息 支持分页和筛选")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result reviewList(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "4") Integer status
    ){
        //如果状态是3就全查询
        if (status == 4) {
            return Result.succeed(roleReviewService.page(new Page<>(pageNum, pageSize)));
        }else{
            return Result.succeed(roleReviewService.page(new Page<>(pageNum, pageSize), new QueryWrapper<RoleReview>().eq("status", status)));
        }
    }

    @PostMapping("/review")
    @Operation(summary = "审核用户角色")
    @AopLog("审核用户角色")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GlobalTransactional(rollbackFor = Exception.class)
    public Result review(@RequestBody Map<String, Object> requestData){
        Integer id = (Integer) requestData.get("id");
        String string = (String) requestData.get("status");
        char status = string.charAt(0);
        RoleReview roleReview = roleReviewService.getById(id);
        if (roleReview == null) {
            return Result.error("审核信息不存在");
        }
        roleReview.setStatus(status);
        roleReviewService.updateById(roleReview);
        // 高危角色 发送邮件通知
        if (status == 1 && roleReview.getRole() == "ADMIN") {
            mailService.sendText("6110536@qq.com", "管理员角色", roleReview.getUserid() + "申请成为管理员 通过");
        }
        return Result.succeed("审核成功");
    }

}
