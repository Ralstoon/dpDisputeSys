package com.seu.enums;

import lombok.Getter;

@Getter
public enum RegisterEnum {
    REGISTER_SUCCESS(1,"用户注册成功"),
    REGISTER_FAIL(-1,"用户已存在"),
    REGISTERMEDIATOR_SUCCESS(2,"调解员注册成功"),
    REGISTERMEDIATOR_FAIL(-2,"调解员注册失败，手机号已存在"),
    BACKGROUNDSERVER_EXCEPTION(-10,"后台服务器异常，请重新发送")
    ;

    private Integer code;
    private String msg;
    RegisterEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
