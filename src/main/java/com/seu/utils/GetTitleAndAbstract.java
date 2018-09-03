package com.seu.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GetTitleAndAbstract
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/3 14:18
 * @Version 1.0
 **/
public class GetTitleAndAbstract {
    public static Map generateCaseTitleDetail(String stageConstant, List<String> applyPersons){
        com.alibaba.fastjson.JSONArray arr= com.alibaba.fastjson.JSONArray.parseArray(stageConstant);
        List<String> hospitalList = new ArrayList<>();
        String hospitals = "";

        for (Object stage:arr){
            Object involvedInstitute = ((com.alibaba.fastjson.JSONObject) stage).get("InvolvedInstitute");

            for(Object hospital: (com.alibaba.fastjson.JSONArray)involvedInstitute){

                hospitalList.add((String)(((com.alibaba.fastjson.JSONObject)hospital).get("Hospital")));
            }
        }

        String personNames="";
        for (String applyPerson: applyPersons){
            personNames=personNames+applyPerson;
        }
        personNames = personNames.substring(0,personNames.length() - 1);
        for(String hospital: hospitalList){
            hospitals = hospitals + hospital + "、";
        }
        hospitals = hospitals.substring(0,hospitals.length() - 1);
        String title = personNames + "与" + hospitals + "医疗纠纷";

        hospitalList = new ArrayList<>();
        String detail = "";
        for(Object stage : arr){
            detail = detail + "患者与";
            Object involvedInstitute = ((com.alibaba.fastjson.JSONObject) stage).get("InvolvedInstitute");
            for(Object hospital: (com.alibaba.fastjson.JSONArray)involvedInstitute){

                hospitalList.add((String)(((com.alibaba.fastjson.JSONObject)hospital).get("Hospital")));
            }
            hospitals = "";
            for(String hospital: hospitalList){
                hospitals = hospitals + hospital + "、";
            }
            hospitals = hospitals.substring(0,hospitals.length() - 1);
            detail = detail + hospitals + "存在医疗纠纷。患者因存在";
            Object possibleFailure = ((com.alibaba.fastjson.JSONObject) stage).get("PossibleFailure");
            Object diseasesymptomBefore = ((com.alibaba.fastjson.JSONObject) stage).get("DiseasesymptomBefore");
            Object diseaseListBefore = ((com.alibaba.fastjson.JSONObject) stage).get("DiseaseListBefore");
            Object diseaseName = ((com.alibaba.fastjson.JSONObject) diseaseListBefore).get("DiseaseName");
            detail = detail + ((String)diseasesymptomBefore) + "症状，到" + hospitals + "就诊。诊断为" + (String)diseaseName + "。";

            //PossibleFailure
            //1.诊断
            Object diagnosis = ((com.alibaba.fastjson.JSONObject) possibleFailure).get("diagnosis");
            Object behavior = ((com.alibaba.fastjson.JSONObject) diagnosis).get("behavior");
            if (((List<String>)behavior).size() != 0){
                String diagnosisFailure= "";
                for(String b: (List<String>)behavior){
                    diagnosisFailure = diagnosisFailure + b +"、";
                }
                diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
                detail = detail + "院方可能存在诊断失误" + diagnosisFailure+"。";
            }
            //2.检查
            Object verification = ((com.alibaba.fastjson.JSONObject) possibleFailure).get("verification");
            behavior = ((com.alibaba.fastjson.JSONObject) verification).get("behavior");
            if (((List<String>)behavior).size() != 0){
                String diagnosisFailure= "";
                for(String b: (List<String>)behavior){
                    diagnosisFailure = diagnosisFailure + b +"、";
                }
                diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
                detail = detail + "院方可能存在检查失误" + diagnosisFailure+ "。";
            }
            Object test =((com.alibaba.fastjson.JSONObject) verification).get("test");
            if((String)test != ""){
                detail = detail + "院方检查行为是"+test+"。";
            }
            //3.shoushu
            Object operator = ((com.alibaba.fastjson.JSONObject) possibleFailure).get("operator");
            behavior = ((com.alibaba.fastjson.JSONObject) operator).get("behavior");
            if (((List<String>)behavior).size() != 0){
                String diagnosisFailure= "";
                for(String b: (List<String>)behavior){
                    diagnosisFailure = diagnosisFailure + b +"、";
                }
                diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
                detail = detail + "院方可能存在手术失误" + diagnosisFailure+ "。";
            }
            Object operation =((com.alibaba.fastjson.JSONObject) operator).get("operation");
            if((String)operation != ""){
                detail = detail + "院方手术行为是"+operation+"。";
            }
            Object syndrome =((com.alibaba.fastjson.JSONObject) operator).get("syndrome");
            if((String)syndrome != ""){
                detail = detail + "造成患者"+syndrome+"。";
            }

            //
            Object treatment = ((com.alibaba.fastjson.JSONObject) possibleFailure).get("treatment");
            behavior = ((com.alibaba.fastjson.JSONObject) treatment).get("behavior");
            if (((List<String>)behavior).size() != 0){
                String diagnosisFailure= "";
                for(String b: (List<String>)behavior){
                    diagnosisFailure = diagnosisFailure + b +"、";
                }
                diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
                detail = detail + "院方可能存在治疗失误" + diagnosisFailure+"。";
            }

            //
            Object medcine = ((com.alibaba.fastjson.JSONObject) possibleFailure).get("medcine");
            behavior = ((com.alibaba.fastjson.JSONObject) medcine).get("behavior");
            if (((List<String>)behavior).size() != 0){
                String diagnosisFailure= "";
                for(String b: (List<String>)behavior){
                    diagnosisFailure = diagnosisFailure + b +"、";
                }
                diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
                detail = detail + "院方可能存在用药失误" + diagnosisFailure+ "。";
            }
            Object medicine =((com.alibaba.fastjson.JSONObject) medcine).get("medicine");
            if((String)medicine != ""){
                detail = detail + "院方使用药品包括"+medicine+"。";
            }
            //
            Object transfusion = ((com.alibaba.fastjson.JSONObject) possibleFailure).get("transfusion");
            behavior = ((com.alibaba.fastjson.JSONObject) transfusion).get("behavior");
            if (((List<String>)behavior).size() != 0){
                String diagnosisFailure= "";
                for(String b: (List<String>)behavior){
                    diagnosisFailure = diagnosisFailure + b +"、";
                }
                diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
                detail = detail + "院方可能存在输血失误" + diagnosisFailure+"。";
            }

            //
            Object anesthesia = ((com.alibaba.fastjson.JSONObject) possibleFailure).get("anesthesia");
            behavior = ((com.alibaba.fastjson.JSONObject) anesthesia).get("behavior");
            if (((List<String>)behavior).size() != 0){
                String diagnosisFailure= "";
                for(String b: (List<String>)behavior){
                    diagnosisFailure = diagnosisFailure + b +"、";
                }
                diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
                detail = detail + "院方可能存在麻醉失误" + diagnosisFailure+"。";
            }

            Object management = ((com.alibaba.fastjson.JSONObject) possibleFailure).get("management");
            behavior = ((com.alibaba.fastjson.JSONObject) management).get("behavior");
            if (((List<String>)behavior).size() != 0){
                String diagnosisFailure= "";
                for(String b: (List<String>)behavior){
                    diagnosisFailure = diagnosisFailure + b +"、";
                }
                diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
                detail = detail + "院方可能存在管理失误" + diagnosisFailure+"。";
            }

            Object diseasesymptomAfter = ((com.alibaba.fastjson.JSONObject) stage).get("DiseasesymptomAfter");
            detail = detail + "经过一系列医疗行为，患者出现" + (String)diseasesymptomAfter + "症状。";
            Object resultOfDamage = ((com.alibaba.fastjson.JSONObject) stage).get("ResultOfDamage");
            detail = detail + "造成" + (String)resultOfDamage + "损害结果。";
            hospitalList = new ArrayList<>();
        }
        Map map = new HashMap();
        map.put("title", title);
        map.put("detail", detail);
        return map;
    }
}
