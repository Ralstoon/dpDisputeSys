package com.seu.form.VOForm;

public class HospitalForm {
    String hospitalName;
    String room;
    String province;
    String city;
    String zone;

    public HospitalForm() {
    }

    public HospitalForm(String hospitalName, String room, String province, String city, String zone) {
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
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
}
