package com.seu.repository;

import com.seu.domian.NormalUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface NormalUserRepository extends JpaRepository<NormalUser,String> {
    NormalUser findByFatherId(String fatherId);
    List<NormalUser> findByIdCard(String idCard);

//    NormalUser selectLogin(@Param("phone")String phone, @Param("password") String md5Password);

//    int checkUser(String phone);
    /** 注册操作，成功返回1 */
//    int register(@Param("user_id") String user_id,@Param("phone")String phone, @Param("password") String password);

//    NormalUser findNormalUserByUserId(@Param("user_id") String user_id);
}
