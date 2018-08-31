package com.seu.repository;

import com.seu.domian.Disputecase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName DisputecaseRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/20 21:29
 * @Version 1.0
 **/
public interface DisputecaseRepository extends JpaRepository<Disputecase,String> {
    List<Disputecase> findByMediatorId(String id);
}
