package com.seu.repository;

import com.seu.DpdisputesysApplication;
import com.seu.domian.Disputecase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DpdisputesysApplication.class)
public class DisputecaseRepositoryTest {
    @Autowired
    private DisputecaseRepository disputecaseRepository;

    @Test
    public void test(){
        String caseId="1539585610439830193";
        Disputecase disputecase=disputecaseRepository.findOne(caseId);
        Assert.assertNotNull(disputecase);
    }
}