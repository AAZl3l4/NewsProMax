package com.AAZl3l4.NewsService.controller;


import com.AAZl3l4.NewsService.pojo.NewsCategory;
import com.AAZl3l4.NewsService.service.INewsCategoryService;
import com.AAZl3l4.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/category")
public class NewsCategoryController {

    @Autowired
    private INewsCategoryService newsCategoryService;
    @GetMapping("/list")
    @Operation(summary = "查询所有分类")
    public Result list() {
        return Result.succeed(newsCategoryService.list());
    }

    @GetMapping("/add")
    @Operation(summary = "添加分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result add(@RequestBody NewsCategory newsCategory) {
        boolean save = newsCategoryService.save(newsCategory);
        if (!save) {
            return Result.error("添加失败");
        }else {
            return Result.succeed("添加成功");
        }
    }

    @GetMapping("/delete")
    @Operation(summary = "删除分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result delete(Integer id) {
        boolean del = newsCategoryService.removeById(id);
        if (!del) {
            return Result.error("添加失败");
        }else {
            return Result.succeed("添加成功");
        }
    }

}
