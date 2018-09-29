package com.seu.enums;

import lombok.Getter;

@Getter
public enum DisputeProgressEnum {
    SETCURRENTPROCESS_FAIL(-999,"流程操作失败"),
    FINDMEDIATOR_FAIL(-105,"调解员id不存在"),

    STARTUP_SUCCESS(0,"纠纷调解工作流启动成功"),
    STARTUP_FAIL(1,"纠纷调解工作流启动失败"),
    DISPUTEREGISTER_SUCCESS(2,"纠纷调解工作流启动成功，纠纷案件已登记暂存"),
    DISPUTEREGISTER_FAIL(3,"纠纷案件登记失败"),
    TEMPORARYCONFIRM_SUCCESS(4,"暂存确认成功"),
    TRIGGERSIGNAL_SUCCESS(5,"用户提出纠纷案件信息修改信号触发成功"),
    PROCESSINSTANCE_HASEXIST(6,"该用户的流程实例已经存在，不能再启动新的流程实例"),
    SEARCH_DISPUTECASELIST_SUCCESS(7,"查询当前用户纠纷案例列表成功"),
    CASEACCEPT_SUCCESS(8,"立案审核成功"),
    SEARCH_TASK_SUCCESS(9,"查询当前待办任务列表成功"),
    SEARCH_HISTORICTASKLIST_SUCESS(10,"查询历史任务成功"),
    ADD_TASKCOMMIT_SUCCESS(11,"添加任务评价成功"),
    MEDIATORSELECTCASE_SUCCESS(12,"调解员申请案件成功"),
    USERCHOOSEMEDIATOR_SUCCESS(13,"用户选择调解员成功"),
    MEDIATORAVOID_SUCCESS(14,"调解员申请回避成功"),
    GETMYMEDIATIONDATA_SUCCESS(15,"获取案件列表成功"),
    GETMEDIATORLIST_SUCCESS(16,"获取调解员列表成功"),
    GETNAMEOFAUTHORITY_SUCCESS(17,"获取调解员的授权信息成功"),
    CHANGEMEDIATORAUTHORITY_SUCCESS(18,"修改调解员权限成功"),
    GETMEDIATIONSTAGE_SUCCESS(19,"获取当前调解步骤成功"),
    SETRESULTOFINDENT_SUCCESS(20,"添加医疗损害鉴定结果成功"),
    SETAPPOINT_SUCCESS(21,"添加预约数据成功"),
    INFORMINDENTY_SUCCESS(22,"通知用户损害医疗鉴定成功"),
    GETEXPERTLIST_SUCCESS(23,"获取专家列表成功"),
    MEDIATORGETAUTHORITY_SUCCESS(24,"获取调解员权限成功"),
    GETUSERCHOOSE_SUCCESS(25,"获取案件的意向调解员成功"),
    GETUSERCHOOSE_NONE(26,"该案件未选择意向调解员"),
    GETADDITIONALALLOCATION_SUCCESS(27,"获取另外分配调解员成功"),
    GETINFORMATION_SUCCESS(28,"从后台获取信息成功")

    ;
    private String msg;
    private Integer code;
    DisputeProgressEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
