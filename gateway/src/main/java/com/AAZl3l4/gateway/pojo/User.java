package com.AAZl3l4.gateway.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String password;

    private String avatarUrl;

    private String email;

    private Integer age;

    private String sex;

    private String roles;

    private String isban;

    private LocalDateTime createTime;

    private Double money;

    private String wxId;


}
