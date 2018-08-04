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
    private String fatherId;
    private String mediatorName;
    private String idCard;
    private String mediateCenter;
    private String authorityConfirm;
    private String authorityJudiciary;
}
