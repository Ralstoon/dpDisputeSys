package com.seu.repository;

import com.seu.common.Const;
import com.seu.domian.ConstantData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName DiseaseListRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/20 20:16
 * @Version 1.0
 **/
public interface DiseaseListRepository extends JpaRepository<ConstantData,Integer> {
    ConstantData findByName(String name);
}
