package com.zhouyun.album.service.biz.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhouyun
 * @Date: 2020/11/1 17:06
 */
@Data
public class LoginRequestModel {

    @ApiModelProperty(value = "帐号")
    private String userAccount;

    @ApiModelProperty(value = "密码")
    private String password;
}
