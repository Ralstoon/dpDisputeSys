package com.seu.repository;

import com.seu.domian.ConstantData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstantDataRepository extends JpaRepository<ConstantData,Integer> {
    ConstantData findByName(String name);
}
