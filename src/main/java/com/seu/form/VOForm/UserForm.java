package com.seu.form.VOForm;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserForm
 * @Description 返回给前端的登录信息form,同时也存储在redis中
 * @Author 吴宇航
 * @Date 2018/8/3 21:24
 * @Version 1.0
 **/

@Data
public class UserForm implements Serializable {
    private static final long serialVersionUID = 1940552884287450579L;

    private String id;  // user表id
    private String specific_id;  // 对应role表的id
    private String role;
}
