package com.seu.common;

/**
 * @ClassName InitConstant
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/1 20:20
 * @Version 1.0
 **/
public interface InitConstant {
    String init_mediateStage="[{\n" +
            "\t\"预约调解\": {\n" +
            "\t\t\"applicant\": [],\n" +
            "\t\t\"respondent\": [],\n" +
            "\t\t\"currentStageContent\": {\n" +
            "\t\t\t\"particopate\": [],\n" +
            "\t\t\t\"Applicants\": [{\n" +
            "\t\t\t\t\"phone\": \"\",\n" +
            "\t\t\t\t\"email\": \"\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"Respondents\": [{\n" +
            "\t\t\t\t\"phone\": \"\",\n" +
            "\t\t\t\t\"email\": \"\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"Experts\": [{\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"phone\": \"\",\n" +
            "\t\t\t\t\"email\": \"\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"MediationPlace\": \"\",\n" +
            "\t\t\t\"MediationTime\": \"\"\n" +
            "\t\t}\n" +
            "\n" +
            "\t},\n" +
            "\t\"正在调解\": {\n" +
            "\n" +
            "\t}\n" +
            "}]";

    String mediate_inform_email="%s 您好，请于[%s]到 %s 参加患方 %s 与院方 %s 的医患纠纷调解";
    String damageIdentify= " %s您好，针对案件%s 请尽快办理医疗损害鉴定并及时上传";
}
