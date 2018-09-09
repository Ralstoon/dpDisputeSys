package com.seu.repository;

import com.seu.domian.DisputecaseApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName DisputecaseApplyRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/31 10:36
 * @Version 1.0
 **/
public interface DisputecaseApplyRepository extends JpaRepository<DisputecaseApply,String> {
    List<DisputecaseApply> findAllByIdCard(String idCard);
    List<DisputecaseApply> findAllByPhone(String phone);
    List<DisputecaseApply> findAllById(String id);

}
