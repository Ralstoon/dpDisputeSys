package com.seu.repository;

import com.seu.DpdisputesysApplication;
import com.seu.domian.Disputecase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DpdisputesysApplication.class)
public class DisputecaseRepositoryTest {

    @Autowired
    private DisputecaseRepository disputecaseRepository;
    @Test
    public void findWithProcessStatus() {
        PageRequest pageRequest=new PageRequest(1,1);
        Assert.assertNotNull(disputecaseRepository.findAll_HallData(pageRequest));
    }
}