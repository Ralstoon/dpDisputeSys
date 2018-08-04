package com.seu.domian;

import lombok.Data;
import org.springframework.context.annotation.Lazy;
import javax.persistence.Id;

import javax.persistence.Entity;

/**
 * @ClassName User
 * @Description 对应数据库user表
 * @Author 吴宇航
 * @Date 2018/8/3 21:12
 * @Version 1.0
 **/

@Data
@Entity
public class User {
    @Id
    private String ID;
    private String phone;
    private String password;
    private String role;
    private String specificId;

    public User(){}

    public User(String ID, String phone, String password, String role, String specificId) {
        this.ID = ID;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.specificId = specificId;
    }
}
