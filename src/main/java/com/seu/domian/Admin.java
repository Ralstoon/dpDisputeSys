package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName Admin
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/20 15:44
 * @Version 1.0
 **/
@Data
@Entity
public class Admin {
    @Id
    private String Id;
    private String fatherId;
    private String adminName;
    private String idCard;
    private String mediateCenter;
    private String city;
    private String province;
    private String level;

    //
    private String caseMangeLevel;

    private String duty;
    private String tele;

    public Admin(String id, String fatherId, String adminName, String idCard, String mediateCenter, String city, String province, String level, String caseMangeLevel, String duty, String tele) {
        Id = id;
        this.fatherId = fatherId;
        this.adminName = adminName;
        this.idCard = idCard;
        this.mediateCenter = mediateCenter;
        this.city = city;
        this.province = province;
        this.level = level;
        this.caseMangeLevel = caseMangeLevel;
        this.duty = duty;
        this.tele = tele;
    }

    public Admin() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMediateCenter() {
        return mediateCenter;
    }

    public void setMediateCenter(String mediateCenter) {
        this.mediateCenter = mediateCenter;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCaseMangeLevel() {
        return caseMangeLevel;
    }

    public void setCaseMangeLevel(String caseMangeLevel) {
        this.caseMangeLevel = caseMangeLevel;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getTele() {
        return tele;
    }

    public void setTele(String tele) {
        this.tele = tele;
    }
}
