package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@Entity
public class DisputecaseAccessory {
    @Id
    private String id;
    private String disputecaseId;
    private String medicaldamageAssessment;
    private String normaluserUpload;
    private String inquireHospital;
    private String appointExpert;
    private String proxyCertification;
    private String notificationAffirm;
}
