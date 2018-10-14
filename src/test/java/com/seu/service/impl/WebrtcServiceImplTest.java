package com.seu.service.impl;

import com.seu.service.WebrtcService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class WebrtcServiceImplTest {

    @Autowired
    private WebrtcService webrtcService;
    @Test
    public void mixStream() {
        webrtcService.mixStream(5);
    }
}