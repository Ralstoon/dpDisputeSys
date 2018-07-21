package com.seu.enums;

import lombok.Getter;

@Getter
public enum DisputeProgressEnum {
    STARTUP_SUCCESS(0,"纠纷调解工作流启动成功"),
    STARTUP_FAIL(1,"纠纷调解工作流启动失败"),
    DISPUTEREGISTER_SUCCESS(2,"纠纷调解工作流启动成功，纠纷案件已登记暂存"),
    DISPUTEREGISTER_FAIL(3,"纠纷案件登记失败"),
    TEMPORARYCONFIRM_SUCCESS(4,"暂存确认成功"),
    TRIGGERSIGNAL_SUCCESS(5,"用户提出纠纷案件信息修改信号触发成功"),
    PROCESSINSTANCE_HASEXIST(6,"该用户的流程实例已经存在，不能再启动新的流程实例"),
    SEARCH_DISPUTECASELIST_SUCCESS(7,"查询当前用户纠纷案例列表成功"),
    CASEACCEPT_SUCCESS(8,"立案审核成功"),

    ;
    private String msg;
    private Integer code;
    DisputeProgressEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
