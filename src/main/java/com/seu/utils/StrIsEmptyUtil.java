package com.seu.utils;

/**
 * @ClassName StrIsEmptyUtil
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/11 0011 下午 10:06
 * @Version 1.0
 **/
public class StrIsEmptyUtil {
    public static boolean isEmpty(String s){
        if(s==null || s.trim()==""||s.trim().equals(""))
            return true;
        else
            return false;
    }
}
