package com.zhouyun.album.enums;

import com.zhouyun.core.base.enums.BaseExceptionEnum;

/**
 * @Author: zhouyun
 * @Date: 2020/11/1 21:07
 */
public enum AlbumExceptionEnum implements BaseExceptionEnum {
    USER_ACCOUNT_NOT_EXIST(10000, "用户账号不存在或密码错误"),;

    private Integer code;
    private String msg;

    AlbumExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
