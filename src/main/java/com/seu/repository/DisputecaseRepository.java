package com.seu.repository;

import com.seu.domian.Disputecase;
import com.seu.utils.StrIsEmptyUtil;
import org.omg.CORBA.TRANSACTION_MODE;
import org.python.antlr.ast.Str;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
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

    // 查询案件所有省
    @Query(value ="select DISTINCT a.city FROM disputecase a where a.province=?1",
        nativeQuery = true)
    List<Object> findDistinctProvince(String province);
    @Query(value ="select count(*) FROM disputecase a where a.city=?1 ",
            nativeQuery = true)
    Integer getCountByCity(String city);

    @Query(value ="select DISTINCT a.mediation_center FROM disputecase a where a.province=?1 and a.city=?2",
            nativeQuery = true)
    List<Object> findDistinctCenter(String province,String city);
    @Query(value ="select count(*) FROM disputecase a where a.mediation_center=?1 ",
            nativeQuery = true)
    Integer getCountByCenter(String mediationCenter);



    //根据涉事者Idcard，查看历史纪录
    @Query(value = "select a.apply_time, a.case_name, a.id, a.brief_case from disputecase a inner join disputecase_apply b on a.id = b.disputecase_id where b.id_card=?1 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "select a.apply_time, a.case_name, a.id, a.brief_case from disputecase a inner join disputecase_apply b on a.id = b.disputecase_id where b.id_card=?1 ORDER BY a.id DESC",
            nativeQuery = true)
    Page<Object[]> findHistoryCaseByUserId(String userId,Pageable pageable);


    @Query(value = "SELECT a.* from disputecase where a.province like ?1 AND a.city like ?2 AND a.mediation_center like ?3 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT a.* from disputecase where a.province like ?1 AND a.city like ?2 AND a.mediation_center like ?3 ORDER BY a.id DESC",
            nativeQuery = true)
    Page<Disputecase> findAllWithDesc(String province,String city,String mediate_center,Pageable pageable);

    /**
     * GetCaseList
     */
    /** 返回所有案件列表,4个查询条件 */
    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.mediator_id=?2 AND a.apply_time >= ?3 AND a.apply_time<?4 AND a.province like ?5 AND a.city like ?6 AND a.mediation_center like ?7 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.mediator_id=?2 AND a.apply_time >= ?3 AND a.apply_time<?4 AND a.province like ?5 AND a.city like ?6 AND a.mediation_center like ?7 ORDER BY a.id DESC",
            nativeQuery = true)
    Page<Disputecase> findWith4Conditions(String filterStatus, String filterMediator, Date startTime,Date endTime,String province,String city,String mediate_center,Pageable pageable);

    /** 返回所有案件列表,1个查询条件 */
    @Query(value ="select a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "select count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY a.id DESC",
            nativeQuery = true)
    Page<Disputecase> findWithProcessStatus(String filterStatus,String province,String city,String mediate_center,Pageable pageable);

    @Query(value ="select a.* from disputecase a where a.mediator_id=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "select count(*) from disputecase a where a.mediator_id=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findByMediatorId(String filterMediator,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a WHERE a.apply_time>=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a WHERE a.apply_time>=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY a.apply_time DESC",
            nativeQuery =true )
    Page<Disputecase> findAfterTime(Date startTime,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a WHERE a.apply_time<?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a WHERE a.apply_time<?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY a.apply_time DESC",
            nativeQuery =true )
    Page<Disputecase> findBeforeTime(Date endTime,String province,String city,String mediate_center,Pageable pageable);

    /** 返回所有案件列表,2个查询条件 */
    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.mediator_id=?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.mediator_id=?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithStatusAndMediator(String filterStatus, String filterMediator,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.apply_time>=?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.apply_time>=?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithStatusAndAfterTime(String filterStatus, Date startTime,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.apply_time<?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.apply_time<?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithStatusAndBeforeTime(String filterStatus, Date endTime,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a where a.mediator_id=?1 AND a.apply_time>=?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a where a.mediator_id=?1 AND a.apply_time>=?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithMediatorAndAfterTime(String filterMediator, Date startTime,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a where a.mediator_id=?1 AND a.apply_time<?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a where a.mediator_id=?1 AND a.apply_time<?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithMediatorAndBeforeTime(String filterMediator, Date endTime,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a where a.apply_time>=?1 AND a.apply_time<?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a where a.apply_time>=?1 AND a.apply_time<?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findBetweenTime(Date startTime,Date endTime,String province,String city,String mediate_center,Pageable pageable);

    /** 返回所有案件列表,3个查询条件 */
    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.mediator_id=?2 AND a.apply_time>=?3 AND a.province like ?4 AND a.city like ?5 AND a.mediation_center like ?6 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.mediator_id=?2 AND a.apply_time>=?3 AND a.province like ?4 AND a.city like ?5 AND a.mediation_center like ?6 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithStatusAndMediatorAndAfterTime(String filterStatus, String filterMediator,Date startTime,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.mediator_id=?2 AND a.apply_time<?3 AND a.province like ?4 AND a.city like ?5 AND a.mediation_center like ?6 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.mediator_id=?2 AND a.apply_time<?3 AND a.province like ?4 AND a.city like ?5 AND a.mediation_center like ?6 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithStatusAndMediatorAndBeforeTime(String filterStatus, String filterMediator,Date endTime,String province,String city,String mediate_center,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.apply_time >= ?2 AND a.apply_time<?3 AND a.province like ?4 AND a.city like ?5 AND a.mediation_center like ?6 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where b.status=?1 AND a.apply_time >= ?2 AND a.apply_time<?3 AND a.province like ?4 AND a.city like ?5 AND a.mediation_center like ?6 ORDER BY ?#{#pageable} ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithStatusAndTime(String filterStatus,Date startTime,Date endTime,String province,String city,String mediate_center,Pageable pageable);



    @Query(value = "SELECT a.* from disputecase a WHERE a.mediator_id=?1 AND a.apply_time>=?2 AND a.apply_time<?3 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a WHERE a.mediator_id=?1 AND a.apply_time>= ?2 AND a.apply_time<?3 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithMediatorAndTime(String filterMediator,Date startTime,Date endTime,String province,String city,String mediate_center,Pageable pageable);


    /**
     * GetMediationHallData 默认案件状态为0或1
     */
    /** 获取所有案件 */
    @Query(value ="select a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9' or b.status='10' or b.status='11') AND a.province like ?1 AND a.city like ?2 AND a.mediation_center like ?3 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "select count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9' or b.status='10' or b.status='11') AND a.province like ?1 AND a.city like ?2 AND a.mediation_center like ?3 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findAll_HallData(String province, String city, String mediateCenter,Pageable pageable);

    @Query(value ="select a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') AND a.province like ?1 AND a.city like ?2 AND a.mediation_center like ?3 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "select count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') AND a.province like ?1 AND a.city like ?2 AND a.mediation_center like ?3 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findAll_HallData_ByLow(String province, String city, String mediateCenter,Pageable pageable);

    @Query(value ="select a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') and a.mediator_id=?1 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "select count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') and a.mediator_id=?1 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findByMediatorId_HallData(String filterMediator,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id WHERE (b.status='1' or b.status='0' or b.status='9') and a.apply_time>=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id WHERE (b.status='1' or b.status='0' or b.status='9') and a.apply_time>=?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY a.apply_time DESC",
            nativeQuery =true )
    Page<Disputecase> findAfterTime_HallData(Date startTime,String province, String city, String mediateCenter,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id WHERE (b.status='1' or b.status='0' or b.status='9') and a.apply_time<?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id WHERE (b.status='1' or b.status='0' or b.status='9') and a.apply_time<?1 AND a.province like ?2 AND a.city like ?3 AND a.mediation_center like ?4 ORDER BY a.apply_time DESC",
            nativeQuery =true )
    Page<Disputecase> findBeforeTime_HallData(Date endTime,String province, String city, String mediateCenter,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') and a.mediator_id=?1 AND a.apply_time>=?2 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') and a.mediator_id=?1 AND a.apply_time>=?2 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithMediatorAndAfterTime_HallData(String filterMediator, Date startTime,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') and a.mediator_id=?1 AND a.apply_time<?2 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') and a.mediator_id=?1 AND a.apply_time<?2 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithMediatorAndBeforeTime_HallData(String filterStatus, Date endTime,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') and a.apply_time>=?1 AND a.apply_time<?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id where (b.status='1' or b.status='0' or b.status='9') and a.apply_time>=?1 AND a.apply_time<?2 AND a.province like ?3 AND a.city like ?4 AND a.mediation_center like ?5 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findBetweenTime_HallData(Date startTime,Date endTime,String province, String city, String mediateCenter,Pageable pageable);

    @Query(value = "SELECT a.* from disputecase a inner join disputecase_process b on a.id=b.disputecase_id WHERE (b.status='1' or b.status='0' or b.status='9') and a.mediator_id=?1 AND a.apply_time>=?2 AND a.apply_time<?3 ORDER BY ?#{#pageable},a.id DESC",
            countQuery = "SELECT count(*) from disputecase a inner join disputecase_process b on a.id=b.disputecase_id WHERE (b.status='1' or b.status='0' or b.status='9') and a.mediator_id=?1 AND a.apply_time>=?2 AND a.apply_time<?3 ORDER BY a.apply_time DESC",
            nativeQuery = true)
    Page<Disputecase> findWithMediatorAndTime_HallData(String filterMediator,Date startTime,Date endTime,Pageable pageable);
}
