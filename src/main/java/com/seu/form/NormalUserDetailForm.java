package com.seu.form;


import lombok.Data;
/*
这个类是用来接收从前端传过来的用户信息管理信息的
 */
@Data
public class NormalUserDetailForm {
    private String sex;

    private String age;

    private String education;
}
