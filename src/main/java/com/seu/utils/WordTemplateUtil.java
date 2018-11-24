package com.seu.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName wordTemplateUtil
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/11/23 0023 下午 5:15
 * @Version 1.0
 **/
@Component
public class WordTemplateUtil {

    public File createWord(Map dataMap,String templateName){
        try {
            //文件路径
//            String filePath = "/home/ubuntu/";
            String filePath = "C:/Users/Administrator/Desktop/";
//            File file=new File(filePath+templateName+".doc");
            /** 生成word */
            WordUtil.createWord(dataMap, templateName+".ftl", filePath, templateName+".doc");
            return new File(filePath+templateName+".doc");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}




