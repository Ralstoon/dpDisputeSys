package com.seu.utils;

import com.alibaba.fastjson.JSONObject;
import com.seu.domian.DisputecaseAccessory;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.service.DisputecaseAccessoryService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

    public static void main(String[] args){
        try {
            Map<String,Object> map=new HashMap<>();
            map.put("param1","param1");
            map.put("param2","param2");
            map.put("param3","param3");
            map.put("param4","param4");
            map.put("param5","param5");
            map.put("param6","param6");
            map.put("check1",true);
            map.put("check2",true);
            map.put("check3",true);
            map.put("check4",true);
            map.put("check5",true);
            map.put("check6",true);
            map.put("check7",true);
            //文件路径
//            String filePath = "/home/ubuntu/";
            String filePath = "C:/Users/Administrator/Desktop/";
            File file=new File(filePath+"temp"+".doc");
            /** 生成word */
//            WordUtil.createWord(map, "C:/Users/Administrator/Desktop/no"+".ftl", filePath, "temp"+".doc");
            Configuration configuration = new Configuration();

            //设置编码
            configuration.setDefaultEncoding("UTF-8");

            //ftl模板文件
//            configuration.setClassForTemplateLoading(WordUtil.class,"/");
            configuration.setClassForTemplateLoading(WordUtil.class,"/");
            //获取模板
            Template template = configuration.getTemplate("南京市医调委不予受理通知书.ftl");

            //输出文件
            File outFile = new File(filePath+File.separator+"temp.doc");

            //如果输出目标文件夹不存在，则创建
            if (!outFile.getParentFile().exists()){
                outFile.getParentFile().mkdirs();
            }

            //将模板和数据模型合并生成文件
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));


            //生成文件
            template.process(map, out);

            //关闭流
            out.flush();
            out.close();




        }catch (Exception e){
            e.printStackTrace();
//            return null;
        }
    }


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




