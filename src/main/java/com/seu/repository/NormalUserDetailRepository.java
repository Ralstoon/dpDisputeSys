package com.seu.repository;

import com.seu.domian.NormalUserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalUserDetailRepository extends JpaRepository<NormalUserDetail,String> {
    NormalUserDetail findByUserId(String userId);

}
