package com.seu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.DisputecaseAccessory;
import com.seu.form.VOForm.NormalUserUploadListForm;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.service.DisputecaseAccessoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping(value = "/mediator")
@CrossOrigin
public class DisputecaseAccessoryController {
    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;
    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;

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
        JSONArray save=JSONArray.parseArray("[]");
        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(disputeID);
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
