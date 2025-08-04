package com.AAZl3l4.MallService.controller;


import com.AAZl3l4.MallService.pojo.Category;
import com.AAZl3l4.MallService.service.ICategoryService;
import com.AAZl3l4.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "查询所有分类")
    public Result list() {
        return Result.succeed(categoryService.list());
    }

    @GetMapping("/add")
    @Operation(summary = "添加分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result add(Category category) {
        boolean save = categoryService.save(category);
        if (!save) {
            return Result.error("添加失败");
        }else{
            return Result.succeed("添加成功");
        }
    }

    @GetMapping("/delete")
    @Operation(summary = "删除分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result delete(Integer id) {
        boolean delete = categoryService.removeById(id);
        if (!delete) {
            return Result.error("删除失败");
        }else{
            return Result.succeed("删除成功");
        }
    }



}
