package com.seu.repository;

import com.seu.domian.ContactList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContactListRepositoryTest {

    @Autowired
    private ContactListRepository contactListRepository;

    @Test
    public void findAllByCityAndZoneAndName() {
        List<ContactList> obj=contactListRepository.findAllByCityAndZoneAndName("南京市","鼓楼区","南京中医院");
        Assert.assertNotNull(obj);
    }
}