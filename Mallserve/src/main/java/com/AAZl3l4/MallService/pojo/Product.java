package com.AAZl3l4.MallService.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import lombok.Data;

@Data
@Document(indexName = "product")   // 商品的es
public class Product {

    @Id
    private Long productId;

    @Field(type = FieldType.Integer)
    private Integer MerchantId;

    @Field(type = FieldType.Text,
           analyzer = "ik_pinyin_analyzer",
           searchAnalyzer = "ik_pinyin_analyzer")
    private String name;

    @Field(type = FieldType.Float)
    private Float price;

    @Field(type = FieldType.Integer)
    private Integer stock;

    @Field(type = FieldType.Integer)
    private Integer categoryId;

    @Field(type = FieldType.Text,
           analyzer = "ik_pinyin_analyzer",
           searchAnalyzer = "ik_pinyin_analyzer")
    private String introduction;

    @Field(type = FieldType.Date)
    private String onShelfTime;

    @Field(type = FieldType.Keyword)
    private String imageUrl;
}