package com.springboot.zhouyun.api.config.configweb;



import com.springboot.zhouyun.api.interceptor.AuthRestInterceptor;
import com.zhouyun.core.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;


@Primary
@Configuration("adminWebConfig")
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public GlobalExceptionHandler getGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        ArrayList<String> commonPathPatterns = getExcludeCommonPathPatterns();
        registry.addInterceptor(getAuthRestInterceptor()).addPathPatterns("/**")
                .excludePathPatterns(commonPathPatterns.toArray(new String[]{}));
    }

    @Bean
    AuthRestInterceptor getAuthRestInterceptor() {
        return new AuthRestInterceptor();
    }

    private ArrayList<String> getExcludeCommonPathPatterns() {
        ArrayList<String> list = new ArrayList<>();
        String[] urls = {
                "/cache/**",
                "/webjars/**",
                "/v2/controller-docs",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/error",
                "/websocket/**"
        };

        Collections.addAll(list, urls);
        return list;
    }

}
