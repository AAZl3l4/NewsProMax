package com.AAZl3l4.NewsService.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.AAZl3l4.NewsService.pojo.Article;
import com.AAZl3l4.NewsService.pojo.ArticleSearchParam;
import com.AAZl3l4.NewsService.pojo.ArticleSearchVo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface ArticleService {
    Article save(Article a);
    void deleteById(Long id);
    Article findById(Long id);
    List<ArticleSearchVo> search(ArticleSearchParam p) throws IOException;
}