package com.seu.repository;

import com.seu.domian.Experts;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * @ClassName ExpertsRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/2 21:08
 * @Version 1.0
 **/
public interface ExpertsRepository extends JpaRepository<Experts,Integer> {
}
