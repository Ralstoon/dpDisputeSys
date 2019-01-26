package com.seu.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class test {
    public static void  main(String[] args) throws InterruptedException {
        String ss= "中美贸易战";
        System.out.println(ss.contains("贸ee易"));

        System.out.println(ss.substring(1,2));
        Date date = new Date();
        System.out.println(date);


        Thread.sleep(5000);
        Date date2 = new Date();
        System.out.println((int)(date2.getTime() - date.getTime())/1000);

    }
}
