package com.AAZl3l4.NewsService.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.LocalDateTime;

@Data
@Document(indexName = "article")  // 文章的es
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知字段
public class Article {

    @Id
    @Field(type = FieldType.Long)
    private Long articleId;

    private Integer authorId;

    private Integer categoryId;
    @Field(type = FieldType.Long)
    private Long productId;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Text,
           analyzer = "ik_pinyin_analyzer",
           searchAnalyzer = "ik_pinyin_analyzer")
    private String title;

    @Field(type = FieldType.Text,
           analyzer = "ik_pinyin_analyzer",
           searchAnalyzer = "ik_pinyin_analyzer")
    private String content;

    @Field(type = FieldType.Boolean)
    private Boolean isRecommended = false;

    @Field(type = FieldType.Long)
    private Long likeCount = 0L;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createTime;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updateTime;

    @Field(type = FieldType.Keyword, index = false)
    private String coverUrl;


}