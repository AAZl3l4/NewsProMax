package com.AAZl3l4.NewsService.service;


import com.AAZl3l4.NewsService.pojo.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ArticleRepository extends ElasticsearchRepository<Article, Long> {

    /* 统一查询：时间区间 + 分类 + 作者 + 关键词 + 距离 + 置顶优先 + 高亮 + 分页 */
}
