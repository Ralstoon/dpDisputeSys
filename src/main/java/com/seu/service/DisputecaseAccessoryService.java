package com.seu.service;

import com.seu.ViewObject.ResultVO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

public interface DisputecaseAccessoryService {
    String uploadFile(FileInputStream file, String fileName);

    ResultVO addNormalUserUpload(String personId, String disputeID, String disputeStatus, String fileType, String fileDescription, String url);

    ResultVO normalFileList(String disputeId);

    /** 问询医院 */
    ResultVO addInquireHospital(MultipartFile[] multipartFiles, String text, String disputeID, String isFinished) throws IOException;

    ResultVO addNotificationAffirm(MultipartFile multipartFile, String disputeId) throws IOException;
    ResultVO addProxyCertification(MultipartFile multipartFile, String disputeId) throws IOException;

    ResultVO addExportApply(MultipartFile application, MultipartFile[] applicationDetail, String disputeId) throws IOException;
}
