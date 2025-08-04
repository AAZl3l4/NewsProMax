package com.AAZl3l4.MallService.service.impl;

import com.AAZl3l4.MallService.pojo.Product;
import com.AAZl3l4.MallService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    @Override
    public Product save(Product p) {
        return repo.save(p);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(String.valueOf(id));
    }

    @Override
    public Product findById(Long id) {
        return repo.findById(String.valueOf(id)).orElse(null);
    }

    @Override
    public List<Product> searchHighlightPage(String category,
                                             Float min,
                                             Float max,
                                             String kw,
                                             Pageable pageable) {
        // 版本兼容有问题 返回page对象没有高亮信息的值
        if (category == null || category.isEmpty()) {
            return toProductList(repo.findByPriceBetweenAndNameContainingOrIntroductionContaining(
                    min, max, kw, pageable));
        } else {
            return toProductList(repo.findByCategoryIdAndPriceBetweenAndNameContainingOrIntroductionContaining(
                    category, min, max, kw, pageable));
        }
    }

    public static List<Product> toProductList(List<SearchHit<Product>> hits) {
        return hits.stream()
                .map(hit -> {
                    Product origin = hit.getContent();

                    // 深拷贝/新建，防止修改原对象
                    Product p = new Product();
                    p.setProductId(origin.getProductId());
                    p.setMerchantId(origin.getMerchantId());
                    p.setPrice(origin.getPrice());
                    p.setStock(origin.getStock());
                    p.setCategoryId(origin.getCategoryId());
                    p.setOnShelfTime(origin.getOnShelfTime());
                    p.setImageUrl(origin.getImageUrl());

                    // 处理高亮
                    if (hit.getHighlightFields() != null) {
                        List<String> hlName = hit.getHighlightFields().get("name");
                        if (hlName != null && !hlName.isEmpty()) {
                            p.setName(String.join("", hlName));   // 多片段合并
                        } else {
                            p.setName(origin.getName());
                        }

                        List<String> hlIntro = hit.getHighlightFields().get("introduction");
                        if (hlIntro != null && !hlIntro.isEmpty()) {
                            p.setIntroduction(String.join("", hlIntro));
                        } else {
                            p.setIntroduction(origin.getIntroduction());
                        }
                    } else {
                        p.setName(origin.getName());
                        p.setIntroduction(origin.getIntroduction());
                    }
                    return p;
                })
                .collect(Collectors.toList());
    }


}