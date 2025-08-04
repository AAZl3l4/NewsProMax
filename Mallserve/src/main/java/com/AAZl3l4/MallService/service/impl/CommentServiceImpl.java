package com.AAZl3l4.MallService.service.impl;

import com.AAZl3l4.MallService.pojo.Comment;
import com.AAZl3l4.MallService.mapper.CommentMapper;
import com.AAZl3l4.MallService.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
