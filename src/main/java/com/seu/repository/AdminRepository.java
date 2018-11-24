package com.seu.repository;

import com.seu.domian.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName AdminRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/20 15:46
 * @Version 1.0
 **/
public interface AdminRepository extends JpaRepository<Admin,String> {
    Admin findByFatherId(String ID);
}
