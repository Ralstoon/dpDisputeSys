package com.seu.enums;

import lombok.Getter;

@Getter
public enum DisputeProgressEnum {
    STARTUP_SUCCESS(0,"纠纷调解工作流启动成功"),
    STARTUP_FAIL(1,"纠纷调解工作流启动失败"),
    DISPUTEREGISTER_SUCCESS(2,"纠纷案件已登记暂存"),
    DISPUTEREGISTER_FAIL(3,"纠纷案件登记失败"),
    TEMPORARYCONFIRM_SUCCESS(4,"暂存确认成功"),
    TRIGGERSIGNAL_SUCCESS(5,"触发信号成功"),

    ;
    private String msg;
    private Integer code;
    DisputeProgressEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
