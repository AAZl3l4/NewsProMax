package com.AAZl3l4.NewsService.service.impl;

import com.AAZl3l4.NewsService.pojo.NewsContent;
import com.AAZl3l4.NewsService.mapper.NewsContentMapper;
import com.AAZl3l4.NewsService.service.INewsContentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class NewsContentServiceImpl extends ServiceImpl<NewsContentMapper, NewsContent> implements INewsContentService {

}
