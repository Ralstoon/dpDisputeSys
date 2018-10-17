package com.seu.repository;

import com.seu.domian.ContactList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName ContactListRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/9 0009 下午 5:20
 * @Version 1.0
 **/
public interface ContactListRepository extends JpaRepository<ContactList,Integer> {

    List<ContactList> findAllByCityAndZoneAndName(String city,String zone,String hospital);

    List<ContactList> findByName(String hospital);
}
