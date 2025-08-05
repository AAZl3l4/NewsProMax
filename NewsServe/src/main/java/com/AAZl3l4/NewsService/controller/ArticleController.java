package com.AAZl3l4.NewsService.controller;

import com.AAZl3l4.NewsService.pojo.Article;
import com.AAZl3l4.NewsService.pojo.ArticleSearchParam;
import com.AAZl3l4.NewsService.service.ArticleService;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService service;
    private final RedisTemplate redisTemplate;

    @PostMapping("/add")
    @Operation(summary = "新增文章")
    @PreAuthorize("hasAnyRole('UP')")
    public Result save(@RequestBody Article a) {
        a.setArticleId(IdWorker.getId());
        a.setAuthorId(UserTool.getid());
        a.setCreateTime(LocalDateTime.now());
        a.setUpdateTime(a.getCreateTime());
        Article save = service.save(a);
        return save == null ? Result.error("保存失败") : Result.succeed("保存成功");
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除文章")
    @PreAuthorize("hasAnyRole('UP')")
    public Result delete(@PathVariable Long id) {
        Article a = service.findById(id);
        if (a == null || !Objects.equals(a.getAuthorId(), UserTool.getid())) {
            return Result.error("无权限删除");
        }
        redisTemplate.delete("article:" + id);
        service.deleteById(id);
        return Result.succeed("删除成功");
    }

    @PostMapping("/update")
    @Operation(summary = "更新文章")
    @PreAuthorize("hasAnyRole('UP')")
    public Result update(@RequestBody Article a) {
        Article old = service.findById(a.getArticleId());
        if (old == null || !Objects.equals(old.getAuthorId(), UserTool.getid())) {
            return Result.error("无权限更新");
        }
        a.setUpdateTime(LocalDateTime.now());
        redisTemplate.delete("article:" + a.getArticleId());
        Article save = service.save(a);
        return save == null ? Result.error("更新失败") : Result.succeed("更新成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据主键查询")
    public Result find(@PathVariable Long id) {
        String key = "article:" + id;
        Object cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            return Result.succeed(cache);
        }
        Article a = service.findById(id);
        if (a == null) return Result.error("文章不存在");
        redisTemplate.opsForValue().set(key, a);
        return Result.succeed(a);
    }

    @GetMapping("/search")
    @Operation(summary = "全文搜索文章（时间/分类/作者/关键词/距离/置顶/高亮/分页）")
    public Result search(ArticleSearchParam param) throws IOException {
        // 避免 size 太大
        if (param.getSize() == null || param.getSize() <= 0 || param.getSize() > 100) {
            param.setSize(10);
        }
        if (param.getPage() == null || param.getPage() <= 0) {
            param.setPage(1);
        }
        return Result.succeed(service.search(param));
    }
}