package com.seu.repository;

import com.seu.domian.DisputeInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName DisputeInfoRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/20 21:29
 * @Version 1.0
 **/
public interface DisputeInfoRepository extends JpaRepository<DisputeInfo,String> {
    Page<DisputeInfo> findByUserId(String userId, Pageable pageable);

    DisputeInfo findByDisputeId(String disputeId);
}
