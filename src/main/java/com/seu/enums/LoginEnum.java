package com.seu.enums;

import lombok.Getter;

@Getter
public enum LoginEnum {
    LOGIN_SUCCESS(0,"用户登录成功"),
    NONREGISTER_PHONE(1,"手机号未注册"),
    PASSWORD_ERROR(2,"登录密码错误"),
    NULL_CURRENTUSER(3,"用户尚未登录"),
    ;
    private String msg;
    private Integer code;
    LoginEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }

}
