package com.seu.domian;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Register {

    @Id
    private String id;

    private String phone;

    private String password;

    private String name;

    private String role;

    private String province;

    private String city;

    private String mediationCenter;

    private String zone;

    private String position;

    private String telephone;


    public Register() {
    }

    public Register(String id, String phone, String password, String name, String role, String province, String city, String mediationCenter, String zone, String position, String telephone) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.role = role;
        this.province = province;
        this.city = city;
        this.mediationCenter = mediationCenter;
        this.zone = zone;
        this.position = position;
        this.telephone = telephone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMediationCenter() {
        return mediationCenter;
    }

    public void setMediationCenter(String mediationCenter) {
        this.mediationCenter = mediationCenter;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
