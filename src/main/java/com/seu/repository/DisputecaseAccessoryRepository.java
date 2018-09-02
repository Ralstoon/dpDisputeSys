package com.seu.repository;

import com.seu.domian.Disputecase;
import com.seu.domian.DisputecaseAccessory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName DisputecaseAccessoryRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/1 23:50
 * @Version 1.0
 **/
public interface DisputecaseAccessoryRepository extends JpaRepository<DisputecaseAccessory,String> {
    DisputecaseAccessory findByDisputecaseId(String id);
}
