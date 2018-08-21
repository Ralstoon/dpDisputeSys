package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName ConstantData
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/20 20:19
 * @Version 1.0
 **/
@Data
@Entity
public class ConstantData {
    @Id
    private Integer id;
    private String name;
    private String data;
}
