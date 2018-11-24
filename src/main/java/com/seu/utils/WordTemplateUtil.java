package com.seu.utils;

import com.alibaba.fastjson.JSONObject;
import com.seu.domian.DisputecaseAccessory;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.service.DisputecaseAccessoryService;
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
    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;
    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;

    public String createWord(Map map,String templateName){
        try {
            //文件路径
            String filePath = "/home/ubuntu/";
//            String filePath = "C:/Users/Administrator/Desktop/";
            File file=new File(filePath+templateName+".doc");
            /** 生成word */
            WordUtil.createWord(map, templateName+".ftl", filePath, templateName+".doc");

            String url = "";
            String disputeId = map.get("caseId").toString();
            DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputeId);
            if (templateName.equals("南京市医调委不予受理通知书") || templateName.equals("南京市医调委案件受理通知书")){
                FileInputStream inputStream = new FileInputStream(file);
                url = disputecaseAccessoryService.uploadFile(inputStream, disputeId+"/"+ file.getName());
                disputecaseAccessory.setAcceptanceNotice("http://"+url);
                disputecaseAccessoryRepository.save(disputecaseAccessory);
            }

            if (templateName.equals("司法确认申请书")){
                FileInputStream inputStream =  new FileInputStream(file);
                url = disputecaseAccessoryService.uploadFile(inputStream, file.getName());
                JSONObject result = JSONObject.parseObject("{}");
                result.put("judicialConfirmFile", url);
                disputecaseAccessory.setJudicialConfirm(result.toJSONString());
                disputecaseAccessoryRepository.save(disputecaseAccessory);
            }

            return url;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}




