package com.seu.domian;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class NormalUserDetail {
    @Id
    private String detailId;
    private String sex;
    private Date age;
    private String education;
    private String userId;

}
