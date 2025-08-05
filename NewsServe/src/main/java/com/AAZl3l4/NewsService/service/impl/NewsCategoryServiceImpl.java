package com.AAZl3l4.NewsService.service.impl;

import com.AAZl3l4.NewsService.pojo.NewsCategory;
import com.AAZl3l4.NewsService.mapper.NewsCategoryMapper;
import com.AAZl3l4.NewsService.service.INewsCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NewsCategoryServiceImpl extends ServiceImpl<NewsCategoryMapper, NewsCategory> implements INewsCategoryService {

}
