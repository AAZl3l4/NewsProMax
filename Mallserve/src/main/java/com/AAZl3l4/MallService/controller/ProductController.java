package com.AAZl3l4.MallService.controller;

import com.AAZl3l4.MallService.pojo.Product;
import com.AAZl3l4.MallService.service.ProductService;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;
    private final RedisTemplate redisTemplate;

    @PostMapping("/add")
    @Operation(summary = "新增")
    @PreAuthorize("hasAnyRole('MERCHANT')")
    public Result save(@RequestBody Product p) {
        p.setProductId(IdWorker.getId());
        if(service.findById(p.getProductId()) != null){
            return Result.error("商品已存在");
        }
        p.setMerchantId(UserTool.getid());
        Product save = service.save(p);
        if (save == null) {
            return Result.error("保存失败");
        }else {
            return Result.succeed("保存成功");
        }
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "根据主键删除")
    @PreAuthorize("hasAnyRole('MERCHANT')")
    public Result delete(@PathVariable Long id) {
        Product p = service.findById(id);
        if (!Objects.equals(p.getMerchantId(), UserTool.getid())){
            return Result.error("没有权限");
        }
        try {
            redisTemplate.delete("product:" + id);
            service.deleteById(id);
        }catch (Exception e) {
            return Result.error("删除失败");
        }
        return Result.error("删除成功");
    }

    @PostMapping("/update")
    @Operation(summary = "根据主键更新")
    @PreAuthorize("hasAnyRole('MERCHANT')")
    public Result update(@RequestBody Product p) {
        if (!Objects.equals(p.getMerchantId(), UserTool.getid())){
            return Result.error("没有权限");
        }
        redisTemplate.delete("product:" + p.getProductId());
        Product save = service.save(p);
        if (save == null) {
            return Result.error("保存失败");
        }else {
            return Result.succeed("保存成功");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据主键查询")
    public Result find(@PathVariable Long id) {
        if (redisTemplate.opsForValue().get("product:" + id) != null){
            return Result.succeed(redisTemplate.opsForValue().get("product:" + id));
        }else{
            Product byId = service.findById(id);
            redisTemplate.opsForValue().set("product:" + id, byId);
            return Result.succeed(byId);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "分页/高亮/分类/价格区间查询 支持条件拼接 包括查看全部商品")
    public Result highlightPage(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Float min,
            @RequestParam(required = false) Float max,
            @RequestParam(required = false) String key,   // 允许为空
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        float minPrice = min == null ? Float.MIN_VALUE : min;
        float maxPrice = max == null ? Float.MAX_VALUE : max;

        String keyword = key == null ? "" : key.trim();


        return Result.succeed(service.searchHighlightPage(
                category, minPrice, maxPrice, keyword,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "onShelfTime"))));

    }

}