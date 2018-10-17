package com.seu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName GetHospitalUtil
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/2 16:50
 * @Version 1.0 todo：涉事医院 wj 18/10/15
 **/
public class GetHospitalUtil {
    public static List<String> extract(String stageContent){
        Set<String> respondent=new HashSet<>();
        JSONArray arr=JSONArray.parseArray(stageContent);
        for(int i=0;i<arr.size();++i){
            JSONObject obj=arr.getJSONObject(i);
            JSONObject InvolvedInstitute=obj.getJSONObject("InvolvedInstitute");

                String temp="";
//                temp+=subObj.getString("City")+"_"+subObj.getString("Zone")+"_"+subObj.getString("Hospital");
                temp=InvolvedInstitute.getString("Hospital").trim();
                respondent.add(temp);

        }
        return new ArrayList<>(respondent);
    }
}
