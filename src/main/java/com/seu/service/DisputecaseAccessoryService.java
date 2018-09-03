package com.seu.service;

import com.seu.ViewObject.ResultVO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;

public interface DisputecaseAccessoryService {
    String uploadFile(FileInputStream file, String fileName);

    ResultVO addNormalUserUpload(String personId, String disputeID, String disputeStatus, String fileType, String fileDescription, String url);

    ResultVO normalFileList(String disputeId);

    ResultVO addInquireHospital(String disputeId, String inquireHospital);
}
