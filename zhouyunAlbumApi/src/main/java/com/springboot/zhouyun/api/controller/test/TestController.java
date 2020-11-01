package com.springboot.zhouyun.api.controller.test;

import com.alibaba.fastjson.JSON;
import com.springboot.zhouyun.service.biz.TestBiz;
import com.springboot.zhouyun.service.entity.TAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zhouyun
 * @Date 2020/10/19
 */
@RestController
@RequestMapping("/api/test/")
@Api(value = "TEST-API", description = "测试接口")
@Slf4j
public class TestController {


    @Autowired
    private TestBiz testBiz;

    @GetMapping(value = "test")
    @ApiOperation("测试接口")
    String searchRoomList1() {
        TAccount tAccount = testBiz.getTAccount();
        String test = "";
        if (null != tAccount) {

            test = JSON.toJSONString(tAccount);
        }

        return test;

    }


}
