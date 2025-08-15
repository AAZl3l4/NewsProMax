package com.AAZl3l4.MallService.service;

import com.AAZl3l4.MallService.pojo.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Product save(Product p);

    void deleteById(Long id);

    Product findById(Long id);

    List<Product> list(int userid);

    List<Product> searchHighlightPage(String category,
                                      Float min,
                                      Float max,
                                      String kw,
                                      Pageable pageable);

}
