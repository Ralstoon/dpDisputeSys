package com.seu.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImplTest {
    @Autowired
    UserServiceImpl userServiceImpl;
    @Test
    public void login() {
        Object object=userServiceImpl.login("15896923019","111111");
        System.out.println(object.toString());
        Assert.assertNotNull(object);
    }
}
