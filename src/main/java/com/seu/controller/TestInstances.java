package com.seu.testt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestInstances
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/19 0019 下午 3:14
 * @Version 1.0
 **/

@RestController
@RequestMapping("/test")
public class TestInstances {

    @PostMapping(value = "/run1")
    public void run1(@RequestParam("caseId") String caseId){
        System.out.println(caseId);
    }

    @PostMapping(value = "/run2")
    public void run2(@RequestParam("caseId1") String caseId1,@RequestParam("caseId2") String caseId2){
        System.out.println(caseId1);
        System.out.println(caseId2);
    }
    @PostMapping(value = "/run3")
    public void run3(@RequestParam("caseId") Integer caseId){
        System.out.println(caseId);
    }
}
