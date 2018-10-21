package com.seu.domian;

import com.seu.common.InitConstant;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.Date;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.Date;

/**
 * @ClassName DisputecaseProcess
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/30 16:01
 * @Version 1.0
 **/

@Data
@Entity
public class DisputecaseProcess {
    @Id
    /** 进程表id号 */
    private String id;
    /** 进程表对应的case表id号 */
    private String disputecaseId;
    /** 进程状态 */
    private String status="0";

    /** 调解员回避状态 */
    private String avoidStatus="";
    /** 调解员申请状态 */
    private String applyStatus="";
    /** 用户选择的调解员id号 */
    private String userChoose="";
    /** 调解情况，即各阶段下调解内容 */
    private String mediateStage=InitConstant.init_mediateStage;
    /** 调解开始时间(立案判断通过时间) */
    private Date startimeDisputecase;
    /** 调解结束时间 */
    private Date endtimeDisputecase;
    /** 进程是否挂起 */
    private Integer isSuspended = 0;
    /** 专家预约参数 */
    private String paramProfessor;

    private String reason = "{\n" +
            "\t\"caseCancelApply \": [],\n" +
            "\t\"caseCancelMediation \": [],\n" +
            "\t\"changeMediator \": []\n" +
            "}";

    public DisputecaseProcess() {
    }

    public DisputecaseProcess(String id, String disputecaseId, String status, String avoidStatus, String applyStatus, String userChoose, String mediateStage, Date startimeDisputecase, Date endtimeDisputecase, Integer isSuspended, String paramProfessor, String reason) {
        this.id = id;
        this.disputecaseId = disputecaseId;
        this.status = status;
        this.avoidStatus = avoidStatus;
        this.applyStatus = applyStatus;
        this.userChoose = userChoose;
        this.mediateStage = mediateStage;
        this.startimeDisputecase = startimeDisputecase;
        this.endtimeDisputecase = endtimeDisputecase;
        this.isSuspended = isSuspended;
        this.paramProfessor = paramProfessor;
        this.reason = reason;
    }
}
