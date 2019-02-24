package com.seu.repository;

import com.seu.domian.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @ClassName AdminRepository
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/20 15:46
 * @Version 1.0
 **/
public interface AdminRepository extends JpaRepository<Admin,String> {
    Admin findByFatherId(String ID);

    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2%",
            nativeQuery = true)
    Page<Admin> findAllByProvinceAndCity(String province, String city, Pageable pageable);

    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and (admin.level = ?3 or admin.case_mange_level = ?4) and admin.id != ?5 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and (admin.level = ?3 or admin.case_mange_level = ?4) and admin.id != ?5 ",
            nativeQuery = true)
    Page<Admin> findAllByProvinceAndCityAndLev(String province, String city, String level1, String level2,String selfId, Pageable pageable);

    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.level = ?3 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.level = ?3",
            nativeQuery = true)
    Page<Admin> findAllByProvinceAndCityAndLevel(String province, String city, String filterType, Pageable pageable);

    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.case_mange_level = ?3 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.case_mange_level = ?3",
            nativeQuery = true)
    Page<Admin> findAllByProvinceAndCityAndCaseMangeLevel(String province, String city, String filterType, Pageable pageable);

    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.level = ?3 and admin.case_mange_level = ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.level = ?3 and admin.case_mange_level = ?4",
            nativeQuery = true)
    Page<Admin> findAllByProvinceAndCityAndCaseMangeLevelAndLevel(String province, String city, String filterType, String filterType2, Pageable pageable);

    //查找有level权限的admin
    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.level = ?3 admin.id != ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.level = ?3 admin.id != ?4",
            nativeQuery = true)
    Page<Admin> findAllWithHasLevel(String province, String city,String level, String selfId, Pageable pageable);

    //查找有casemangelevel权限的admin
    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.case_mange_level  = ?3 admin.id != ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.case_mange_level = ?3 admin.id != ?4",
            nativeQuery = true)
    Page<Admin> findAllWithHasCaseManageLevel(String province, String city,String level, String selfId, Pageable pageable);

    //查找无level权限的admin
    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.level = \"\" and admin.case_mange_level =?3 and admin.id != ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.level = \"\" and admin.case_mange_level =?3 admin.id!= ?4",
            nativeQuery = true)
    Page<Admin> findAllWithNotHasLevel(String province, String city,String level, String selfId, Pageable pageable);

    //查找无casemangelevel权限的admin
    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.case_mange_level = \"\" and admin.level = ?3 and  admin.id != ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.case_mange_level = \"\" and admin.level = ?3 and admin.id != ?4",
            nativeQuery = true)
    Page<Admin> findAllWithNotHasCaseManageLevel(String province, String city,String level,String selfId, Pageable pageable);

    //查找有level权限,有casemanagelevel的admin
    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.level = ?3 and admin.case_mange_level = ?3 and admin.id != ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.level = ?3 and admin.case_mange_level =?3 and admin.id != ?4",
            nativeQuery = true)
    Page<Admin> findAllWithHasLevelAndHasCaseManageLevel(String province, String city,String level, String selfId, Pageable pageable);

    //查找有level权限,无casemanagelevel的admin
    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.level =?3 and admin.case_mange_level = \"\" and admin.id != ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.level =?3 and admin.case_mange_level = \"\" and admin.id != ?4",
            nativeQuery = true)
    Page<Admin> findAllWithHasLevelAndNotHasCaseManageLevel(String province, String city,String level, String selfId, Pageable pageable);

    //查找无level权限,有casemanagelevel的admin
    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.level =\"\" and admin.case_mange_level = ?3 and admin.id != ?4 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.level =\"\" and admin.case_mange_level = ?3 and admin.id != ?4",
            nativeQuery = true)
    Page<Admin> findAllWithNotHasLevelAndHasCaseManageLevel(String province, String city,String level,String selfId, Pageable pageable);

    //查找无level权限,无casemanagelevel的admin
    @Query(value = "select * from admin where admin.province like %?1% and admin.city like %?2% and admin.level =\"\" and admin.case_mange_level =\"\" and admin.id != ?3 ORDER BY ?#{#pageable}",
            countQuery = "select count(*) from admin where admin.province like %?1% and admin.city like %?2% and admin.level \"\" and admin.case_mange_level \"\" and admin.id != ?3",
            nativeQuery = true)
    Page<Admin> findAllWithNotHasLevelAndNotHasCaseManageLevel(String province, String city, String selfId, Pageable pageable);
}
