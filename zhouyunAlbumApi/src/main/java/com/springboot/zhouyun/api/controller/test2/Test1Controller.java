package com.springboot.zhouyun.api.controller.test2;

import com.zhouyun.common.jwtkits.IJWTInfo;
import com.zhouyun.common.jwtkits.JWTInfo;
import com.zhouyun.common.jwtkits.TokenVo;
import com.zhouyun.core.redis.RedisUtil;
import com.zhouyun.service.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @Author: zhouyun
 * @Date: 2020/10/25 17:50
 */
@RestController
@RequestMapping("/api/test2/")
@Api(value = "TEST-API", description = "测试接口2")
public class Test1Controller {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping(value = "test2")
    @ApiOperation("测试接口2")
    String searchRoomList2() {


        TokenVo tokenVo = jwtTokenUtil.generateToken(new JWTInfo("测试13", 1L, "123", 1L, "1", "fsafas", new ArrayList<>(), new ArrayList<>()));

        redisUtil.get("ss");
        IJWTInfo ijwtInfo = jwtTokenUtil.getInfoFromToken(tokenVo.getToken());


        return ijwtInfo.getUserName();
    }

}
