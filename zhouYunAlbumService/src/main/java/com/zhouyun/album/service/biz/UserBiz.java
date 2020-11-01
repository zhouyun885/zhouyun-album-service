package com.zhouyun.album.service.biz;

import com.zhouyun.album.enums.AlbumExceptionEnum;
import com.zhouyun.album.service.biz.model.LoginRequestModel;
import com.zhouyun.album.service.biz.model.LoginResponseModel;
import com.zhouyun.album.service.entity.TUser;
import com.zhouyun.album.service.mapper.TUserMapper;
import com.zhouyun.common.jwtkits.JWTInfo;
import com.zhouyun.common.jwtkits.TokenVo;
import com.zhouyun.core.exception.BizException;
import com.zhouyun.service.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @Author: zhouyun
 * @Date: 2020/11/1 16:30
 */
@Service
@Slf4j
public class UserBiz {
    @Autowired
    private TUserMapper tUserMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public LoginResponseModel login(LoginRequestModel requestModel) {
        LoginResponseModel loginResponseModel = new LoginResponseModel();
        TUser tUser = tUserMapper.getUser(requestModel.getUserAccount());
        if (null == tUser) {
            throw new BizException(AlbumExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        }
        TokenVo tokenVo = jwtTokenUtil.generateToken(new JWTInfo(tUser.getName(), tUser.getId(), tUser.getName(), 1L, "1", "fsafas", new ArrayList<>(), new ArrayList<>()));
        loginResponseModel.setId(tUser.getId());
        loginResponseModel.setName(tUser.getName());
        loginResponseModel.setToken(tokenVo.getToken());
        return new LoginResponseModel();

    }

}
