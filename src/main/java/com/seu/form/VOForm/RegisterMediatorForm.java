package com.seu.form.VOForm;

import lombok.Data;

/**
 * @ClassName RegisterMediatorForm
 * @Description 调解员注册成功后呈现在前端的界面
 * @Author 吴宇航
 * @Date 2018/9/11 0011 上午 10:40
 * @Version 1.0
 **/

@Data
public class RegisterMediatorForm {
    private String phone;
    private String name;
    private String idCard;
    private String mediateCenter;
    private String authorityConfirm;
    private String authorityJudiciary;

    public RegisterMediatorForm(String phone,String name, String idCard, String mediateCenter, String authorityConfirm, String authorityJudiciary) {
        this.phone=phone;
        this.name = name;
        this.idCard = idCard;
        this.mediateCenter = mediateCenter;
        if(authorityConfirm.trim()=="0" || authorityConfirm.trim().equals("0"))
            this.authorityConfirm = "否";
        else
            this.authorityConfirm = "是";

        if(authorityJudiciary.trim()=="0" || authorityJudiciary.trim().equals("0"))
            this.authorityJudiciary = "否";
        else
            this.authorityJudiciary = "是";
    }
}
