package com.seu.config;

import com.seu.elasticsearch.MyTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ElasticSearchConfig
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/28 20:13
 * @Version 1.0
 **/

@Configuration
@ComponentScan(basePackages = {"com.seu.elasticsearch"})
public class ElasticSearchConfig {

}
