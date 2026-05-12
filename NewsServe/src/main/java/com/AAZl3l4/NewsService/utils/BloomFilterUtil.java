package com.AAZl3l4.NewsService.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 布隆过滤器工具类
 * 用于解决缓存穿透问题
 */
@Slf4j
@Component
public class BloomFilterUtil {

    /**
     * 布隆过滤器实例
     * 预期插入数量: 100万
     * 误判率: 0.01%
     */
    private BloomFilter<String> bloomFilter;

    /**
     * 初始化布隆过滤器
     */
    @PostConstruct
    public void init() {
        // 预期插入1万条数据，误判率0.01%
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                10000,
                0.0001
        );
        log.info("布隆过滤器初始化完成");
    }

    /**
     * 添加元素到布隆过滤器
     *
     * @param key 元素key
     */
    public void put(String key) {
        if (key != null) {
            bloomFilter.put(key);
        }
    }

    /**
     * 批量添加元素到布隆过滤器
     *
     * @param keys 元素key列表
     */
    public void putAll(List<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                bloomFilter.put(key);
            }
            log.info("布隆过滤器批量添加 {} 个元素", keys.size());
        }
    }

    /**
     * 判断元素是否可能存在
     *
     * @param key 元素key
     * @return true-可能存在, false-一定不存在
     */
    public boolean mightContain(String key) {
        if (key == null) {
            return false;
        }
        return bloomFilter.mightContain(key);
    }

    /**
     * 判断元素是否一定不存在
     *
     * @param key 元素key
     * @return true-一定不存在, false-可能存在
     */
    public boolean definitelyNotContain(String key) {
        return !mightContain(key);
    }
}
