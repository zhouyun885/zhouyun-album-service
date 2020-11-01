package com.zhouyun;

import com.zhouyun.client.annotation.EnableTransUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@EnableTransUser
@SpringBootApplication
@ComponentScan(basePackages = {"com.zhouyun"})
public class ZhouYunAlbumApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhouYunAlbumApplication.class, args);
    }
}
