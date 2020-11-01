package com.zhouyun.album.api.controller.user;

import com.netflix.client.http.HttpResponse;
import com.zhouyun.album.api.controller.dto.LoginRequestDto;
import com.zhouyun.album.api.controller.dto.LoginResponseDto;
import com.zhouyun.album.service.biz.UserBiz;
import com.zhouyun.album.service.biz.model.LoginRequestModel;
import com.zhouyun.album.service.biz.model.LoginResponseModel;
import com.zhouyun.core.base.Result;
import com.zhouyun.tools.mapper.utils.MapperUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.ws.Action;

/**
 * @Author: zhouyun
 * @Date: 2020/11/1 16:54
 */
@RestController
@RequestMapping("api/v1/user")
@Api(value = "USER-API", description = "用户接口")
public class UserController {

    @Autowired
    private UserBiz userBiz;

    @PostMapping("/login")
    public Result<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {
        LoginResponseModel response = userBiz.login(MapperUtils.mapper(requestDto, LoginRequestModel.class));
        httpServletResponse.setHeader("token", response.getToken());
        return Result.success(MapperUtils.mapper(response, LoginResponseDto.class));
    }
}
