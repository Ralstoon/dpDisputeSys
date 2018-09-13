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
    /** 用户上传json */
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



    public DisputecaseAccessory(String id, String disputecaseId, String medicaldamageAssessment, String normaluserUpload, String inquireHospital, String appointExpert, String proxyCertification) {
        this.id = id;
        this.disputecaseId = disputecaseId;
        this.medicaldamageAssessment = medicaldamageAssessment;
        this.normaluserUpload = normaluserUpload;
        this.inquireHospital = inquireHospital;
        this.appointExpert = appointExpert;
        this.proxyCertification = proxyCertification;
    }

    public DisputecaseAccessory() {
    }
}
