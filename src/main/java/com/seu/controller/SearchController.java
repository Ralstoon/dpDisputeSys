package com.seu.controller;

import com.alibaba.fastjson.JSONObject;
import com.seu.service.KeyWordsSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SearchController
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/9 0009 下午 8:22
 * @Version 1.0
 **/


@Slf4j
@RestController
@RequestMapping(("/DisputeWeb/search"))
public class SearchController {
    @Autowired
    private KeyWordsSearchService keyWordsSearchService;


    @PostMapping(value = "/oneWord")
    public List<String> searchOneWord(@RequestBody JSONObject map) throws Exception{
        String keyword=map.getString("keyword");
        String type=map.getString("type");
        log.info("=====start=====");
        List<String> results=keyWordsSearchService.getByOneWord(keyword,type);
        log.info("=====finish=====");
        return results;
    }

    @PostMapping(value = "/words")
    public List<String> searchByWords(@RequestBody JSONObject map) throws Exception{

        String keywords=map.getString("keywords");
        String type=map.getString("type");
        log.info("=====start=====");
        List<String> results=keyWordsSearchService.getByWords(keywords,type);
        log.info("=====finish=====");
        return results;
    }

    @PostMapping(value = "/similarCases")
    public Map<String,Object> getSimilarCases(@RequestBody JSONObject map){
        String caseId=map.getString("caseId");
        Map<String,Object> results=keyWordsSearchService.getSimilarCases(caseId);
        return results;
    }

    @PostMapping(value = "/caseDetails")
    public Object getCaseDetails(@RequestBody JSONObject map){
        String caseName=map.getString("caseName");
        String type=map.getString("type");
        return keyWordsSearchService.getCaseDetails(caseName,type);
    }

    @PostMapping(value = "/postKeyWordList")
    public Map<String,Object> postKeyWordList(@RequestBody JSONObject map){
        String keyWordList = map.getString("keyWordList");//todo:记录
        Map<String,Object> results=keyWordsSearchService.getSimilarCases(keyWordList);
        return results;
    }

}
