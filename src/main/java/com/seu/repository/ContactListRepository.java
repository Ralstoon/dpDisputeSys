package com.seu.repository;

import com.seu.domian.ContactList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

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


    @Query(value = "select * from contact_list where contact_list.province like %?1% and contact_list.city like %?2% and contact_list.zone like %?3% and contact_list.name like %?4% ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from contact_list where contact_list.province like %?1% and contact_list.city like %?2% and contact_list.zone like %?3% and contact_list.name like %?4%",
            nativeQuery = true)
    Page<ContactList> findByLoc(String province, String city, String mediateCenter,String hosipital, Pageable pageable);
}
