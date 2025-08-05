package com.AAZl3l4.NewsService.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleSearchParam {
    private String keyword;          // 全文关键词
    private Integer categoryId;      // 分类
    private Integer authorId;        // 作者
    private LocalDateTime start;     // 时间区间
    private LocalDateTime end;
    private Double lat;              // 中心点坐标
    private Double lon;
    private String distance;         // 距离表达式 如 "10km"
    private Integer page = 1;        // 分页
    private Integer size = 10;
}