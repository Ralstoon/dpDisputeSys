package com.seu.repository;

import com.alibaba.fastjson.JSONObject;
import com.seu.form.ExpertAppointForm;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DisputecaseAccessoryRepositoryTest {

    @Autowired
    private  DisputecaseAccessoryRepository disputecaseAccessoryRepository;

    @Test
    public void findBySuspended() {

    }
}