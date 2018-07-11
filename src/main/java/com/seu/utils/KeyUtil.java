package com.seu.utils;

import java.util.Random;

public class KeyUtil {
    /**
     * 生成唯一的主键
     * 格式：时间+随机数
     */
    //synchronized关键词为了防止多线程中产生重复
    public static synchronized String genUniqueKey(){
        Random random=new Random();
        Integer a=random.nextInt(900000)+100000; //生成6位随机数
        return System.currentTimeMillis()+String.valueOf(a);
    }
}
