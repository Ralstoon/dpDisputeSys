package com.seu.form.VOForm;

import lombok.Data;

import java.util.Date;

@Data
public class UserCaseListForm {
    Date date;
    String name;//case name
    String caseId;//case id
    String status;
    String applicant;
    String respondent;
    String currentMedator;
    String mediatorId;
}
