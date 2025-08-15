package com.AAZl3l4.MallService.service;

import com.AAZl3l4.MallService.pojo.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {

    /* 有分类 + 关键字高亮 + 价格区间 + 分页 */
    @Query("{\"bool\":{" +
            "  \"must\":[" +
            "    {\"term\":{\"categoryId\":\"?0\"}}," +
            "    {\"range\":{\"price\":{\"gte\":?1,\"lte\":?2}}}," +
            "    {\"bool\":{\"should\":[" +
            "      {\"wildcard\":{\"name\":\"*?3*\"}}," +
            "      {\"wildcard\":{\"introduction\":\"*?3*\"}}" +
            "    ]}}" +
            "  ]" +
            "}}")
    @Highlight(
            fields = {
                    @HighlightField(name = "name"),
                    @HighlightField(name = "introduction")
            },
            parameters = @HighlightParameters(
                    preTags  = "<span style='color:red'>",
                    postTags = "</span>",
                    numberOfFragments = 0
            )
    )
    List<SearchHit<Product>> findByCategoryIdAndPriceBetweenAndNameContainingOrIntroductionContaining(
            String categoryId,
            Float minPrice,
            Float maxPrice,
            String keyword,
            Pageable pageable);

    /* 无分类，有关键字高亮 + 价格区间 + 分页 */
    @Query("{\"bool\":{" +
            "  \"must\":[" +
            "    {\"range\":{\"price\":{\"gte\":?0,\"lte\":?1}}}," +
            "    {\"bool\":{\"should\":[" +
            "      {\"wildcard\":{\"name\":\"*?2*\"}}," +
            "      {\"wildcard\":{\"introduction\":\"*?2*\"}}" +
            "    ]}}" +
            "  ]" +
            "}}")
    @Highlight(
            fields = {
                    @HighlightField(name = "name"),
                    @HighlightField(name = "introduction")
            },
            parameters = @HighlightParameters(
                    preTags  = "<span style='color:red'>",
                    postTags = "</span>",
                    numberOfFragments = 0
            )
    )
    List<SearchHit<Product>> findByPriceBetweenAndNameContainingOrIntroductionContaining(
            Float minPrice,
            Float maxPrice,
            String keyword,
            Pageable pageable);

    // 查询 MerchantId 为指定参数的所有商品
    @Query("{\"term\": {\"MerchantId\": \"?0\"}}")
    List<Product> findAllByMerchantId(Integer merchantId);
}