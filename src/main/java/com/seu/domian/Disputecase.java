package com.seu.domian;

import lombok.Data;
import org.springframework.context.annotation.Lazy;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName Disputecase
 * @Description 纠纷信息表
 * @Author 吴宇航
 * @Date 2018/7/20 21:22
 * @Version 1.0
 **/

@Data
@Entity
//@Lazy(value = true)
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
    private String recommendedPaper="{\n" +
            "\t\"dissension_ms\": [],\n" +
            "\t\"dissension_dx\": [],\n" +
            "\t\"dissension\": []\n" +
            "}";
    /** 案件确定的调解员id号 */
    private String mediatorId;
    /** 表示主要纠纷场景是第几个 */
    private String mainRecStage;
    /** 表示排序后的纠纷要素信息 */
    private String keywordList;
    /** 表示调解员评判 */
    private String moderatorRegister;

    //调解中心
    private String mediationCenter;
    private String provice;
    private String city;

    //案件等级
    private String level;

    public Disputecase() {
    }

    public Disputecase(Date applyTime) {
        this.applyTime = applyTime;
    }
}
