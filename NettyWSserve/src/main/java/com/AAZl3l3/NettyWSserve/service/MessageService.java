package com.AAZl3l3.NettyWSserve.service;

import com.AAZl3l3.NettyWSserve.mapper.MessageMapper;
import com.AAZl3l3.NettyWSserve.pojo.Message;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;


    /** 保存单聊消息 */
    public void saveSingle(String fromUid, String toUid, String content) {
        Message m = new Message();
        m.setMsgType(1);
        m.setFromUid(fromUid);
        m.setToUid(toUid);
        m.setContent(content);
        m.setMsgTime(LocalDateTime.now());
        messageMapper.insert(m);
    }

    /** 保存群聊消息 */
    public void saveGroup(String fromUid, String content) {
        Message m = new Message();
        m.setMsgType(2);
        m.setFromUid(fromUid);
        m.setGroupId(String.valueOf(1));
        m.setContent(content);
        m.setMsgTime(LocalDateTime.now());
        messageMapper.insert(m);
    }

    /** 离线拉取：单聊 */
    public List<Message> pullSingle(String uidA, String uidB, Long lastId, int size) {
        return messageMapper.selectList(
            new LambdaQueryWrapper<Message>()
                .eq(Message::getMsgType, 1)
                .and(q -> q.eq(Message::getFromUid, uidA).eq(Message::getToUid, uidB)
                         .or()
                         .eq(Message::getFromUid, uidB).eq(Message::getToUid, uidA))
                .gt(lastId != null, Message::getId, lastId)
                .orderByAsc(Message::getId)
                .last("limit " + size)
        );
    }

    /** 离线拉取：群聊 */
    public List<Message> pullGroup(String groupId, Long lastId, int size) {
        return messageMapper.selectList(
            new LambdaQueryWrapper<Message>()
                .eq(Message::getMsgType, 2)
                .eq(Message::getGroupId, groupId)
                .gt(lastId != null, Message::getId, lastId)
                .orderByAsc(Message::getId)
                .last("limit " + size)
        );
    }
}