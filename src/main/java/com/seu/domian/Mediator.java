package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName Mediator
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/20 15:45
 * @Version 1.0
 **/

@Data
@Entity
public class Mediator {
    @Id
    private String mediatorId;
    private String mediatorName;
    private String phone;
    private String idCard;
    private String password;
}
