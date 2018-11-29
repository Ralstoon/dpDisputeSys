package com.seu.form.VOForm;

public class MediatorBankendForm {

    private String mediatorName;
    private String idCard;
    private String mediateCenter;
    private String authorityConfirm;
    private String authorityJudiciary;
    private String basicInformation;
    private String city;
    private String province;
    private String avatar;
    private String phone;

    public String getMediatorName() {
        return mediatorName;
    }

    public void setMediatorName(String mediatorName) {
        this.mediatorName = mediatorName;
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

    public String getAuthorityConfirm() {
        return authorityConfirm;
    }

    public void setAuthorityConfirm(String authorityConfirm) {
        this.authorityConfirm = authorityConfirm;
    }

    public String getAuthorityJudiciary() {
        return authorityJudiciary;
    }

    public void setAuthorityJudiciary(String authorityJudiciary) {
        this.authorityJudiciary = authorityJudiciary;
    }

    public String getBasicInformation() {
        return basicInformation;
    }

    public void setBasicInformation(String basicInformation) {
        this.basicInformation = basicInformation;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public MediatorBankendForm(String mediatorName, String idCard, String mediateCenter, String authorityConfirm, String authorityJudiciary, String basicInformation, String city, String province, String avatar, String phone) {
        this.mediatorName = mediatorName;
        this.idCard = idCard;
        this.mediateCenter = mediateCenter;
        this.authorityConfirm = authorityConfirm;
        this.authorityJudiciary = authorityJudiciary;
        this.basicInformation = basicInformation;
        this.city = city;
        this.province = province;
        this.avatar = avatar;
        this.phone = phone;
    }
}
