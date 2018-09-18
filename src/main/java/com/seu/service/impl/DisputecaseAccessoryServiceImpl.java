package com.seu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.ConstantQiniu;
import com.seu.domian.Disputecase;
import com.seu.domian.DisputecaseAccessory;
import com.seu.domian.NormalUserUpload;
import com.seu.enums.DisputecaseAccessoryEnum;
import com.seu.repository.DiseaseListRepository;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.repository.DisputecaseActivitiRepository;
import com.seu.repository.DisputecaseRepository;
import com.seu.service.DisputeProgressService;
import com.seu.service.DisputecaseAccessoryService;
import com.seu.utils.KeyUtil;
import com.seu.utils.VerifyProcessUtil;
import net.sf.json.JSONArray;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.task.Task;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class DisputecaseAccessoryServiceImpl implements DisputecaseAccessoryService {

    @Autowired
    private ConstantQiniu constantQiniu;

    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;

    @Autowired
    private DisputeProgressService disputeProgressService;

    @Autowired
    private VerifyProcessUtil verifyProcessUtil;

    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;

    @Autowired
    private DisputecaseRepository disputecaseRepository;
    @Autowired
    private DisputecaseActivitiRepository disputecaseActivitiRepository;
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public String uploadFile(FileInputStream file, String fileName){
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        // 其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        // 生成上传凭证，然后准备上传

        try {
            Auth auth = Auth.create(constantQiniu.getAccessKey(), constantQiniu.getSecretKey());
            String upToken = auth.uploadToken(constantQiniu.getBucket());
            try {
                Response response = uploadManager.put(file, fileName, upToken, null, null);
                // 解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

                String returnPath = constantQiniu.getPath() + "/" + putRet.key;
                return returnPath;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public ResultVO addNormalUserUpload(String personId, String disputeID, String disputeStatus, String fileType, String fileDescription, String url) {
        //1.检查是否已经有该案件的附件存在
        //2.否则新增记录，是则做updata
        DisputecaseAccessory dA = disputecaseAccessoryRepository.findByDisputecaseId(disputeID);
        if(dA == null){
            String id = KeyUtil.genUniqueKey();
            NormalUserUpload normalUserUpload = new NormalUserUpload(personId, disputeStatus, fileType, fileDescription, url);
            List<NormalUserUpload> normalUserUploadList =  new ArrayList<NormalUserUpload>();
            normalUserUploadList.add(normalUserUpload);
            String normaluserUpload = JSONArray.fromObject(normalUserUploadList).toString();
            DisputecaseAccessory disputecaseAccessory = new DisputecaseAccessory();
            disputecaseAccessory.setId(id);
            disputecaseAccessory.setDisputecaseId(disputeID);
            disputecaseAccessory.setNormaluserUpload(normaluserUpload);
            disputecaseAccessoryRepository.save(disputecaseAccessory);
            return ResultVOUtil.ReturnBack(normalUserUpload, DisputecaseAccessoryEnum.ADDNORMLUSERUPLOAD_SUCCESS.getCode(), DisputecaseAccessoryEnum.ADDNORMLUSERUPLOAD_SUCCESS.getMsg());
        }
        else {
            DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputeID);
            String normalusrUpload = disputecaseAccessory.getNormaluserUpload();
            List<NormalUserUpload> normalUserUploadList = JSONArray.fromObject(normalusrUpload);
            NormalUserUpload normalUserUpload = new NormalUserUpload(personId, disputeStatus, fileType, fileDescription, url);
            normalUserUploadList.add(normalUserUpload);
            //DisputecaseAccessory disputecaseAccessory = new DisputecaseAccessory(id, disputeID, null, normaluserUpload);
            disputecaseAccessory.setNormaluserUpload(JSONArray.fromObject(normalUserUploadList).toString());
            disputecaseAccessoryRepository.save(disputecaseAccessory);
            return ResultVOUtil.ReturnBack(normalUserUpload, DisputecaseAccessoryEnum.ADDNORMLUSERUPLOAD_SUCCESS.getCode(), DisputecaseAccessoryEnum.ADDNORMLUSERUPLOAD_SUCCESS.getMsg());

        }

    }

    @Override
    public ResultVO normalFileList(String disputeId) {
        DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputeId);
        List<NormalUserUpload> normalUserUploadList = JSONArray.fromObject(disputecaseAccessory.getNormaluserUpload());
        return ResultVOUtil.ReturnBack(normalUserUploadList, DisputecaseAccessoryEnum.GETNORMALUSERUPLOADLIST_SUCCESS.getCode(), DisputecaseAccessoryEnum.GETNORMALUSERUPLOADLIST_SUCCESS.getMsg());
    }

    @Override
    @Transactional
    public ResultVO addInquireHospital(MultipartFile[] multipartFiles,
                                       String text,
                                       String disputeID,
                                       String isFinished) throws IOException {
        List<Task> tasks=verifyProcessUtil.verifyTask(disputeID,"问询医院");
        Task currentTask=null;
        for(Task one :tasks)
            if(one.getName().equals("问询医院")){
                currentTask=one;
                break;
            }

        Disputecase disputecase = disputecaseRepository.findOne(disputeID);

        String title =disputecase.getCaseName();

        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(disputeID);
        com.alibaba.fastjson.JSONArray inquireHospital;
        if(disputecaseAccessory.getInquireHospital() == null){
            inquireHospital= com.alibaba.fastjson.JSONArray.parseArray("[]");
        }else {
            inquireHospital= com.alibaba.fastjson.JSONArray.parseArray(disputecaseAccessory.getInquireHospital());
        }

        com.alibaba.fastjson.JSONObject save= com.alibaba.fastjson.JSONObject.parseObject("{}");
        com.alibaba.fastjson.JSONArray files= com.alibaba.fastjson.JSONArray.parseArray("[]");

        //Arrays.stream(multipartFiles).filter(each -> each.getOriginalFilename() == "");

            for (MultipartFile multipartFile: multipartFiles){
                com.alibaba.fastjson.JSONObject obj= JSONObject.parseObject("{}");
                try {
                    FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
                    String url = disputecaseAccessoryService.uploadFile(inputStream, title+ multipartFile.getOriginalFilename());
                    obj.put("url","http://"+url);
                    obj.put("name",multipartFile.getOriginalFilename());
                    files.add(obj);
                }catch (Exception e){
                    obj.put("url","");
                    obj.put("name","");
                    files.add(obj);
                }

            }



        save.put("file", files);
        if(text != null){
            save.put("text", text);
        }

        save.put("isFinisihed", isFinished);

        inquireHospital.add(save);

        disputecaseAccessory.setInquireHospital(inquireHospital.toString());

        disputecaseAccessoryRepository.save(disputecaseAccessory);

        /** 完成流程 */
        String pid=disputecaseActivitiRepository.getOne(disputeID).getProcessId();
        Integer paramInquireHospital=0;
        if(isFinished.trim()=="1" || isFinished.trim().equals("1"))
            paramInquireHospital=1;
        runtimeService.setVariable(pid,"paramInquireHospital",paramInquireHospital);
        disputeProgressService.completeCurrentTask(currentTask.getId());

        return ResultVOUtil.ReturnBack(112,"问询医院成功");
    }

    @Override
    public ResultVO addNotificationAffirm(MultipartFile multipartFile, String disputeId) throws IOException {


        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(disputeId);

        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String url = disputecaseAccessoryService.uploadFile(inputStream, disputeId+"/"+ multipartFile.getOriginalFilename());


        disputecaseAccessory.setNotificationAffirm("http://"+url);

        disputecaseAccessoryRepository.save(disputecaseAccessory);
        return ResultVOUtil.ReturnBack(112,"上传告知书确认成功");
    }

    @Override
    public ResultVO addProxyCertification(MultipartFile multipartFile, String disputeId) throws IOException {
        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(disputeId);

        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String url = disputecaseAccessoryService.uploadFile(inputStream, disputeId+"/"+ multipartFile.getOriginalFilename());


        disputecaseAccessory.setProxyCertification("http://"+url);

        disputecaseAccessoryRepository.save(disputecaseAccessory);
        return ResultVOUtil.ReturnBack(112,"上传代理人委托书成功");
    }

    @Override
    public ResultVO addExportApply(MultipartFile application, MultipartFile[] applicationDetail, String disputeId) throws IOException {
        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(disputeId);
        FileInputStream inputStream = (FileInputStream) application.getInputStream();
        String applicationUrl = disputecaseAccessoryService.uploadFile(inputStream, disputeId+"/"+ application.getOriginalFilename());
        com.alibaba.fastjson.JSONObject save= com.alibaba.fastjson.JSONObject.parseObject("{}");
        com.alibaba.fastjson.JSONObject applicationJson= com.alibaba.fastjson.JSONObject.parseObject("{}");
        applicationJson.put("url", applicationUrl);
        applicationJson.put("name", application.getOriginalFilename());
        save.put("application", application);
        com.alibaba.fastjson.JSONArray files = com.alibaba.fastjson.JSONArray.parseArray("{}");
        for (MultipartFile multipartFile: applicationDetail){
            com.alibaba.fastjson.JSONObject obj= JSONObject.parseObject("{}");
            FileInputStream inputStream2 = (FileInputStream) multipartFile.getInputStream();
            String url = disputecaseAccessoryService.uploadFile(inputStream2, disputeId+"/"+ multipartFile.getOriginalFilename());
            obj.put("url","http://"+url);
            obj.put("name",multipartFile.getOriginalFilename());
            files.add(obj);
        }



        return ResultVOUtil.ReturnBack(112,"上传专家申请成功");
    }

    @Override
    public String addAcceptanceNotification(MultipartFile multipartFile, String disputeId) throws IOException {
        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(disputeId);

        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String url = disputecaseAccessoryService.uploadFile(inputStream, disputeId+"/"+ multipartFile.getOriginalFilename());


        disputecaseAccessory.setAcceptanceNotice("http://"+url);

        disputecaseAccessoryRepository.save(disputecaseAccessory);

        return  url;
    }
}
