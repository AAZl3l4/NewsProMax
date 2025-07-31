package com.AAZl3l3.NettyWSserve.service;

import com.AAZl3l4.common.feignApi.UserServepi;
import com.AAZl3l4.common.pojo.User;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OnlineUserService {
    @Autowired
    private UserServepi userServepi;


    private final Map<String, Channel> uidChannelMap = new ConcurrentHashMap<>();

    public void put(String uid, Channel ch) { uidChannelMap.put(uid, ch); }

    public void remove(String uid) { uidChannelMap.remove(uid); }

    public Channel getChannel(String uid) { return uidChannelMap.get(uid); }

    public List<User> getGroupMembers() {
        //返回所有用户
        return userServepi.list().getData();
    }

    public List<User> getOnlineUsers() {
        //返回所有用户 过滤 只留下登录的
        List<User> list = userServepi.list().getData();
        return list.stream()
                .filter(u -> uidChannelMap.containsKey(String.valueOf(u.getId())))
                .collect(Collectors.toList());
    }
}