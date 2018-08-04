package com.seu.form;


import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/*
这个类是用来接收从前端传过来的用户修改信息
 */
@Data
public class NormalUserForm {
    /** 对应father_id */
    @NotEmpty(message = "ID号必填")
    private String fatherId;

    private String userName;

    private String idCard;

    private String sex;

    private String age;

    private String education;

    private String name;

    private String email;
}
