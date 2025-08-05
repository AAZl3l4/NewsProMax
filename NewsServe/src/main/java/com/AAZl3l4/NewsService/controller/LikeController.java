package com.AAZl3l4.NewsService.controller;


import com.AAZl3l4.NewsService.pojo.Article;
import com.AAZl3l4.NewsService.pojo.Like;
import com.AAZl3l4.NewsService.pojo.LikeDTO;
import com.AAZl3l4.NewsService.service.ArticleService;
import com.AAZl3l4.NewsService.service.ILikeService;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/like")
public class LikeController {
    @Autowired
    private ILikeService likeService;
    @Autowired
    private ArticleService newsService;

    @PostMapping("/add")
    @Operation(summary = "添加/取消点赞点赞")
    public Result add(@RequestBody Like like) {
        like.setUserId(UserTool.getid());
        //检测是否已点赞
        QueryWrapper<Like> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", like.getUserId());
        wrapper.eq("news_id", like.getNewsId());
        boolean exists = likeService.exists(wrapper);
        if (exists) {
            likeService.remove(wrapper);
            // 取消点赞时，减少文章的点赞数
            Article article = newsService.findById(like.getNewsId());
            if (article != null) {
                article.setLikeCount(article.getLikeCount() - 1);
                newsService.save(article);
            }
            return Result.succeed("取消点赞成功");
        } else {
            likeService.save(like);
            // 点赞时，增加文章的点赞数
            Article article = newsService.findById(like.getNewsId());
            if (article != null) {
                article.setLikeCount(article.getLikeCount() + 1);
                newsService.save(article);
            }
            return Result.succeed("点赞成功");
        }
    }

    @PostMapping("/list")
    @Operation(summary = "分页查询当前用户所有点赞")
    public Result list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        QueryWrapper<Like> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", UserTool.getid());
        Page<Like> page1 = likeService.page(new Page<>(page, size), wrapper);

        // 填充点赞的文章的信息
        List<LikeDTO> likeDTOS = page1.getRecords().stream().map(like -> {
            LikeDTO likeDTO = new LikeDTO();
            BeanUtils.copyProperties(like, likeDTO);

            // 根据 newsId 查询文章信息
            Article article = newsService.findById(like.getNewsId());
            likeDTO.setArticle(article);
            return likeDTO;
        }).collect(Collectors.toList());

        Page<LikeDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(page1, resultPage, "records");
        resultPage.setRecords(likeDTOS);

        return Result.succeed(resultPage);
    }



}
