package com.AAZl3l3.NettyWSserve.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    private Long id;
    private Integer msgType;   // 1 单聊 2 群聊
    private String fromUid;
    private String toUid;
    private String groupId;
    private String content;
    private LocalDateTime msgTime;
    private Integer delFlag;
}