package com.seu.config;

import com.seu.Interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName LoginInterceptorConfig
 * @Description LoginInterceptor拦截器的配置类
 * @Author 吴宇航
 * @Date 2018/7/20 20:12
 * @Version 1.0
 **/
@Configuration
public class LoginInterceptorConfig extends WebMvcConfigurerAdapter {
    @Bean
    public HandlerInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/user/updateInfo")
                .addPathPatterns("/disputeProgress/**");
        super.addInterceptors(registry);
    }

}
