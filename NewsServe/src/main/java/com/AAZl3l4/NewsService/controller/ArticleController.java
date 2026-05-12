package com.AAZl3l4.NewsService.controller;

import com.AAZl3l4.NewsService.pojo.Article;
import com.AAZl3l4.NewsService.pojo.ArticleSearchParam;
import com.AAZl3l4.NewsService.service.ArticleService;
import com.AAZl3l4.NewsService.utils.BloomFilterUtil;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.SensitiveWordUtil;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService service;
    private final RedisTemplate redisTemplate;
    private final BloomFilterUtil bloomFilterUtil;

    @PostMapping("/add")
    @Operation(summary = "新增文章")
    @PreAuthorize("hasAnyRole('UP','ADMIN')")
    public Result save(@RequestBody Article a) {
        a.setArticleId(IdWorker.getId());
        a.setAuthorId(UserTool.getid());
        a.setCreateTime(LocalDateTime.now());
        a.setUpdateTime(a.getCreateTime());
        if (a.getContent().equals("<script>"))return Result.error("包含js语句");
        // 敏感词过滤 - 替换标题和内容中的敏感词
        a.setTitle(SensitiveWordUtil.replaceSensitiveWords(a.getTitle()));
        a.setContent(SensitiveWordUtil.replaceSensitiveWords(a.getContent()));
        Article save = service.save(a);
        if (save != null) {
            // 添加到布隆过滤器
            bloomFilterUtil.put(String.valueOf(save.getArticleId()));
            return Result.succeed("保存成功");
        }
        return Result.error("保存失败");
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除文章")
    @PreAuthorize("hasAnyRole('UP','ADMIN')")
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
    @PreAuthorize("hasAnyRole('UP','ADMIN')")
    public Result update(@RequestBody Article a) {
        if (a.getContent().equals("<script>"))return Result.error("包含js语句");
        Article old = service.findById(a.getArticleId());
        if (old == null || !Objects.equals(old.getAuthorId(), UserTool.getid())) {
            return Result.error("无权限更新");
        }
        //把为null的重新赋值原来的
        BeanUtils.copyProperties(a, old,
                // 不覆盖的字段：主键、创建时间、作者 id
                "articleId", "createTime", "authorId");
        old.setUpdateTime(LocalDateTime.now());
        // 敏感词过滤 - 替换标题和内容中的敏感词
        old.setTitle(SensitiveWordUtil.replaceSensitiveWords(old.getTitle()));
        old.setContent(SensitiveWordUtil.replaceSensitiveWords(old.getContent()));
        redisTemplate.delete("article:" + old.getArticleId());
        Article save = service.save(old);
        return save == null ? Result.error("更新失败") : Result.succeed("更新成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据主键查询")
    public Result find(@PathVariable Long id) {
        // 布隆过滤器判断文章是否可能存在，不存在则直接返回
        if (bloomFilterUtil.definitelyNotContain(String.valueOf(id))) {
            log.info("布隆过滤器判断 文章不存在");
            return Result.error("文章不存在");
        }
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

    @PostMapping("/search")
    @Operation(summary = "全文搜索文章（时间/分类/作者/关键词/距离/置顶/高亮/分页）")
    public Result search(@RequestBody ArticleSearchParam param) throws IOException {
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