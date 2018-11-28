package com.seu.repository;

import com.seu.domian.Mediator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.*;
import java.util.List;


/**
 * @ClassName MediatorRepository
 * @Description
 * @Author 吴宇航
 * @Date 2018/7/20 15:47
 * @Version 1.0
 **/
public interface MediatorRepository extends JpaRepository<Mediator,String> {
    Mediator findByFatherId(String ID);

    @Query(value = "select * from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3%  ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from mediator where  mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3% ",
            nativeQuery = true)
    Page<Mediator> findAll(String province, String  city,String mediate_center, Pageable pageable);

    /** 获取另外分配的调解员列表： 剔除用户意向和调解员回避 */
    @Query(value = "select * from mediator where FIND_IN_SET(father_id,?1)=0 AND FIND_IN_SET(father_id,?2)=0 and mediator.province like %?3% and mediator.city like %?4% and mediator.mediate_center like %?5%  ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from mediator where FIND_IN_SET(father_id,?1)=0 AND FIND_IN_SET(father_id,?2)=0 and mediator.province like %?3% and mediator.city like %?4% and mediator.mediate_center like %?5% ",
            nativeQuery = true)
    Page<Mediator> findAllWithoutUserChooseAndAvoidByMediatorCenter(String userChoose, String avoidStatus, String province, String city, String mediateCenter, Pageable pageable);

    @Query(value = "select * from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3% and mediator.authority_confirm = ?4 and authority_judiciary = ?5 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3% and mediator.authority_confirm = ?4 and authority_judiciary = ?5",
            nativeQuery = true)
    Page<Mediator> findAllByAuthorityConfirmAndAuthorityJudiciaryByMediatorCenter(String province, String city, String mediateCenter,String filterType1,String filterType2,Pageable pageable);

    @Query(value = "select * from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3% and mediator.authority_confirm = ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3% and mediator.authority_confirm = ?4",
            nativeQuery = true)
    Page<Mediator> findAllByAuthorityConfirmByMediatorCenter(String province, String city, String mediateCenter,String filterType,Pageable pageable);

    @Query(value = "select * from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3% and authority_judiciary = ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3% and authority_judiciary = ?4",
            nativeQuery = true)
    Page<Mediator> findAllByAuthorityJudiciaryByMediatorCenter(String province, String city, String mediateCenter,String filterType,Pageable pageable);

    @Query(value = "select * from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3%",
            countQuery = "select count(*) from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3%",
            nativeQuery = true)
    List<Mediator> findByMediateCenter(String province, String city, String mediateCenter);

    @Query(value = "select * from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3% ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from mediator where mediator.province like %?1% and mediator.city like %?2% and mediator.mediate_center like %?3%",
            nativeQuery = true)
    Page<Mediator> findAllByMediatorCenter(String province, String city, String mediateCenter,Pageable pageable);

}
