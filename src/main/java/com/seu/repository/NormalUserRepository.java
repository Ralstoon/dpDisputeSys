package com.seu.repository;

import com.seu.domian.NormalUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalUserRepository  {
    NormalUser selectLogin(@Param("phone")String phone, @Param("password") String md5Password);

    int checkUser(String phone);
    /** 注册操作，成功返回1 */
    int register(@Param("user_id") String user_id,@Param("phone")String phone, @Param("password") String password);

    NormalUser findNormalUserByUserId(@Param("user_id") String user_id);
}
