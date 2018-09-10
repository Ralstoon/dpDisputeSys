package com.seu.controller;

import com.seu.service.KeyWordsSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName SearchController
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/9 0009 下午 8:22
 * @Version 1.0
 **/


@Slf4j
@RestController
@RequestMapping(("/search"))
public class SearchController {
    @Autowired
    private KeyWordsSearchService keyWordsSearchService;


    @GetMapping(value = "/oneWord")
    public List<String> searchOneWord(@RequestParam("keyword") String keyword) throws Exception{
        log.info("=====start=====");
        List<String> results=keyWordsSearchService.getByOneWord(keyword);
        log.info("=====finish=====");
        return results;
    }

    @GetMapping(value = "/words")
    public List<String> searchByWords(@RequestParam("keywords") String keywords) throws Exception{
        log.info("=====start=====");
        List<String> results=keyWordsSearchService.getByWords(keywords);
        log.info("=====finish=====");
        return results;


    }

}
