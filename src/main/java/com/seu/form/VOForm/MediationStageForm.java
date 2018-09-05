package com.seu.form.VOForm;

import com.seu.common.RedisConstant;
import lombok.Data;

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
public class MediationStageForm {
    class unti{
        private String name;
        private String phone;
        private String email;

        public unti(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }
    }

    /** 当前阶段，从1开始 */
    private Integer stage;

    /** 当前步骤 0 1 2 */
    private Integer currentStatus;

    /** 是否具有鉴定资格 */
    private boolean identiQualify;

    /** 是否做过鉴定 */
    private boolean identified=false;

    /** 鉴定结果 */
    private String resultOfIdentify="";

    /** 可否预约专家 */
    private boolean expert;

    /** 申请人数组 */
    private List<unti> applicants;

    /** 被申请人数组 */
    private List<unti> respondents=new ArrayList<>();

    private String stageContent="[]";

    public void addApplicants(String name,String phone,String email){
        if(applicants==null)
            applicants=new ArrayList<>();
        applicants.add(new unti(name,phone,email));
    }
    public void addRespondents(String name,String phone,String email){
        if(respondents==null)
            respondents=new ArrayList<>();
        respondents.add(new unti(name,phone,email));
    }


//    private CurrentStageContent currentStageContent;

//    private String MediationPlace="";
//
//    private String MediationTime="";


//    public MediationStageForm(){
//        currentStageContent=new CurrentStageContent();
//    }
//
//    public void getCurrentStageContent(List<String> particopate,ParticopateContact particopateContact){
//            this.currentStageContent=new CurrentStageContent(particopate,particopateContact);
//
//    }
//
//    @Data
//    class CurrentStageContent{
//        private List<String> particopate;
//        private ParticopateContact particopateContact;
//
//        public CurrentStageContent(){
//            particopate=new ArrayList<>();
//            particopateContact=new ParticopateContact();
//        }
//
//        public CurrentStageContent(List<String> particopate, ParticopateContact particopateContact) {
//            this.particopate = particopate;
//            this.particopateContact = particopateContact;
//        }
//    }
//
//    public ParticopateContact particopateContact(){
//        return new ParticopateContact();
//    }
//
//    @Data
//    public class ParticopateContact{
//        private List<Info1> Applicants;
//        private List<Info1> Respondents;
//        private List<Info2> Experts;
//
//        public ParticopateContact(){
//            Applicants=new ArrayList<>();
//            Respondents=new ArrayList<>();
//            Experts=new ArrayList<>();
//        }
//
//        public void addApplicant(String phone,String email){
//            if(Applicants==null)
//                Applicants=new ArrayList<>();
//            Applicants.add(new Info1(phone,email));
//        }
//        public void addRespondent(String phone,String email){
//            if(Respondents==null)
//                Respondents=new ArrayList<>();
//            Respondents.add(new Info1(phone,email));
//        }
//        public void addExpert(String phone,String email,String name){
//            if(Experts==null)
//                Experts=new ArrayList<>();
//            Experts.add(new Info2(phone,email,name));
//        }
//    }
//
//    class Info1{
//        private String phone;
//        private String email;
//
//        public Info1(String phone, String email) {
//            this.phone = phone;
//            this.email = email;
//        }
//    }
//    class Info2{
//        private String phone;
//        private String email;
//        private String name;
//
//        public Info2(String phone, String email,String name) {
//            this.phone = phone;
//            this.email = email;
//            this.name=name;
//        }
//    }
}
