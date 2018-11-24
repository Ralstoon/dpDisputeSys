package com.seu.controller;


import com.alibaba.fastjson.JSONObject;
import com.seu.repository.ConstantDataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DisputeRegisterControllerTest {

    @Autowired
    private ConstantDataRepository constantDataRepository;

    @Test
    public void getMediationCenterList() {
        String province="江苏省";
        String city="连云港市";
        JSONObject obj=JSONObject.parseObject(constantDataRepository.findByName("center_list").getData());
        String li=obj.getJSONObject(province).getJSONArray(city).toString();
        System.out.println(li);
    }
}
