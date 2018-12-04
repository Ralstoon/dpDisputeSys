package com.seu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.*;
import com.seu.repository.*;
import com.seu.service.DisputecaseAccessoryService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
//            map.put("param1","param1");
//            map.put("param2","param2");
//            map.put("param3","param3");
//            map.put("param4","param4");
//            map.put("param5","param5");
//            map.put("param6","param6");
//            map.put("param7","param7");
//            map.put("param8","param8");
            List<Map<String,Object>> columns=new ArrayList<>();
            for(int i=0;i<2;++i){
                Map<String,Object> col=new HashMap<>();
                col.put("param1","param1");
                col.put("param2","param2");
                col.put("param3","param3");
                col.put("param4","param4");
                col.put("param5","param5");
                col.put("param6","param6");
                col.put("param7","param7");
                col.put("param8","param8");
                columns.add(col);
            }
            map.put("columns",columns);
            map.put("param9","param9");
            map.put("param10","param10");
            map.put("param11","param11");
            map.put("param12","param12");
            map.put("param13","param13");
//            map.put("param14","param14");
//            map.put("param15","param15");


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
            Template template = configuration.getTemplate("司法确认申请书.ftl");
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
        }
    }

    /**
     * 生成模板并返回下载链接的函数
     * @param
     * @param templateName
     * @return
     */
    public ResultVO createWord(JSONArray array,String templateName,String disputeId) {
        try {
            //文件路径
//            String filePath = "/home/ubuntu/";
            String filePath = "C:/Users/Administrator/Desktop/";
            JSONArray res=JSONArray.parseArray("[]");


            DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputeId);
            if (templateName.equals("南京市医调委不予受理通知书") || templateName.equals("南京市医调委案件受理通知书")) {
                File file = new File(filePath + templateName + ".doc");
                WordUtil.createWord(array.getJSONObject(0), templateName + ".ftl", filePath, templateName + ".doc");
                FileInputStream inputStream = new FileInputStream(file);
                String url = "http://"+disputecaseAccessoryService.uploadFile(inputStream, disputeId + "/" + file.getName());
                JSONObject obj=JSONObject.parseObject("{}");
                obj.put("templateName",templateName);
                obj.put("url",url);
                res.add(obj);
                disputecaseAccessory.setAcceptanceNotice(obj.toString());
                disputecaseAccessoryRepository.save(disputecaseAccessory);
            } else if (templateName.equals("司法确认申请书")) {
                int num=array.size();
                for(int i=0;i<num;++i){
                    JSONObject obj=JSONObject.parseObject("{}");
                    File file = new File(filePath + templateName+ i + ".doc");
                    WordUtil.createWord(array.getJSONObject(i), templateName + ".ftl", filePath, templateName+ i + ".doc");
                    FileInputStream inputStream = new FileInputStream(file);
                    String url="http://" +disputecaseAccessoryService.uploadFile(inputStream, file.getName());
                    obj.put("url",url);
                    obj.put("templateName",templateName+i);
                    obj.put("hosName",array.getJSONObject(i).getString("param9"));
                    res.add(obj);
                }
                disputecaseAccessory.setJudicialConfirm(res.toString());
                disputecaseAccessoryRepository.save(disputecaseAccessory);
            } else if (templateName.equals("南京市医患纠纷人民调解专家咨询申请书")) {
                File file = new File(filePath + templateName + ".doc");
                WordUtil.createWord(array.getJSONObject(0), templateName + ".ftl", filePath, templateName + ".doc");
                FileInputStream inputStream = new FileInputStream(file);
                String url = "http://"+disputecaseAccessoryService.uploadFile(inputStream, file.getName());
//                String temp = disputecaseAccessory.getAppointExpert();
//                JSONObject appointExpert;
//                if (StrIsEmptyUtil.isEmpty(temp)) {
//                    appointExpert = JSONObject.parseObject("{}");
//                    appointExpert.put("application", "");
//                    appointExpert.put("applicationDetail", JSONArray.parseArray("[]"));
//                } else
//                    appointExpert = JSONObject.parseObject(temp);
//                appointExpert.put("application", url);
                JSONObject obj=JSONObject.parseObject("{}");
                obj.put("templateName",templateName);
                obj.put("url",url);
                res.add(obj);
                disputecaseAccessory.setAppointExpert(obj.toString());
                disputecaseAccessoryRepository.save(disputecaseAccessory);
            } else if (templateName.equals("人民调解协议书")) {
                int num=array.size();
                for(int i=0;i<num;++i){
                    JSONObject obj=JSONObject.parseObject("{}");
                    File file = new File(filePath + templateName+ i + ".doc");
                    WordUtil.createWord(array.getJSONObject(i), templateName + ".ftl", filePath, templateName+i+ ".doc");
                    FileInputStream inputStream = new FileInputStream(file);
                    String url="http://" +disputecaseAccessoryService.uploadFile(inputStream, file.getName());
                    obj.put("url",url);
                    obj.put("templateName",templateName+i);
                    obj.put("hosName",array.getJSONObject(i).getString("param8"));
                    res.add(obj);
                }
                disputecaseAccessory.setProtocal(res.toString());
                disputecaseAccessoryRepository.save(disputecaseAccessory);
            }

            return ResultVOUtil.ReturnBack(res,200,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Autowired
    private DisputecaseRepository disputecaseRepository;
    @Autowired
    private DisputecaseApplyRepository disputecaseApplyRepository;
    @Autowired
    private NormalUserRepository normalUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactListRepository contactListRepository;

    /**
     * 自动填充模板空格内容的函数
     * @param map
     * @return
     */
    public ResultVO autoBlank(JSONObject map){
        String templateName=map.getString("templateName");
        String caseId=map.getString("caseId");
        JSONArray array=JSONArray.parseArray("[]");
        if(templateName.equals("南京市医调委案件受理通知书")){
            /** 初始化返回JSON */
            JSONObject res=JSONObject.parseObject("{}");
            res.put("param1","");res.put("param2","");res.put("param3","");res.put("param4","");res.put("param5","");res.put("param6","");res.put("param7","");res.put("param8","");
            /** 获取申请人姓名 */
            Disputecase dc=disputecaseRepository.getOne(caseId);
            String[] proposerIdList=dc.getProposerId().trim().split(",");
            String nameOfProposers="";
            for(String s:proposerIdList)
                nameOfProposers+=disputecaseApplyRepository.getOne(s).getName()+",";
            nameOfProposers=nameOfProposers.substring(0,nameOfProposers.length()-1);
            res.put("param1",nameOfProposers);
            /** 获取医院名称 */
            String hospitals="";
            for(String s:GetHospitalUtil.extract(dc.getMedicalProcess()))
                hospitals+=s+",";
            hospitals=hospitals.substring(0,hospitals.length()-1);
            res.put("param2",hospitals);
            /** 获取当前时间 */
            Date currentTIme=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String timeStr=sdf.format(currentTIme);
            String[] timeList=timeStr.split("-");
            res.put("param3",timeList[0]);res.put("param4",timeList[1]);res.put("param5",timeList[2]);
            res.put("param6",timeList[0]);res.put("param7",timeList[1]);res.put("param8",timeList[2]);
            res.put("templateName",templateName);
            res.put("caseId",caseId);
            array.add(res);
        }else if(templateName.equals("南京市医调委不予受理通知书")){
            /** 初始化返回JSON */
            JSONObject res=JSONObject.parseObject("{}");
            res.put("param1","");res.put("param2","");res.put("param3","");res.put("param4","");res.put("param5","");res.put("param6","");
            res.put("check1",false);res.put("check2",false);res.put("check3",false);res.put("check4",false);res.put("check5",false);res.put("check6",false);res.put("check7",false);
            /** 获取申请人姓名 */
            Disputecase dc=disputecaseRepository.getOne(caseId);
            String[] proposerIdList=dc.getProposerId().trim().split(",");
            String nameOfProposers="";
            for(String s:proposerIdList)
                nameOfProposers+=disputecaseApplyRepository.getOne(s).getName()+",";
            nameOfProposers=nameOfProposers.substring(0,nameOfProposers.length()-1);
            res.put("param1",nameOfProposers);
            /** 获取医院名称 */
            String hospitals="";
            for(String s:GetHospitalUtil.extract(dc.getMedicalProcess()))
                hospitals+=s+",";
            hospitals=hospitals.substring(0,hospitals.length()-1);
            res.put("param2",hospitals);
            /** 获取当前时间 */
            Date currentTIme=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String timeStr=sdf.format(currentTIme);
            String[] timeList=timeStr.split("-");
            res.put("param3",timeList[0]);res.put("param4",timeList[1]);res.put("param5",timeList[2]);
            res.put("templateName",templateName);
            res.put("caseId",caseId);
            array.add(res);
        }else if(templateName.equals("司法确认申请书")){
            Disputecase dc=disputecaseRepository.getOne(caseId);
            /** 先获取所有医院，确定返回JSONObject个数 */
            List<String> hosNameList=GetHospitalUtil.extract(dc.getMedicalProcess());
            int nums=hosNameList.size();
            List<Map<String,Object>> columns=new ArrayList<>();
            for(int i=0;i<nums;++i){
                /** 初始化返回JSON */
                JSONObject res=JSONObject.parseObject("{}");
                res.put("param9","");res.put("param10","");res.put("param11","");res.put("param12","");res.put("param13","");

                /** 获取申请人姓名和身份证号码 */
                String[] proposerIdList=dc.getProposerId().trim().split(",");
                if(columns.size()==0){
                    for(String proposerId:proposerIdList){
                        Map<String,Object> col=new HashMap<>();
                        col.put("param1","");
                        col.put("param2","");
                        col.put("param3","");
                        col.put("param4","");
                        col.put("param5","");
                        col.put("param6","");
                        col.put("param7","");
                        col.put("param8","");
                        DisputecaseApply da=disputecaseApplyRepository.getOne(proposerId);
                        col.put("param1",da.getName());
                        String idCard=normalUserRepository.getOne(userRepository.findByPhone(da.getPhone()).getSpecificId()).getIdCard();
                        if(!StrIsEmptyUtil.isEmpty(idCard))
                            col.put("param7",idCard);
                        columns.add(col);
                    }
                }
                res.put("columns",columns);
                res.put("param9",hosNameList.get(i));
                List<ContactList> cl=contactListRepository.findByName(hosNameList.get(i));
                if(!StrIsEmptyUtil.isEmpty(cl.get(0).getLocation()))
                    res.put("param10",cl.get(0).getLocation());
                res.put("templateName",templateName);
                res.put("caseId",caseId);
                res.put("hosName",hosNameList.get(i));
                array.add(res);
            }
        }else if(templateName.equals("南京市医患纠纷人民调解专家咨询申请书")){
            /** 初始化返回JSON */
            JSONObject res=JSONObject.parseObject("{}");
            res.put("param1","");res.put("param2","");res.put("param3","");res.put("param4","");res.put("param5","");res.put("param6","");
            res.put("param7","");res.put("param8","");res.put("param9","");res.put("param10","");res.put("param11","");res.put("param12","");res.put("param13","");res.put("param14","");;res.put("param15","");
            /** 获取申请人姓名 */
            Disputecase dc=disputecaseRepository.getOne(caseId);
            String[] proposerIdList=dc.getProposerId().trim().split(",");
            String nameOfProposers="";
            for(String s:proposerIdList)
                nameOfProposers+=disputecaseApplyRepository.getOne(s).getName()+",";
            nameOfProposers=nameOfProposers.substring(0,nameOfProposers.length()-1);
            res.put("param1",nameOfProposers);
            /** 获取医院名称 */
            String hospitals="";
            for(String s:GetHospitalUtil.extract(dc.getMedicalProcess()))
                hospitals+=s+",";
            hospitals=hospitals.substring(0,hospitals.length()-1);
            res.put("param2",hospitals);
            /** 获取纠纷争议事项概述 */
            res.put("param3",dc.getBriefCase());
            res.put("templateName",templateName);
            res.put("caseId",caseId);
            array.add(res);
        }else if(templateName.equals("人民调解协议书")){
            Disputecase dc=disputecaseRepository.getOne(caseId);
            /** 先获取所有医院，确定返回JSONObject个数 */
            List<String> hosNameList=GetHospitalUtil.extract(dc.getMedicalProcess());
            int nums=hosNameList.size();
            List<Map<String,Object>> columns=new ArrayList<>();
            for(int i=0;i<nums;++i){
                /** 初始化返回JSON */
                JSONObject res=JSONObject.parseObject("{}");
                res.put("param8","");res.put("param9","");res.put("param10","");res.put("param11","");res.put("param12","");res.put("param13","");
                res.put("param14","");res.put("param15","");res.put("param16","");res.put("param17","");res.put("param18","");res.put("param19","");
                res.put("param20","");res.put("param21","");res.put("param22","");
                /** 获取申请人姓名和身份证号码 */
                String[] proposerIdList=dc.getProposerId().trim().split(",");
                String param14="";
                if(columns.size()==0){
                    for(String proposerId:proposerIdList){
                        Map<String,Object> col=new HashMap<>();
                        col.put("param1","");
                        col.put("param2","");
                        col.put("param3","");
                        col.put("param4","");
                        col.put("param5","");
                        col.put("param6","");
                        col.put("param7","");
                        DisputecaseApply da=disputecaseApplyRepository.getOne(proposerId);
                        col.put("param1",da.getName());
                        param14+=da.getName()+",";
                        String idCard=normalUserRepository.getOne(userRepository.findByPhone(da.getPhone()).getSpecificId()).getIdCard();
                        if(!StrIsEmptyUtil.isEmpty(idCard))
                            col.put("param6",idCard);
                        columns.add(col);
                    }
                }
                res.put("columns",columns);
                res.put("param8",hosNameList.get(i));
                List<ContactList> cl=contactListRepository.findByName(hosNameList.get(i));
                if(cl.size()!=0 && !StrIsEmptyUtil.isEmpty(cl.get(0).getLocation()))
                    res.put("param9",cl.get(0).getLocation());
                res.put("param14",param14.substring(0,param14.length()-1));
                res.put("param20",param14.substring(0,param14.length()-1));
                res.put("templateName",templateName);
                res.put("caseId",caseId);
                res.put("hosName",hosNameList.get(i));
                array.add(res);
            }
        }
        return ResultVOUtil.ReturnBack(array,200,"success");
    }

}




