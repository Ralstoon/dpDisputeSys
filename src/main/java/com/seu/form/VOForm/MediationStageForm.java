package com.seu.form.VOForm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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


    /** 可否预约专家 */
    private boolean expert=false;

    /** 是否预约过专家 */
    private String expertAppoint;

    /** 申请人数组 */
    private JSONArray applicants;

    /** 被申请人数组 */
    private JSONArray respondents=JSONArray.parseArray("[]");

    private JSONObject currentStageContent;

    /** 挂起状态 */
    private Integer isSuspended;

}
