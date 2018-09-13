package com.seu.utils;

import com.alibaba.fastjson.JSONObject;
import com.seu.common.InitConstant;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Component
public class GetWorkingTimeUtil {

    private Integer getResult(Date curTime) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String s=sdf.format(curTime);
        URL url=new URL(InitConstant.getTimeUrl+s);
        BufferedReader in=null;
        StringBuffer sb=new StringBuffer();
        in=new BufferedReader(new InputStreamReader(url.openStream(),"utf-8"));
        String str=null;
        while ((str=in.readLine())!=null)
            sb.append(str);
        JSONObject obj=JSONObject.parseObject(sb.toString());
        Integer result=obj.getInteger("data");
        return result;
    }

    /**
     * 计算从当前日期开始，经过limitTime个工作日后的首个工作日日期
     * @param curTime
     * @param limitTime
     * @return
     */
    public Date calWorkingTime(Date curTime,int limitTime) throws Exception{
        Date endTime=curTime;
        Calendar c=Calendar.getInstance();
        while (limitTime!=0){
            Integer result=getResult(endTime);
            if(result==0){
                limitTime=limitTime-1;
            }
            c.setTime(endTime);
            c.add(Calendar.DAY_OF_MONTH,1);
            endTime=c.getTime();
        }
        return endTime;
    }

}
