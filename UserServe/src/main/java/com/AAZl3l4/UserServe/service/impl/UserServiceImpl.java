package com.AAZl3l4.UserServe.service.impl;

import com.AAZl3l4.common.pojo.User;
import com.AAZl3l4.UserServe.mapper.UserMapper;
import com.AAZl3l4.UserServe.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
