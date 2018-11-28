package com.seu.service;

import com.alibaba.fastjson.JSONArray;
import com.seu.ViewObject.ResultVO;
import org.python.antlr.ast.Str;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ManagerService {
    ResultVO addMediator(String mediatorName, String idCard, String mediateCenter, String authorityConfirm,
                         String authorityJudiciary, String basicInformation, String city, String province,
                         String phone, String password, MultipartFile multipartFile) throws IOException;

    ResultVO addContactList(String name,String
                            tele,String
                            contactPerson,String
                            contactPhone,String
                            role,String
                            location,String
                            province,String
                            zone,String
                            city);

    ResultVO addHospitalAndRoom(String zone, String
                                 city, String
                                 hospital, JSONArray
                                 room);

    ResultVO deleteMediator(String phone);

    ResultVO updateMediator(String mediatorName, String idCard, String mediateCenter, String authorityConfirm,
                            String authorityJudiciary, String basicInformation, String city, String province,
                            String phone, String password, MultipartFile multipartFile) throws IOException;

    ResultVO deleteContactList(String id);

    ResultVO updateContactList(String id, String name,String
            tele,String
                                       contactPerson,String
                                       contactPhone,String
                                       role,String
                                       location,String
                                       province,String
                                       zone,String
                                       city);

    ResultVO getContactList(Integer size,Integer page,String province,String city,String zone);
}
