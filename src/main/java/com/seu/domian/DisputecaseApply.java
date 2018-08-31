package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName DisputecaseApply
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/31 10:34
 * @Version 1.0
 **/

@Data
@Entity
public class DisputecaseApply {
    @Id
    private String id;
    private String name;
    private String idCard;
    private String phone;
    private String role;
    private String disputecaseId;
}
