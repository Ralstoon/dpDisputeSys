package com.seu.form;

import lombok.Data;

@Data
public class HistoryCaseForm {
    private String applyTime;
    private String caseName;
    private String caseId;
    private String biefCase;
    private String applyPerson;
    private String hospital;

    public HistoryCaseForm() {
    }

    public HistoryCaseForm(String applyTime, String caseName, String caseId, String biefCase, String applyPerson, String hospital) {
        this.applyTime = applyTime;
        this.caseName = caseName;
        this.caseId = caseId;
        this.biefCase = biefCase;
        this.applyPerson = applyPerson;
        this.hospital = hospital;
    }
}
