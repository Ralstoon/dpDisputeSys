package com.seu.repository;

import com.seu.domian.DisputecaseAccessory;
import com.seu.form.ExpertAppointForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @ClassName DisputecaseAccessoryRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/1 23:50
 * @Version 1.0
 **/
public interface DisputecaseAccessoryRepository extends JpaRepository<DisputecaseAccessory,String> {
    DisputecaseAccessory findByDisputecaseId(String id);

    @Query(value = "SELECT a.id,a.case_name,c.appoint_expert,b.status from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where b.is_suspended=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT a.id,a.case_name,c.appoint_expert,b.status from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where b.is_suspended=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY a.id DESC",
            nativeQuery = true)
    Page<Object[]> findBySuspended(Integer isSuspended,String province,String city,String mediateCenter, Pageable pageable);

    @Query(value = "SELECT a.id,a.case_name,c.appoint_expert,b.status from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where b.param_professor=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT a.id,a.case_name,c.appoint_expert,b.status from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where b.param_professor=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY a.id DESC",
            nativeQuery = true)
    Page<Object[]> findByParamProfessor(String filterStatus,String province,String city,String mediateCenter, Pageable pageable);

    @Query(value = "SELECT a.id,a.case_name,c.appoint_expert,b.param_professor from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where (b.param_professor=1 or b.param_professor=2 or b.is_suspended=2) AND a.province like ?1 AND a.city like ?2 AND a.mediation_center like ?3 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT a.id,a.case_name,c.appoint_expert,b.param_professor from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where (b.param_professor=1 or b.param_professor=2 or b.is_suspended=2) AND a.province like ?1 AND a.city like ?2 AND a.mediation_center like ?3 ORDER BY a.id DESC",
            nativeQuery = true)
    Page<Object[]> findBySuspendedAndParamProfessor(String province,String city,String mediateCenter,Pageable pageable);
//    @Query(value = "SELECT a.id,a.case_name,c.appoint_expert,b.status from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where b.is_suspended=?1  ORDER BY ?#{#pageable},a.id DESC",
//            countQuery = "SELECT a.id,a.case_name,c.appoint_expert,b.status from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where b.is_suspended=?1  ORDER BY a.id DESC",
//            nativeQuery = true)
//    Page<Object[]> findBySuspended(Integer isSuspended,Pageable pageable);
//
//    @Query(value = "SELECT a.id,a.case_name,c.appoint_expert,b.status from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where b.param_professor=?1  ORDER BY ?#{#pageable},a.id DESC",
//            countQuery = "SELECT a.id,a.case_name,c.appoint_expert,b.status from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where b.param_professor=?1  ORDER BY a.id DESC",
//            nativeQuery = true)
//    Page<Object[]> findByParamProfessor(String filterStatus, Pageable pageable);
//
//    @Query(value = "SELECT a.id,a.case_name,c.appoint_expert,b.param_professor from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where (b.param_professor=1 or b.param_professor=2 or b.is_suspended=2) ORDER BY ?#{#pageable},a.id DESC",
//            countQuery = "SELECT a.id,a.case_name,c.appoint_expert,b.param_professor from disputecase a inner join disputecase_process b on a.id=b.disputecase_id inner join disputecase_accessory c on b.disputecase_id=c.disputecase_id  where (b.param_professor=1 or b.param_professor=2 or b.is_suspended=2)  ORDER BY a.id DESC",
//            nativeQuery = true)
//    Page<Object[]> findBySuspendedAndParamProfessor(Pageable pageable);
}
