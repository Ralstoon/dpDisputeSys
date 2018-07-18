package com.seu.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/*
工具类，获取当前时间，格式为yyyy-MM-dd HH:mm

 */
public class CurrentTimeUtil {

    public static String getCurrentTime(){
        Date d=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(d);
    }
}
