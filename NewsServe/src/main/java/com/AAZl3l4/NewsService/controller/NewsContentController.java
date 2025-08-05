package com.AAZl3l4.NewsService.controller;


import com.AAZl3l4.NewsService.pojo.NewsContent;
import com.AAZl3l4.NewsService.pojo.NewsContentDTO;
import com.AAZl3l4.NewsService.service.INewsContentService;
import com.AAZl3l4.common.feignApi.UserServeApi;
import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
@RequestMapping("/content")
public class NewsContentController {

    @Autowired
    private INewsContentService commentService;
    @Autowired
    private UserServeApi userServeApi;

    @GetMapping("/list/{id}")
    @Operation(summary = "分页查询当前文章的所有评论")
    public Result list(@PathVariable Integer id,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer size) {
        QueryWrapper<NewsContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("news_id", id);
        Page<NewsContent> page1 = commentService.page(new Page<>(page, size), queryWrapper);
        //将NewsContent转换为NewsContentDTO 携带用户信息
        Page<NewsContentDTO> page2 = new Page<>();
        BeanUtils.copyProperties(page1,page2);
        //遍历设置NewsContentDTO(浅拷贝只拷贝的page的信息 没有records)
        page2.setRecords(page1.getRecords().stream().map(comment -> {
            NewsContentDTO commentDTO = new NewsContentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            Integer userid = comment.getUserId();
            User userById = userServeApi.getUserById(userid);
            commentDTO.setUsername(userById.getName());
            commentDTO.setAvatar(userById.getAvatarUrl());
            return commentDTO;
        }).toList());
        return Result.succeed(page2);
    }

    @PostMapping("/add")
    @Operation(summary = "添加评论")
    public Result add(@RequestBody NewsContent comment) {
        comment.setUserId(UserTool.getid());
        return commentService.save(comment) ? Result.succeed("添加成功") : Result.error("添加失败");
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除评论")
    public Result delete(@PathVariable Integer id) {
        NewsContent comment = commentService.getById(id);
        if (!Objects.equals(comment.getNewsId(), UserTool.getid())){
            return Result.error("没有权限");
        }
        return commentService.removeById(id) ? Result.succeed("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/addelete/{id}")
    @Operation(summary = "管理员删除评论")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result addelete(@PathVariable Integer id) {
        return commentService.removeById(id) ? Result.succeed("删除成功") : Result.error("删除失败");
    }


}
