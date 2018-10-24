package com.seu.controller;

import com.seu.domian.Disputecase;
import org.python.antlr.ast.Str;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName temp
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/24 0024 下午 9:46
 * @Version 1.0
 **/
public class temp {
    public static void main(String[] args) throws Exception{
        List<Date> dateList=new ArrayList<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        dateList.add(sdf.parse("2018-09-03"));
        dateList.add(sdf.parse("2019-09-03"));
        dateList.add(sdf.parse("2018-09-03"));

        List<Date> templist=dateList;
        Collections.sort(templist, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                try{
                    Date dt1 = o1;
                    Date dt2 = o2;
                    if (dt1.getTime() > dt2.getTime()) {
                        return 1;
                    } else if (dt1.getTime() < dt2.getTime()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return 0;
            }
        });
        System.out.println(templist);
    }
}
