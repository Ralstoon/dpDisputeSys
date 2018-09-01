package com.seu.repository;

import com.seu.domian.DisputecaseAccessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/*
    private String id;
    private String disputecase_id;
    private String medicaldamage_assessment;
    private String normaluser_upload;
 */

public interface DisputecaseAccessoryRepository extends JpaRepository<DisputecaseAccessory, String> {
    DisputecaseAccessory findByDisputecaseId(String disputecaseId);

    @Query(value = "update disputecase_id set normaluser_upload=?1 where diputecase_id=?2 ", nativeQuery = true)
    @Modifying
    void updateNormluserUpload(String normaluserUpload, String disputecaseId);

}
