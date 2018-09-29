package com.seu.form.VOForm;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.seu.common.RedisConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MediationStageForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/1 20:29
 * @Version 1.0
 **/
@Data
public class MediationStageForm implements Serializable{


    private static final long serialVersionUID = -82542334189002280L;
    /** 当前阶段，从1开始 */
    private Integer stage=1;

    /** 当前步骤 0 1 2 */
    private Integer currentStatus=0;

    /** 是否具有鉴定资格 */
    private boolean identiQualify=true;

    /** 是否做过鉴定 */
    private boolean identified=false;

    /** 鉴定结果 */
    private String resultOfIdentify="";

    /** 可否预约专家 */
    private boolean expert=false;

    /** 申请人数组 */
    private String applicants="[]";

    /** 被申请人数组 */
    private String respondents="[]";

    private String currentStageContent="[]";

    /** 挂起状态 */
    private Integer isSuspended;

}
