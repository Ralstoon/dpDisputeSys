package com.seu.repository;

import com.seu.domian.DisputecaseProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @ClassName DisputecaseProcessRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/30 18:37
 * @Version 1.0
 **/
public interface DisputecaseProcessRepository extends JpaRepository<DisputecaseProcess,String> {
    DisputecaseProcess findByDisputecaseId(String disputecase_id);

    List<DisputecaseProcess> findAllByIsSuspended(int is_suspended);
}
