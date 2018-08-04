package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class NormalUser{
    @Id
    private String normalId;
    private String fatherId;
    private String userName;
    private String idCard;
    private String sex;
    private Date age;
    private String education;
    private String name;
    private String email;

    public NormalUser() {
    }

    public NormalUser(String normalId, String fatherId, String userName, String idCard, String sex, Date age, String education, String name, String email) {
        this.normalId = normalId;
        this.fatherId = fatherId;
        this.userName = userName;
        this.idCard = idCard;
        this.sex = sex;
        this.age = age;
        this.education = education;
        this.name = name;
        this.email = email;
    }

    public NormalUser(String normalId, String fatherId) {
        this.normalId = normalId;
        this.fatherId = fatherId;
    }
}
