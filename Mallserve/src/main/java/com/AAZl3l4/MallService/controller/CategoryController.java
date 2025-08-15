package com.AAZl3l4.MallService.controller;


import com.AAZl3l4.MallService.pojo.Category;
import com.AAZl3l4.MallService.service.ICategoryService;
import com.AAZl3l4.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/add")
    @Operation(summary = "添加分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result add(@RequestBody Category category) {
        boolean save = categoryService.save(category);
        if (!save) {
            return Result.error("添加失败");
        }else{
            return Result.succeed("添加成功");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "修改分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result update(@RequestBody Category category) {
        boolean update = categoryService.updateById(category);
        if (!update) {
            return Result.error("修改失败");
        }else{
            return Result.succeed("修改成功");
        }
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除分类")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result delete(@PathVariable("id") Integer id) {
        boolean delete = categoryService.removeById(id);
        if (!delete) {
            return Result.error("删除失败");
        }else{
            return Result.succeed("删除成功");
        }
    }



}
