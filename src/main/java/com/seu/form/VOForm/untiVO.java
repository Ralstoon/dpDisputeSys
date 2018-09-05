package com.seu.form.VOForm;

import java.io.Serializable;

/**
 * @ClassName untiVO
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/5 22:43
 * @Version 1.0
 **/
public class untiVO implements Serializable {

    private static final long serialVersionUID = -3011541242930103758L;
    private String name;
    private String phone;
    private String email;

    public untiVO(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;

    }
}
