package com.seu.repository;

import com.seu.domian.Register;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegisterRepository extends JpaRepository<Register,String> {
    @Query(value = "select * from register where register.province like %?1% and register.city like %?2% and register.mediation_center like %?3% and register.role = ?4 or register.role = ?5  ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from register where register.province like %?1% and register.city like %?2% and register.mediation_center like %?3% and register.role = ?4 or register.role = ?5",
            nativeQuery = true)
    Page<Register> findAllByRole(String province, String city, String mediation_center, String role1, String role2, Pageable pageable);

    Register findByPhone(String phone);
}
