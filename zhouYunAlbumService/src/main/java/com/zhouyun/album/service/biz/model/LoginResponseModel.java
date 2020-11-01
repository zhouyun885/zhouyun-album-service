package com.zhouyun.album.service.biz.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhouyun
 * @Date: 2020/11/1 17:04
 */
@Data
public class LoginResponseModel {
    @ApiModelProperty("用户id")
    private Long id;
    @ApiModelProperty("用户名称")
    private String name;

    private String token;


}
