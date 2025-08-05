package com.AAZl3l4.MallService.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
public class CommentDTO extends Comment {
    private String username;
    private String avatar;
}
