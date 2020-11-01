package com.zhouyun.album.service.mapper;

import com.zhouyun.album.service.entity.TUser;
import com.zhouyun.core.base.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TUserMapper extends BaseMapper<TUser> {

    TUser getUser(@Param("account") String account);
}