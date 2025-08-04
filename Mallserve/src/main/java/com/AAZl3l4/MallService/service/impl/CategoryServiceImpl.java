package com.AAZl3l4.MallService.service.impl;

import com.AAZl3l4.MallService.pojo.Category;
import com.AAZl3l4.MallService.mapper.CategoryMapper;
import com.AAZl3l4.MallService.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
