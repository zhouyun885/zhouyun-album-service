package com.zhouyun.album.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigKeyConstant {

    @Value("${auth.client.pub-key.path}")
    public String jwtPubKeyPath;
    @Value("${auth.client.pri-key.path}")
    public String jwtPriKeyPath;
    @Value("${auth.client.expire}")
    public String jwtExpireTime;

}
