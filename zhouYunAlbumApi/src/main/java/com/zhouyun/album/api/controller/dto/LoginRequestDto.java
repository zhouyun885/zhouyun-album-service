package com.zhouyun.album.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: zhouyun
 * @Date: 2020/11/1 17:06
 */
@Data
public class LoginRequestDto {

    @ApiModelProperty(value = "帐号")
    @NotBlank(message = "帐号不能为空")
    private String userAccount;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
