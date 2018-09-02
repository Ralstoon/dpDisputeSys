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

    public DisputecaseAccessory(String id, String disputecase_id, String medicaldamage_assessment, String normaluser_upload, String inquireHospital) {
        this.id = id;
        this.disputecaseId = disputecase_id;
        this.medicaldamageAssessment = medicaldamage_assessment;
        this.normaluserUpload = normaluser_upload;
        this.inquireHospital = inquireHospital;
    }

    public DisputecaseAccessory() {
    }
}
