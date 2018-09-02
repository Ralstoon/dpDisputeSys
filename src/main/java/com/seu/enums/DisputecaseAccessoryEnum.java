package com.seu.enums;

import lombok.Getter;

@Getter
public enum  DisputecaseAccessoryEnum {

    ADDNORMLUSERUPLOAD_SUCCESS(1,"添加用户附件成功"),
    GETNORMALUSERUPLOADLIST_SUCCESS(2, "用户附件列表获取成功"),
    ADDINQUIREHOSPITAL_SUCCESS(3, "添加问询医院成功"),
    ;

    private String msg;
    private Integer code;
    DisputecaseAccessoryEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
