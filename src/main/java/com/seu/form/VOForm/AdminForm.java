package com.seu.form.VOForm;

import lombok.Data;

@Data
public class AdminForm {
    private String Id;
    private String fatherId;
    private String adminName;
    private String mediateCenter;
    private String city;
    private String province;
    private String level;
    private String caseMangeLevel;

    private String duty;
    private String tele;

    private boolean authority1;
    private boolean authority2;

    public AdminForm(String id, String fatherId, String adminName, String mediateCenter, String city, String province, String level, String caseMangeLevel, String duty, String tele,
                     boolean authority1, boolean authority2) {
        Id = id;
        this.fatherId = fatherId;
        this.adminName = adminName;
        this.mediateCenter = mediateCenter;
        this.city = city;
        this.province = province;
        this.level = level;
        this.caseMangeLevel = caseMangeLevel;
        this.duty = duty;
        this.tele = tele;
        this.authority1 = authority1;
        this.authority2 = authority2;
    }
}
