package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName DisputeInfo
 * @Description 纠纷信息表
 * @Author 吴宇航
 * @Date 2018/7/20 21:22
 * @Version 1.0
 **/
// TODO 目前先写这么多，content内容到时候填充
    //TODO disputeInfo表中userId和disputeId是一一一对应的，但是同一个用户可能先后多次做纠纷流程，因而在根据userId查询该表时可能会出现问题，这个要记得留意
@Data
@Entity
public class DisputeInfo {
    @Id
    private String disputeId;
    private String userId;
    private String content;
}
