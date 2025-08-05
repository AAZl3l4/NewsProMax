package com.AAZl3l4.NewsService.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.json.JsonData;
import com.AAZl3l4.NewsService.pojo.Article;
import com.AAZl3l4.NewsService.pojo.ArticleSearchParam;
import com.AAZl3l4.NewsService.pojo.ArticleSearchVo;
import com.AAZl3l4.NewsService.service.ArticleRepository;
import com.AAZl3l4.NewsService.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repo;
    private final ElasticsearchClient client;
    @Override public Article save(Article a) { return repo.save(a); }
    @Override public void deleteById(Long id) { repo.deleteById(id); }
    @Override public Article findById(Long id) { return repo.findById(id).orElse(null); }
    public  List<ArticleSearchVo> search(ArticleSearchParam p) throws IOException {

        BoolQuery.Builder bool = QueryBuilders.bool();

        // 1. 关键词（title + content）
        if (StringUtils.hasText(p.getKeyword())) {
            bool.must(m -> m
                    .multiMatch(mm -> mm
                            .fields("title", "content")
                            .query(p.getKeyword())));
        }

        // 2. 分类
        if (p.getCategoryId() != null) {
            bool.filter(f -> f.term(t -> t.field("categoryId").value(p.getCategoryId())));
        }

        // 3. 作者
        if (p.getAuthorId() != null) {
            bool.filter(f -> f.term(t -> t.field("authorId").value(p.getAuthorId())));
        }

        // 4. 时间区间
        if (p.getStart() != null || p.getEnd() != null) {
            bool.filter(f -> f.range(r -> r
                    .field("createTime")
                    .gte(p.getStart() == null ? null : JsonData.of(p.getStart()))
                    .lte(p.getEnd()   == null ? null : JsonData.of(p.getEnd()))));
        }

        // 5. 距离
        if (p.getLat() != null && p.getLon() != null && StringUtils.hasText(p.getDistance())) {
            bool.filter(f -> f
                    .geoDistance(gd -> gd
                            .field("location")
                            .distance(p.getDistance())
                            .location(GeoLocation.of(gl -> gl
                                    .latlon(LatLonGeoLocation.of(ll -> ll
                                            .lat(p.getLat())
                                            .lon(p.getLon())))))));
        }

        // 6. 置顶优先：把 isRecommended=true 排在前面
        List<SortOptions> sorts = new ArrayList<>();
        sorts.add(SortOptions.of(so -> so
                .field(f -> f.field("isRecommended").order(SortOrder.Desc))));
        // 其余可按 _score、时间等再排序
        sorts.add(SortOptions.of(so -> so
                .field(f -> f.field("createTime").order(SortOrder.Desc))));

        // 7. 高亮
        Highlight highlight = Highlight.of(h -> h
                .fields("title", hf -> hf)
                .fields("content", hf -> hf));

        SearchRequest request = SearchRequest.of(s -> s
                .index("article")
                .query(bool.build()._toQuery())
                .from((p.getPage() - 1) * p.getSize())
                .size(p.getSize())
                .sort(sorts)
                .highlight(highlight));

        SearchResponse<Article> resp = client.search(request, Article.class);
        List<ArticleSearchVo> list = resp.hits().hits().stream()
                .map(h -> {
                    ArticleSearchVo vo = new ArticleSearchVo();
                    BeanUtils.copyProperties(h.source(), vo);
                    vo.setHighlight(h.highlight());
                    return vo;
                })
                .toList();
        return list;
    }

}