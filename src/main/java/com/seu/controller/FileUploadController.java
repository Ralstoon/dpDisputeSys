package com.seu.controller;

import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
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
    public ResultVO upload(@RequestBody Map<String,Object> map) throws IOException {
        String templateName=map.get("templateName").toString();
        String url=wordTemplateUtil.createWord(map,templateName);
        JSONObject res=JSONObject.parseObject("{}");
        res.put("name",templateName);
        res.put("url",url);
        return ResultVOUtil.ReturnBack(res,200,"成功");

    }

    @PostMapping("/autoBlank")
    public ResultVO autoBlank(@RequestBody JSONObject map) throws IOException {
        return wordTemplateUtil.autoBlank(map);
    }
}
