package com.seu.form.VOForm;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ManagerCaseForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/31 21:57
 * @Version 1.0
 **/
@Data
public class ManagerCaseForm {
    @Data
    class intentionOne{
        private String mediatorName;
        private String mediatorId;
        private String mediatorIntention;
        public intentionOne(String mediatorName,String mediatorId,String mediatorIntention){
            this.mediatorId=mediatorId;
            this.mediatorName=mediatorName;
            this.mediatorIntention=mediatorIntention;
        }
    }
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
    /** 当前调解员 */
    private String currentMediator;
    /** 当前调解员id */
    private String mediatorid;
    /** 用户意向调解员 */
    private List<intentionOne> userIntention;

    public void addUserIntention(String mediatorName,String mediatorId,String mediatorIntention){
        if(userIntention==null)
            userIntention=new ArrayList<>();
        userIntention.add(new intentionOne(mediatorName,mediatorId,mediatorIntention));
    }
}
