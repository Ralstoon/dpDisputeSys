package com.seu.form.VOForm;

import com.alibaba.fastjson.JSONArray;
import com.seu.domian.ContactList;

import java.util.List;

public class HospitalForm {
    String hospitalName;
    JSONArray room;
    String province;
    String city;
    String zone;
    List<ContactList> contactLists;

    public HospitalForm() {
    }

    public HospitalForm(String hospitalName, JSONArray room, String province, String city, String zone) {
        this.hospitalName = hospitalName;
        this.room = room;
        this.province = province;
        this.city = city;
        this.zone = zone;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public JSONArray getRoom() {
        return room;
    }

    public void setRoom(JSONArray room) {
        this.room = room;
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

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setContactLists(List<ContactList> contactLists){
        this.contactLists = contactLists;
    }

    public List<ContactList> getContactLists() {
        return contactLists;
    }
}
