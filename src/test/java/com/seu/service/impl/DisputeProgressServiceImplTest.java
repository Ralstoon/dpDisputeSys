package com.seu.service.impl;

import com.seu.DpdisputesysApplication;
import com.seu.domian.DisputecaseProcess;
import com.seu.form.VOForm.MediationHallDataForm;
import com.seu.service.DisputeProgressService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DpdisputesysApplication.class)
public class DisputeProgressServiceImplTest {


    @Autowired
    DisputeProgressService disputeProgressService;
    @Test
    public void updateApplyStatus() {
        String disputeId="123";
        String apply_status="321";
        disputeProgressService.updateApplyStatus(disputeId,apply_status);
    }

    @Test
    public void getMediationHallData(){
        try {
            Object list=disputeProgressService.getMediationHallData("333").getData();
            Assert.assertNotNull(list);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}