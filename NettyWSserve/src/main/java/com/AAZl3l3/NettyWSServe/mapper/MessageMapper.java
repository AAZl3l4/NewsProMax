package com.AAZl3l3.NettyWSServe.mapper;

import com.AAZl3l3.NettyWSServe.pojo.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> { }