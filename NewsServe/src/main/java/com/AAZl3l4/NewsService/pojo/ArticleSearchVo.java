package com.AAZl3l4.NewsService.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleSearchVo {
    private Long articleId;
    private Integer authorId;
    private Integer categoryId;
    private Integer productId;
    private GeoPoint location;
    private String title;
    private String content;
    private Boolean isRecommended;
    private Long likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String coverUrl;
    // 高亮字段
    private Map<String, List<String>> highlight;
}