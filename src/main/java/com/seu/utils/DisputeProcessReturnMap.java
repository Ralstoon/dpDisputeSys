package com.seu.utils;

import java.util.HashMap;
import java.util.Map;

/*
工具类，目前先用来初始化返回的Data类型中数据
有： finish time，current task，assignee
 */
public class DisputeProcessReturnMap {
    public static Map<String,Object> initDisputeProcessReturnMap(String taskName,String assignee){
        Map<String,Object> map=new HashMap<>();
        map.put("current task",taskName);
        map.put("finish time",CurrentTimeUtil.getCurrentTime());
        map.put("assignee",assignee);

        return map;
    }
}
