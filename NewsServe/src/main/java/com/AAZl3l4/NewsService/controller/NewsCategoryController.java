package com.AAZl3l4.NewsService.controller;


import com.AAZl3l4.NewsService.pojo.NewsCategory;
import com.AAZl3l4.NewsService.service.INewsCategoryService;
import com.AAZl3l4.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/add")
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

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result delete(@PathVariable("id") Integer id) {
        boolean del = newsCategoryService.removeById(id);
        if (!del) {
            return Result.error("添加失败");
        }else {
            return Result.succeed("添加成功");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result update(@RequestBody NewsCategory newsCategory) {
        boolean update = newsCategoryService.updateById(newsCategory);
        if (!update) {
            return Result.error("更新失败");
        }else {
            return Result.succeed("更新成功");
        }
    }

}
