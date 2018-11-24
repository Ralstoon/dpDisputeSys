package com.seu.controller;

import com.seu.utils.WordTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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

    @PostMapping("/wordUpload")
    public ResponseEntity<FileSystemResource> upload(@RequestBody Map<String,Object> map){
        String templateName=map.get("templateName").toString();
        File file=wordTemplateUtil.createWord(map,templateName);


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
