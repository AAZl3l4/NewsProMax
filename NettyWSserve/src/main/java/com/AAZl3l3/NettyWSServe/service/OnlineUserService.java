package com.AAZl3l3.NettyWSServe.service;

import com.AAZl3l4.common.feignApi.UserServeApi;
import com.AAZl3l4.common.pojo.User;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OnlineUserService {
    @Autowired
    private UserServeApi userServepi;
    @Autowired
    private RedisTemplate redisTemplate;


    private final Map<String, Channel> uidChannelMap = new ConcurrentHashMap<>();

    public void put(String uid, Channel ch) { uidChannelMap.put(uid, ch); }

    public void remove(String uid) { uidChannelMap.remove(uid); }

    public Channel getChannel(String uid) { return uidChannelMap.get(uid); }

    public List<User> getGroupMembers() {
        //返回所有用户 接入缓存
        Object o = redisTemplate.opsForValue().get("userlist");
        if (o != null) {
            return (List<User>) o;
        }else {
            List<User> data = userServepi.list().getData();
            redisTemplate.opsForValue().set("userlist",data ,60, TimeUnit.SECONDS);
            return data;
        }
    }

    public List<User> getOnlineUsers() {
        //返回所有用户 过滤 只留下登录的  接入缓存
        List<User> list;
        Object o = redisTemplate.opsForValue().get("userlist");
        if (o != null) {
            list = (List<User>) o;
        }else {
            List<User> data = userServepi.list().getData();
            redisTemplate.opsForValue().set("userlist",data ,60, TimeUnit.SECONDS);
            list = data;
        }

        return list.stream()
                .filter(u -> uidChannelMap.containsKey(String.valueOf(u.getId())))
                .collect(Collectors.toList());
    }
}