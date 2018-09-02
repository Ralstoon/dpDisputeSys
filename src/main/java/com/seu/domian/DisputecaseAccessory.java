package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName DisputecaseAccessory
 * @Description 附件表
 * @Author 吴宇航
 * @Date 2018/9/1 23:46
 * @Version 1.0
 **/

@Data
@Entity
public class DisputecaseAccessory {
    @Id
    private String id;
    private String disputecaseId;
    private String medicaldamageAssessment;
    private String normaluserUpload;
    private String inquireHospital;
}
