package com.seu.controller;

import com.seu.common.EndDate;
import com.seu.domian.Disputecase;
import com.seu.utils.GetWorkingTimeUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class isWeekday {
    public static void main(String[] args) throws Exception {

        FileInputStream freader;
        freader = new FileInputStream("C:\\Users\\wangj\\Desktop\\isWeekday.txt");
        ObjectInputStream objectInputStream = new ObjectInputStream(freader);

        EndDate.isWeekday = (Map<Date, Integer>) objectInputStream.readObject();



        GetWorkingTimeUtil getWorkingTimeUtil = new GetWorkingTimeUtil();
        Date currentDate = new Date();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(currentDate);

        Map<Date, Integer> hh = new HashMap<Date, Integer>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        for (int i = 0; i < 365; i++) {
            hh.put(sdf.parse(sdf.format(c1.getTime())), getWorkingTimeUtil.getResult(c1.getTime()));
            c1.add(Calendar.DAY_OF_MONTH, 1);
        }

        FileOutputStream outStream = new FileOutputStream("E:/1.txt");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);



        objectOutputStream.writeObject(hh);

        outStream.close();
    }
}
