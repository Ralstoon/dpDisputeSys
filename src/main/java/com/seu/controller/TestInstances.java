package com.seu.controller;

import com.alibaba.fastjson.JSONObject;
import com.seu.domian.Admin;
import com.seu.domian.One;
import com.seu.repository.DisputecaseRepository;
import com.seu.utils.GetWorkingTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;

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
    @Autowired
    private DisputecaseRepository disputecaseRepository;
    @PostMapping(value = "/run1")
    public Object run1(@RequestBody JSONObject map){
        Integer page=map.getInteger("page")-1;
        Integer size=map.getInteger("size");
        PageRequest pageRequest=new PageRequest(page,size);
//        return disputecaseRepository.findAll_HallData(pageRequest);
        return null;
//        System.out.println(caseId);
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

    @PostMapping(value = "/run4")
    public void run4(@RequestBody @Valid One one,
                     BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors())
                System.out.println(bindingResult.getFieldError().getDefaultMessage());
            System.out.println(one.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Autowired
    private GetWorkingTimeUtil getWorkingTimeUtil;

    @PostMapping(value = "/endTime")
    public void endTime() throws Exception {
        System.out.println(getWorkingTimeUtil.calWorkingTime(new Date(),30));
        Date endTime=new Date();
        int limitTime = 30;
        Calendar c=Calendar.getInstance();
        while (limitTime!=0){
            Integer result=getWorkingTimeUtil.getResult(endTime);
            if(result==0){
                limitTime=limitTime-1;
            }
            c.setTime(endTime);
            c.add(Calendar.DAY_OF_MONTH,1);
            endTime=c.getTime();
        }

    }
}
