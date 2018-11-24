package com.seu.controller;

import com.alibaba.fastjson.JSONObject;
import com.seu.domian.DisputecaseAccessory;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.service.DisputecaseAccessoryService;
import com.seu.utils.WordTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName FileUploadController
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/11/23 0023 下午 7:44
 * @Version 1.0
 **/
@RestController
@RequestMapping(value = "/DisputeWeb")
@CrossOrigin
@Slf4j
public class FileUploadController {
    @Autowired
    private WordTemplateUtil wordTemplateUtil;

    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;

    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;

    @PostMapping("/wordUpload")
    public ResponseEntity<FileSystemResource> upload(@RequestBody Map<String,Object> map) throws IOException {
        String templateName=map.get("templateName").toString();
        File file=wordTemplateUtil.createWord(map,templateName);


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

        /** 返回文件结果 */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + templateName + ".doc");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(file));

    }
}
