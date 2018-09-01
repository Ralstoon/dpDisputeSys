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

    public DisputecaseAccessory(String id, String disputecase_id, String medicaldamage_assessment, String normaluser_upload) {
        this.id = id;
        this.disputecaseId = disputecase_id;
        this.medicaldamageAssessment = medicaldamage_assessment;
        this.normaluserUpload = normaluser_upload;
    }

    public DisputecaseAccessory() {
    }
}
