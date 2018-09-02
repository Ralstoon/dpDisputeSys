package com.seu.form.VOForm;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ManagerCaseJudiciaryForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/1 10:06
 * @Version 1.0
 **/
@Data
public class ManagerCaseJudiciaryForm {
    /** 案件登记日期 */
    private Date date;
    /** 案件名 */
    private String name;
    /** 案件id */
    private String nameid;
    /** 案件状态 */
    private String status;
    /** 申请人 */
    private List<String> Applicant;
    /** 被申请人 */
    private List<String> Respondent;
    /** 当前调解员 */
    private String currentMediator;
    /** 当前调解员id */
    private String mediatorid;
}
