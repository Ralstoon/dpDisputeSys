package com.seu.enums;

import lombok.Getter;

/**
 * @ClassName DisputeRegisterEnum
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/20 21:11
 * @Version 1.0
 **/

@Getter
public enum DisputeRegisterEnum {
    GETDISEASELIST_SUCCESS(0,"获取疾病列表成功"),
    GETMEDICALBEHAVIOR_SUCCESS(1,"获取医疗行为列表成功"),
    GETROOMLIST_SUCCESS(2,"获取科室列表成功")


    ;
    private String msg;
    private Integer code;
    DisputeRegisterEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
