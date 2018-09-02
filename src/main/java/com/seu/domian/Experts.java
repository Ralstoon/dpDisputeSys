package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @ClassName Experts
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/2 20:59
 * @Version 1.0
 **/

@Data
@Entity
public class Experts {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String phone;
    private String email;
}
