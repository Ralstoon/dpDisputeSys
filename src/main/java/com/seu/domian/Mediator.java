package com.seu.domian;

import lombok.Data;
import org.python.antlr.ast.Str;

import javax.persistence.*;

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

//    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
////    @JoinColumn(name = "user_ID")
    private String fatherId;
    private String mediatorName;
    private String idCard;
    private String mediateCenter;
    private String authorityConfirm="0";
    private String authorityJudiciary="0";
    private String basicInformation;
    private String city;
    private String province;
    private String avatar;

    public Mediator() {
    }

    public Mediator(String mediatorId, String fatherId, String mediatorName, String idCard, String mediateCenter, String authorityConfirm, String authorityJudiciary) {
        this.mediatorId = mediatorId;
        this.fatherId = fatherId;
        this.mediatorName = mediatorName;
        this.idCard = idCard;
        this.mediateCenter = mediateCenter;
        this.authorityConfirm = authorityConfirm;
        this.authorityJudiciary = authorityJudiciary;
    }

    public Mediator(String mediatorId, String fatherId, String mediatorName, String idCard, String mediateCenter, String authorityConfirm, String authorityJudiciary, String basicInformation, String city, String province) {
        this.mediatorId = mediatorId;
        this.fatherId = fatherId;
        this.mediatorName = mediatorName;
        this.idCard = idCard;
        this.mediateCenter = mediateCenter;
        this.authorityConfirm = authorityConfirm;
        this.authorityJudiciary = authorityJudiciary;
        this.basicInformation = basicInformation;
        this.city = city;
        this.province = province;
    }

    public Mediator(String mediatorId, String fatherId, String mediatorName, String idCard, String mediateCenter, String authorityConfirm, String authorityJudiciary, String basicInformation, String city, String province, String avatar) {
        this.mediatorId = mediatorId;
        this.fatherId = fatherId;
        this.mediatorName = mediatorName;
        this.idCard = idCard;
        this.mediateCenter = mediateCenter;
        this.authorityConfirm = authorityConfirm;
        this.authorityJudiciary = authorityJudiciary;
        this.basicInformation = basicInformation;
        this.city = city;
        this.province = province;
        this.avatar = avatar;
    }
}
