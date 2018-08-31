package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName Disputecase
 * @Description 纠纷信息表
 * @Author 吴宇航
 * @Date 2018/7/20 21:22
 * @Version 1.0
 **/

    //TODO disputeInfo表中userId和disputeId是一一一对应的，但是同一个用户可能先后多次做纠纷流程，因而在根据userId查询该表时可能会出现问题，这个要记得留意
@Data
@Entity
public class Disputecase {
    @Id
    private String id;
    /** 申请人在apply表的id号列表 */
    private String proposerId;
    /** 代理人在apply表的id号列表 */
    private String agnetId;
    /** 案件名 */
    private String caseName;
    /** 简要情况 */
    private String briefCase;
    /** 医疗过程 */
    private String medicalProcess;
    /** 索赔金额 */
    private String claimMoney;
    /** 申请时间 */
    private Date applyTime;
    /** 诉求 */
    private String appeal;
    /** 案件相关的process表id号 */
    private String processId;
    /** 案件相关的accessory表id号 */
    private String accessoryId;
    /** 案件相关类案推荐 */
    private String recommendedPaper;
    /** 案件确定的调解员id号 */
    private String mediatorId;
}
