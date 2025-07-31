package com.AAZl3l3.NettyWSserve.pojo;

import lombok.Data;

@Data
public class MsgProtocol {
    private Integer type; // 1 单聊 2 群聊
    private String from;
    private String to;    // 目标用户ID 群聊是公共是无id
    private String body;  // 消息内容
}