package com.seu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

//            for(Object hospital: (com.alibaba.fastjson.JSONArray)involvedInstitute){
//
//                hospitalList.add((String)(((com.alibaba.fastjson.JSONObject)hospital).get("Hospital")));
//            }
            hospitalList.add(((JSONObject)involvedInstitute).getString("Hospital"));
        }

        String personNames="";
        for (String applyPerson: applyPersons){
            personNames=personNames+applyPerson+"、";
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
//            for(Object hospital: (com.alibaba.fastjson.JSONArray)involvedInstitute){
//
//                hospitalList.add((String)(((com.alibaba.fastjson.JSONObject)hospital).get("Hospital")));
//            }
//            hospitals = "";
//            for(String hospital: hospitalList){
//                hospitals = hospitals + hospital + "、";
//            }
//            hospitals = hospitals.substring(0,hospitals.length() - 1);
            hospitals = ((JSONObject)involvedInstitute).getString("Hospital");
            detail = detail + hospitals + "存在医疗纠纷。在医疗行为环节"+((com.alibaba.fastjson.JSONObject) stage).getString("name")+"中，";

            //获取resultOfRegConflict列表
            JSONArray resultOfRegConflict = ((JSONObject) stage).getJSONArray("resultOfRegConflict");
            for(Object eachResult: resultOfRegConflict){

                if(
                         ((JSONObject)eachResult).getString("test").isEmpty()


                        && ((List<String>)((JSONObject)eachResult).get("operation")).size() == 0

                        && ((JSONObject)eachResult).getString("medicine").isEmpty()

                        && ((JSONObject)eachResult).getString("added").isEmpty()

                        && ((JSONObject)eachResult).getString("syndrome").isEmpty()


                        && ((List<String>)((JSONObject) eachResult).get("diseasesymptomAfter")).size() ==0


                        && ((List<String>)((JSONObject) eachResult).get("diseaseAfter")).size() ==0

                        && ((List<String>)((JSONObject) eachResult).get("defaultBehavior")).size() ==0

                        && ((List<String>)((JSONObject) eachResult).get("diseaseBefore")).size() ==0

                        && ((List<String>)((JSONObject) eachResult).get("diseasesymptomBefore")).size() ==0)

                    continue;

                if( ((List<String>)((JSONObject) eachResult).get("diseasesymptomBefore")).size() !=0 ){
                    detail = detail + "患者因存在症状"
                            + ((List<String>)(((JSONObject)eachResult).get("diseasesymptomBefore"))).stream().collect(Collectors.joining("、"))
                            + "在"
                            + hospitals
                            + "就诊。";
                }

                if ( ((List<String>)((JSONObject) eachResult).get("diseaseBefore")).size() !=0 ){
                    detail = detail + "院方诊断为"
                            + ((List<String>)(((JSONObject)eachResult).get("diseaseBefore"))).stream().collect(Collectors.joining("、"));
                }


                if( ((JSONObject)eachResult).get("test") != null && !((JSONObject)eachResult).getString("test").isEmpty() ){
                    detail = detail + "。医院进行了" + ((JSONObject)eachResult).getString("test") + "检测。";
                }
                if( ((JSONObject)eachResult).get("operation") != null && ((List<String>)((JSONObject)eachResult).get("operation")).size() != 0 ){
                    detail = detail + "医院进行了" + ((JSONObject)eachResult).getString("operation") + "手术。";
                }

                if( ((JSONObject)eachResult).get("medicine") != null && !((JSONObject)eachResult).getString("medicine").isEmpty() ){
                    detail = detail + "医方对患者使用了" + ((JSONObject)eachResult).getString("medicine") + "药品。";
                }
                if( ((JSONObject)eachResult).get("added") != null && !((JSONObject)eachResult).getString("added").isEmpty() ){
                    detail = detail + "（备注：" + ((JSONObject)eachResult).getString("added") + "。）";
                }

                if( ((JSONObject)eachResult).get("syndrome") != null && !((JSONObject)eachResult).getString("syndrome").isEmpty() ){
                    detail = detail + "患者出现并发症" + ((JSONObject)eachResult).getString("syndrome") + "。";
                }

                if( ((JSONObject)eachResult).get("diseasesymptomAfter") != null && ((List<String>)((JSONObject) eachResult).get("diseasesymptomAfter")).size() !=0 ){
                    detail = detail
                            + "院方一系列医疗行为导致患者出现"
                            + ((List<String>)(((JSONObject)eachResult).get("diseasesymptomAfter"))).stream().collect(Collectors.joining("、"))
                            + "症状";
                }

                if (((JSONObject) eachResult).get("diseasemAfter") != null && ((List<String>)((JSONObject) eachResult).get("diseaseAfter")).size() !=0) {
                    detail = detail
                            + "使患者患有"
                            + ((List<String>)(((JSONObject) eachResult).get("diseasemAfter"))).stream().collect(Collectors.joining("、"))
                            + "。";
                }

                if (((JSONObject) eachResult).get("name") != null && !((JSONObject) eachResult).getString("name").isEmpty()) {
                    detail = detail
                            + "院方可能在"
                            + ((JSONObject) eachResult).getString("name")
                            + "失误。";
                }

                if (((JSONObject) eachResult).get("defaultBehavior") != null && ((List<String>)((JSONObject) eachResult).get("defaultBehavior")).size() !=0) {
                    detail = detail.substring(0, detail.length() - 1);
                    detail = detail
                            + "，具体包括"
                            + ((List<String>)(((JSONObject) eachResult).get("defaultBehavior"))).stream().collect(Collectors.joining("、"))
                            + "。";
                }
            }
            if( !((JSONObject) stage).getString("ResultOfDamage").isEmpty() )
                detail = detail  + "在本阶段医疗行为中，最终造成患者"+ ((JSONObject) stage).getString("ResultOfDamage")+ "损害结果。";
            if( !((JSONObject) stage).getString("ResultOfDamageDetail").isEmpty() )
                detail = detail  + "详情：" + ((JSONObject) stage).getString("ResultOfDamageDetail");

//            detail = detail + hospitals + "存在医疗纠纷。患者因存在";
//            Object resultList = ((com.alibaba.fastjson.JSONObject) stage).get("ResultList");
//            if(resultList==null)
//                resultList="{}";
//            Object diseasesymptomBefore = ((com.alibaba.fastjson.JSONObject) stage).get("DiseasesymptomBefore");
//            if(diseasesymptomBefore==null)
//                diseasesymptomBefore="";
//            Object diseaseListBefore = ((com.alibaba.fastjson.JSONObject) stage).get("DiseaseListBefore");
//            if(diseaseListBefore==null)
//                diseaseListBefore="[]";
//            JSONArray arrDLB=JSONArray.parseArray(diseaseListBefore.toString());
//            String diseaseName="";
//            for(int i=0;i<arrDLB.size();++i)
//                diseaseName+=arrDLB.getJSONObject(i).get("DiseaseName")+",";
//            if(diseaseName!="")
//                diseaseName=diseaseName.substring(0,diseaseName.length()-1);
//            if(diseasesymptomBefore!="")
//                detail = detail + ((String)diseasesymptomBefore) + "症状，到" + hospitals + "就诊.";
//            if(diseaseName!="")
//                detail = detail + "诊断为" + (String)diseaseName + "。";
//            JSONObject resultListJsObj=JSONObject.parseObject(resultList.toString());
//            Object behavior=null;
//            //ResultList
//            //1.诊断
//            Object diagnosis=resultListJsObj.get("diagnosis");
//            if(diagnosis!=null){
//                behavior = ((com.alibaba.fastjson.JSONObject) diagnosis).get("behavior");
//                if (((List<String>)behavior).size() != 0){
//                    String diagnosisFailure= "";
//                    for(String b: (List<String>)behavior){
//                        diagnosisFailure = diagnosisFailure + b +"、";
//                    }
//                    diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
//                    detail = detail + "院方可能存在诊断失误" + diagnosisFailure+"。";
//                }
//            }
//
//            //2.检查
//            Object verification = resultListJsObj.get("verification");
//            if(verification!=null){
//                behavior = ((com.alibaba.fastjson.JSONObject) verification).get("behavior");
//                if (((List<String>)behavior).size() != 0){
//                    String diagnosisFailure= "";
//                    for(String b: (List<String>)behavior){
//                        diagnosisFailure = diagnosisFailure + b +"、";
//                    }
//                    diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
//                    detail = detail + "院方可能存在检查失误" + diagnosisFailure+ "。";
//                }
//                Object test =((com.alibaba.fastjson.JSONObject) verification).get("testt");
//                if((String)test != ""){
//                    detail = detail + "院方检查行为是"+test+"。";
//                }
//            }
//
//            //3.shoushu
//            Object operator = resultListJsObj.get("operator");
//            if(operator!=null){
//                behavior = ((com.alibaba.fastjson.JSONObject) operator).get("behavior");
//                if (((List<String>)behavior).size() != 0){
//                    String diagnosisFailure= "";
//                    for(String b: (List<String>)behavior){
//                        diagnosisFailure = diagnosisFailure + b +"、";
//                    }
//                    diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
//                    detail = detail + "院方可能存在手术失误" + diagnosisFailure+ "。";
//                }
//                Object operation =((com.alibaba.fastjson.JSONObject) operator).get("operation");
//                if((String)operation != ""){
//                    detail = detail + "院方手术行为是"+operation+"。";
//                }
//                Object syndrome =((com.alibaba.fastjson.JSONObject) operator).get("syndrome");
//                if((String)syndrome != ""){
//                    detail = detail + "造成患者"+syndrome+"。";
//                }
//            }
//
//
//            // zhiliao
//            Object treatment = resultListJsObj.get("treatment");
//            if(treatment!=null){
//                behavior = ((com.alibaba.fastjson.JSONObject) treatment).get("behavior");
//                if (((List<String>)behavior).size() != 0){
//                    String diagnosisFailure= "";
//                    for(String b: (List<String>)behavior){
//                        diagnosisFailure = diagnosisFailure + b +"、";
//                    }
//                    diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
//                    detail = detail + "院方可能存在治疗失误" + diagnosisFailure+"。";
//                }
//            }
//
//
//            // yongyao
//            Object medcine = resultListJsObj.get("medcine");
//            if(medcine!=null){
//                behavior = ((com.alibaba.fastjson.JSONObject) medcine).get("behavior");
//                if (((List<String>)behavior).size() != 0){
//                    String diagnosisFailure= "";
//                    for(String b: (List<String>)behavior){
//                        diagnosisFailure = diagnosisFailure + b +"、";
//                    }
//                    diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
//                    detail = detail + "院方可能存在用药失误" + diagnosisFailure+ "。";
//                }
//                Object medicine =((com.alibaba.fastjson.JSONObject) medcine).get("medicine");
//                if((String)medicine != ""){
//                    detail = detail + "院方使用药品包括"+medicine+"。";
//                }
//            }
//
//            // shuxue
//            Object transfusion = resultListJsObj.get("transfusion");
//            if(transfusion!=null){
//                behavior = ((com.alibaba.fastjson.JSONObject) transfusion).get("behavior");
//                if (((List<String>)behavior).size() != 0){
//                    String diagnosisFailure= "";
//                    for(String b: (List<String>)behavior){
//                        diagnosisFailure = diagnosisFailure + b +"、";
//                    }
//                    diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
//                    detail = detail + "院方可能存在输血失误" + diagnosisFailure+"。";
//                }
//            }
//
//
//            // mazui
//            Object anesthesia = resultListJsObj.get("anesthesia");
//            if(anesthesia!=null){
//                behavior = ((com.alibaba.fastjson.JSONObject) anesthesia).get("behavior");
//                if (((List<String>)behavior).size() != 0){
//                    String diagnosisFailure= "";
//                    for(String b: (List<String>)behavior){
//                        diagnosisFailure = diagnosisFailure + b +"、";
//                    }
//                    diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
//                    detail = detail + "院方可能存在麻醉失误" + diagnosisFailure+"。";
//                }
//            }
//
//            // guanli
//            Object management = resultListJsObj.get("management");
//            if(management!=null){
//                behavior = ((com.alibaba.fastjson.JSONObject) management).get("behavior");
//                if (((List<String>)behavior).size() != 0){
//                    String diagnosisFailure= "";
//                    for(String b: (List<String>)behavior){
//                        diagnosisFailure = diagnosisFailure + b +"、";
//                    }
//                    diagnosisFailure = diagnosisFailure.substring(0, diagnosisFailure.length() - 1);
//                    detail = detail + "院方可能存在管理失误" + diagnosisFailure+"。";
//                }
//            }
//
//
//            Object diseasesymptomAfter = ((com.alibaba.fastjson.JSONObject) stage).get("DiseasesymptomAfter");
//            if(diseasesymptomAfter!=null)
//                detail = detail + "经过一系列医疗行为，患者出现" + (String)diseasesymptomAfter + "症状。";
//            Object resultOfDamage = ((com.alibaba.fastjson.JSONObject) stage).get("ResultOfDamage");
//            if(resultOfDamage!=null)
//                detail = detail + "造成" + (String)resultOfDamage + "损害结果。";
//            hospitalList = new ArrayList<>();
        }
        Map map = new HashMap();
        map.put("title", title);
        map.put("detail", detail);
        return map;
    }
}
