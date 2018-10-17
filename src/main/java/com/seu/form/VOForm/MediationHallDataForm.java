package com.seu.form.VOForm;

import com.seu.common.RedisConstant;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName MediationHallDataForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/30 22:09
 * @Version 1.0
 **/

@Data
public class MediationHallDataForm {
    /** 案件登记日期 */
    private Date date;
    /** 案件名 */
    private String name;
    /** 案件id */
    private String CaseId;
    /** 案件状态 */
    private String status;
    /** 申请人 */
    private List<String> Applicant;
    /** 被申请人 */
    private List<String> Respondent;
    /** 剩余工作日 */
    private Object countdown;
    //
    private String fordebarb;

}
