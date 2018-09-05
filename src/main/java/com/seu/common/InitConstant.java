package com.seu.common;

/**
 * @ClassName InitConstant
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/1 20:20
 * @Version 1.0
 **/
public interface InitConstant {
    String currentStageContent="{\n" +
            "\t\t\"particopate\": [],\n" +
            "\t\t\"particopateContact\": {\n" +
            "\t\t\t\"expertChoosed\": {\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"id\": \"\",\n" +
            "\t\t\t\t\"phone\": \"\",\n" +
            "\t\t\t\t\"email\": \"\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"mediationPlace\": \"\",\n" +
            "\t\t\t\"mediationTime\": \"\",\n" +
            "\t\t\t\"currentFiles\": []\n" +
            "\t\t}\n" +
            "\t}";
    String init_mediateStage="{\n" +
            "\t\"stage\": \"\", \n" +
            "\t\"currentStatus\": -1,  \n" +
            "\t\"applicants\": [],\n" +
            "\t\"respondents\": [],\n" +
            "\t\"identiQualify\": true,  \n" +
            "\t\"identified\": false,  \n" +
            "\t\"resultOfIdentify\": \"\",  \n" +
            "\t\"expert\": false,  \n" +
            "\t\"stageContent\": [{\n" +
            "\t\t\"particopate\": [],  \n" +
            "\t\t\"particopateContact\": {\n" +
            "\t\t\t\"expertChoosed\": {\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"id\": \"\",\n" +
            "\t\t\t\t\"phone\": \"\",\n" +
            "\t\t\t\t\"email\": \"\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"mediationPlace\": \"\",  \n" +
            "\t\t\t\"mediationTime\": \"\",  \n" +
            "\t\t\t\"currentFiles\": []\n" +
            "\t\t}\n" +
            "\t}]\n" +
            "}";
    String mediate_inform_email="%s 您好，请于[%s]到 %s 参加患方 %s 与院方 %s 的医患纠纷调解";
    String damageIdentify= " %s您好，针对案件%s 请尽快办理医疗损害鉴定并及时上传";


}
