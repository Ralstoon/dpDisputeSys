package com.seu.utils;

import com.alibaba.fastjson.JSONObject;
import com.seu.common.EndDate;
import com.seu.common.InitConstant;
import com.seu.domian.DisputecaseProcess;
import com.seu.repository.DisputecaseProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class GetWorkingTimeUtil {

    public Integer getResult(Date curTime) throws Exception {
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
    //@Async
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


    @Autowired
    private DisputecaseProcessRepository disputecaseProcessRepository;
    public Object calRemainTime(String caseId) throws Exception{
        DisputecaseProcess dp=disputecaseProcessRepository.findByDisputecaseId(caseId);
        Date endTime=dp.getEndtimeDisputecase();
        if(endTime==null)
            return null;
        Date currentTime=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        endTime=sdf.parse(sdf.format(endTime));
        currentTime=sdf.parse(sdf.format(currentTime));

        Calendar c=Calendar.getInstance();
        int countdown=0;
        if (EndDate.isWeekday==null){

            FileInputStream freader;
            freader = new FileInputStream("/home/ubuntu/isWeekday.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(freader);

            EndDate.isWeekday = (Map<Date, Integer>) objectInputStream.readObject();

//            Date currentDate = new Date();
//            Calendar c1=Calendar.getInstance();
//            c1.setTime(currentDate);
////            EndDate.isWeekday.clear();
//            EndDate.isWeekday = new HashMap<>();
//            for(int i = 0; i<60; i++){
//
//                EndDate.isWeekday.put(sdf.parse(sdf.format(c1.getTime())), getResult(c1.getTime()));
//                c1.add(Calendar.DAY_OF_MONTH,1);
//            }
        }

        while(currentTime.getTime()<=endTime.getTime()){
            Integer result = EndDate.isWeekday.get(currentTime);
            if(result==0)
                countdown+=1;
            c.setTime(currentTime);
            c.add(Calendar.DAY_OF_MONTH,1);
            currentTime=c.getTime();
        }
        countdown=30;
        return countdown;
    }
}
