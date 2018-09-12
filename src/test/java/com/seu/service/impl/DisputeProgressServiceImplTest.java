package com.seu.service.impl;

import com.seu.DpdisputesysApplication;
import com.seu.ViewObject.ResultVO;
import com.seu.domian.DisputecaseProcess;
import com.seu.domian.Mediator;
import com.seu.form.VOForm.MediationHallDataForm;
import com.seu.service.DisputeProgressService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    }

    @Test
    public void getManagerCaseList(){
//        Object list=disputeProgressService.getManagerCaseList("123");
//        Assert.assertNotNull(list);
    }

    @Test
    public void getAdditionalAllocation(){
        PageRequest pageRequest=new PageRequest(1,2);
        ResultVO result=disputeProgressService.getAdditionalAllocation("1536066159098480333",pageRequest);
        System.out.println(result.toString());
        Assert.assertNotNull(result.getData());
    }
}