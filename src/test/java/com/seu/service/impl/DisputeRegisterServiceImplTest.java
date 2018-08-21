package com.seu.service.impl;

import com.seu.ViewObject.ResultVO;
import com.seu.domian.ConstantData;
import com.seu.service.DisputeRegisterService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DisputeRegisterServiceImplTest {

    @Autowired
    DisputeRegisterServiceImpl disputeRegisterService;

    @Test
    public void getDieaseList() {
        ResultVO resultVO =disputeRegisterService.getDieaseList();
        System.out.println(resultVO.toString());
        Assert.assertNotNull(resultVO);
    }
}