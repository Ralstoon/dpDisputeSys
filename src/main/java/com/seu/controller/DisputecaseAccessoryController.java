package com.seu.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seu.ViewObject.ResultVO;
import com.seu.service.DisputecaseAccessoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping(value = "/File")
@CrossOrigin
public class DisputecaseAccessoryController {
    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;

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
}
