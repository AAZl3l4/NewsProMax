package com.AAZl3l3.NettyWSserve.mapper;

import com.AAZl3l3.NettyWSserve.pojo.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> { }