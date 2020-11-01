package com.zhouyun.album.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhouyun
 * @Date: 2020/11/1 17:04
 */
@Data
public class LoginResponseDto {
    @ApiModelProperty("用户id")
    private String id;
    @ApiModelProperty("用户名称")
    private String name;


}
