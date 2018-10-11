package com.seu.form;

import lombok.Data;

/**
 * @ClassName InstituteForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/9 0009 下午 5:09
 * @Version 1.0
 **/
@Data
public class InstituteForm {
    private String city;
    private String zone;
    private String hospital;

    public InstituteForm() {
    }

    public InstituteForm(String city, String zone, String hospital) {
        this.city = city;
        this.zone = zone;
        this.hospital = hospital;
    }

    @Override
    public String toString(){
        return city+"_"+zone+"_"+hospital;
    }
}
