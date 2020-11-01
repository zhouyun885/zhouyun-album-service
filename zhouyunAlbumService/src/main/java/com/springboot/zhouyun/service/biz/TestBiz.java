package com.springboot.zhouyun.service.biz;

import com.springboot.zhouyun.service.entity.TAccount;
import com.springboot.zhouyun.service.mapper.TAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author zhouyun
 * @Date 2020/10/29
 */
@Service
public class TestBiz {
    @Autowired
    private TAccountMapper tAccountMapper;


    public TAccount getTAccount() {
        return tAccountMapper.selectByPrimaryKey(1L);
    }

}
