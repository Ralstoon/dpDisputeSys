package com.seu.repository;

import com.seu.domian.Mediator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * @ClassName MediatorRepository
 * @Description
 * @Author 吴宇航
 * @Date 2018/7/20 15:47
 * @Version 1.0
 **/
public interface MediatorRepository extends JpaRepository<Mediator,String> {
    Mediator findByFatherId(String ID);

    /** 获取另外分配的调解员列表： 剔除用户意向和调解员回避 */
    @Query(value = "select * from mediator where FIND_IN_SET(father_id,?1)=0 AND FIND_IN_SET(father_id,?2)=0 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from mediator where FIND_IN_SET(father_id,?1)=0 AND FIND_IN_SET(father_id,?2)=0",
            nativeQuery = true)
    Page<Mediator> findAllWithoutUserChooseAndAvoid(String userChoose, String avoidStatus, Pageable pageable);

    Page<Mediator> findAllByAuthorityConfirmAndAuthorityJudiciary(String filterType1,String filterType2,Pageable pageable);
    Page<Mediator> findAllByAuthorityConfirm(String filterType,Pageable pageable);
    Page<Mediator> findAllByAuthorityJudiciary(String filterType,Pageable pageable);


}
