package com.seu.enums;

import lombok.Getter;

@Getter
public enum RegisterEnum {
    REGISTER_SUCCESS(0,"用户注册成功"),
    REGISTER_FAIL(1,"用户已存在")
    ;

    private Integer code;
    private String msg;
    RegisterEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
