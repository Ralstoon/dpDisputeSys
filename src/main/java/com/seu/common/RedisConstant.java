package com.seu.common;

/**
 * @ClassName RedisConstant
 * @Description 定义一些与redis相关的常量
 * @Author 吴宇航
 * @Date 2018/8/2 20:56
 * @Version 1.0
 **/
public interface RedisConstant {
    String USER_RREFIX="user_%s";  //key值， %s 为用户id号
    Integer EXPIRE=7200; //  2小时，单位秒
}
