package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@Entity
public class DisputecaseAccessory {
    @Id
    /** 附件表id号 */
    private String id;
    /** 附件表对应的case表id号 */
    private String disputecaseId;
    /** 医疗损害鉴定json */
    private String medicaldamageAssessment;
    /** 用户上传json *///实际为调解员上传
    private String normaluserUpload;
    /** 问询医院json */
    private String inquireHospital;
    /** 代理人委托书*n */
    private String proxyCertification;
    /** 告知书确认函 */
    private String notificationAffirm;
    /** 专家申请（专家邀请书*1+材料*n）*/
    private String appointExpert;
    /** 受理通知书 */
    private String acceptanceNotice;

    //协议
    private String protocal;

    //司法确认
    private String judicialConfirm;

    //用户上传
    private String userUpload;

    private String inqueryFile;

    public DisputecaseAccessory(String id, String disputecaseId, String medicaldamageAssessment, String normaluserUpload, String inquireHospital, String proxyCertification, String notificationAffirm, String appointExpert, String acceptanceNotice, String protocal, String judicialConfirm, String userUpload, String inqueryFile) {
        this.id = id;
        this.disputecaseId = disputecaseId;
        this.medicaldamageAssessment = medicaldamageAssessment;
        this.normaluserUpload = normaluserUpload;
        this.inquireHospital = inquireHospital;
        this.proxyCertification = proxyCertification;
        this.notificationAffirm = notificationAffirm;
        this.appointExpert = appointExpert;
        this.acceptanceNotice = acceptanceNotice;
        this.protocal = protocal;
        this.judicialConfirm = judicialConfirm;
        this.userUpload = userUpload;
        this.inqueryFile = inqueryFile;
    }

    public DisputecaseAccessory() {
    }
}
