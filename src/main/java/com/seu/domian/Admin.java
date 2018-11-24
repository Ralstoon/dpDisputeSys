package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName Admin
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/20 15:44
 * @Version 1.0
 **/
@Data
@Entity
public class Admin {
    @Id
    private String Id;
    private String fatherId;
    private String adminName;
    private String idCard;
    private String mediateCenter;
    private String city;
    private String province;
    private Integer level;
}
