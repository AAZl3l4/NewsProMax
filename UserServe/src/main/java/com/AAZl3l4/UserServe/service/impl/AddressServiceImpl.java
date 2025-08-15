package com.AAZl3l4.UserServe.service.impl;

import com.AAZl3l4.UserServe.mapper.AddressMapper;
import com.AAZl3l4.common.pojo.Address;
import com.AAZl3l4.UserServe.service.IAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

}
