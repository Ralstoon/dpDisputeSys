package com.seu.service.impl;

import com.seu.DpdisputesysApplication;
import com.seu.service.DisputeProgressService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DpdisputesysApplication.class)
public class DisputeProgressServiceImplTest {


    @Autowired
    private DisputeProgressService disputeProgressService;
    @Test
    public void saveMediateVideo() {
        disputeProgressService.saveMediateVideo("1539604432004672067","www.baidu.com");
    }
}