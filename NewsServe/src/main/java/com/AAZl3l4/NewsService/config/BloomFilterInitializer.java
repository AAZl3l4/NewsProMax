package com.AAZl3l4.NewsService.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.AAZl3l4.NewsService.pojo.Article;
import com.AAZl3l4.NewsService.utils.BloomFilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 布隆过滤器预热组件
 * 应用启动时从ES加载所有文章ID到布隆过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BloomFilterInitializer implements ApplicationRunner {

    private final ElasticsearchClient client;
    private final BloomFilterUtil bloomFilterUtil;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始预热布隆过滤器...");
        long startTime = System.currentTimeMillis();
        int count = 0;

        try {
            // 查询所有文章ID（最多10000条，如果文章更多需要分页）
            SearchResponse<Article> response = client.search(s -> s
                    .index("article")
                    .size(10000)
                    .source(src -> src.filter(f -> f.includes("articleId"))),
                    Article.class);

            for (Hit<Article> hit : response.hits().hits()) {
                Article article = hit.source();
                if (article != null && article.getArticleId() != null) {
                    bloomFilterUtil.put(String.valueOf(article.getArticleId()));
                    count++;
                }
            }

            long cost = System.currentTimeMillis() - startTime;
            log.info("布隆过滤器预热完成，共加载 {} 个文章ID，耗时 {} ms", count, cost);

        } catch (Exception e) {
            log.error("布隆过滤器预热失败", e);
        }
    }
}
