package com.AAZl3l4.UserServe.controller;


import com.AAZl3l4.UserServe.pojo.Address;
import com.AAZl3l4.UserServe.service.IAddressService;
import com.AAZl3l4.common.utils.Result;
import com.AAZl3l4.common.utils.UserTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private IAddressService addressService;

    @PostMapping("/add")
    @Operation(summary = "添加地址")
    public Result add(@RequestBody Address address) {
        address.setId(UserTool.getid());
        if (address.getIsDefault()==('1')){
            Address byId = addressService.getById(new QueryWrapper<Address>().eq("userId", address.getUserId()).eq("isDefault", '1'));
            if (byId != null) {
                byId.setIsDefault('0');
                addressService.updateById(byId);
            }
        }
        boolean save = addressService.save(address);

        if (save) {
            return Result.succeed("添加成功");
        }else {
            return Result.error("添加失败");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "修改地址")
    public Result update(@RequestBody Address address) {
        if (address.getIsDefault()==('1')){
            Address byId = addressService.getById(new QueryWrapper<Address>().eq("userId", address.getUserId()).eq("isDefault", '1'));
            if (byId != null) {
                byId.setIsDefault('0');
                addressService.updateById(byId);
            }
        }
        boolean update = addressService.updateById(address);

        if (update) {
            return Result.succeed("修改成功");
        }else {
            return Result.error("修改失败");
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除地址")
    public Result delete(Integer id) {
        boolean delete = addressService.removeById(id);

        if (delete) {
            return Result.succeed("删除成功");
        }else {
            return Result.error("删除失败");
        }
    }




}
