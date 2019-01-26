package com.seu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.VerifyCodeGlobal;
import com.seu.domian.DisputecaseAccessory;
import com.seu.form.VOForm.NormalUserUploadListForm;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.service.DisputecaseAccessoryService;
import com.seu.service.UserService;
import com.seu.utils.VerifyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
//@RequestMapping(value = "/mediator")
@CrossOrigin
public class DisputecaseAccessoryController {
    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;
    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;

    @Autowired
    private UserService userService;

    //注册验证码请求
    @RequestMapping(value = "/register/vertifyCode",method = RequestMethod.POST)
    public ResultVO getZoneList(@RequestBody Map<String, String > map) {
        String phone = map.get("phone");
        if(VerifyCodeGlobal.registerCode==null){
            VerifyCodeGlobal.registerCode = new HashMap<>();
        }
        if(VerifyCodeGlobal.registerCode.get(phone)!=null){
            return ResultVOUtil.ReturnBack(124,"请勿重复获取验证码");
        }

        VerifyCode verifyCode = new VerifyCode(userService.getCode(phone),new Date());
        VerifyCodeGlobal.registerCode.put(phone,verifyCode);


        return ResultVOUtil.ReturnBack(123,"获取验证码成功");
    }

    //上传
    @PostMapping(value = "/uploadNormalFile")
    public ResultVO uploadNomralUserFile(@RequestParam(value = "file", required=false) MultipartFile multipartFile,
                               @RequestParam("personId") String personId,
                               @RequestParam("disputeId") String disputeID,
                               @RequestParam("disputeStatus") String disputeStatus,
                               @RequestParam("fileType") String fileType,
                               @RequestParam("fileDescription") String fileDescription) throws IOException {
        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String url = disputecaseAccessoryService.uploadFile(inputStream, multipartFile.getOriginalFilename());

        return disputecaseAccessoryService.addNormalUserUpload(personId,disputeID,disputeStatus,fileType,fileDescription,url);

    }

    //用户附件列表展示
    @PostMapping(value = "/normalFileList")
    public ResultVO normalFileList(@RequestParam("disputeId") String disputeID){

        return disputecaseAccessoryService.normalFileList(disputeID);
    }

    @PostMapping(value = "DisputeWeb/Mediation/MediationUpload")
    public ResultVO uploadNomralUserFile(@RequestParam(value = "files", required=false) MultipartFile[] multipartFiles,
                                         @RequestParam("caseId") String disputeID) throws IOException {
        List<NormalUserUploadListForm> normalUserUploadListFormList = new ArrayList<>();
        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(disputeID);
        JSONArray save=JSONArray.parseArray(disputecaseAccessory.getNormaluserUpload());
        if(save==null || save.isEmpty())
            save=JSONArray.parseArray("[]");
        for (MultipartFile multipartFile: multipartFiles){
            JSONObject obj=JSONObject.parseObject("{}");
            FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
            String url = disputecaseAccessoryService.uploadFile(inputStream, multipartFile.getOriginalFilename());
            obj.put("url",url);
            obj.put("caseId",disputeID);
            obj.put("name",multipartFile.getOriginalFilename());
            save.add(obj);
            NormalUserUploadListForm normalUserUploadListForm = new NormalUserUploadListForm(url, disputeID);
            normalUserUploadListFormList.add(normalUserUploadListForm);
        }
        disputecaseAccessory.setNormaluserUpload(save.toString());
        disputecaseAccessoryRepository.save(disputecaseAccessory);

        return ResultVOUtil.ReturnBack(normalUserUploadListFormList,122, "添加文件列表成功");

    }
}
