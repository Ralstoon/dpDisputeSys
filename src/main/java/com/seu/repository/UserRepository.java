package com.seu.repository;

import com.seu.domian.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName UserRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/3 21:18
 * @Version 1.0
 **/
public interface UserRepository extends JpaRepository<User,String> {
    User findByPhoneAndPassword(String phone, String password);
    User findByPhone(String phone);
}
