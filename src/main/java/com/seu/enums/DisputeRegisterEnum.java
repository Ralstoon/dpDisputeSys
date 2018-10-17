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
    GETCASEID_FAIL(-3,"获取案件ID失败"),
    GETALLMESSAGE_FAIL(-6,"整体纠纷信息登记成功"),
    GETDISEASELIST_SUCCESS(0,"获取疾病列表成功"),
    GETMEDICALBEHAVIOR_SUCCESS(1,"获取医疗行为列表成功"),
    GETROOMLIST_SUCCESS(2,"获取科室列表成功"),
    GETCASEID_SUCCESS(3,"获取案件ID成功"),
    GETINVOLVEDPEOPLEINFO_SUCCESS(4,"获取涉事人员信息成功"),
    GETBASICDIVIDEINFO_SUCCESS(5,"获取医疗过程数据成功"),
    GETALLMESSAGE_SUCCESS(6,"整体纠纷信息登记成功")


    ;
    private String msg;
    private Integer code;
    DisputeRegisterEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
