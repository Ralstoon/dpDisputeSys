package com.seu.enums;

import lombok.Getter;

@Getter
public enum UpdateInfoEnum {
    UPDATE_SUCCESS(0,"信息修改成功"),
    UPDATE_FAIL(1,"信息修改失败")
    ;

    private Integer code;
    private String msg;
    UpdateInfoEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
