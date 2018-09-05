package com.seu.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.ConstantQiniu;
import com.seu.domian.DisputecaseAccessory;
import com.seu.domian.NormalUserUpload;
import com.seu.enums.DisputecaseAccessoryEnum;
import com.seu.repository.DiseaseListRepository;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.service.DisputecaseAccessoryService;
import com.seu.utils.KeyUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisputecaseAccessoryServiceImpl implements DisputecaseAccessoryService {

    @Autowired
    private ConstantQiniu constantQiniu;

    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;

    @Override
    public String uploadFile(FileInputStream file, String fileName){
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        // 其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        // 生成上传凭证，然后准备上传

        try {
            Auth auth = Auth.create(constantQiniu.getAccessKey(), constantQiniu.getSecretKey());
            String upToken = auth.uploadToken(constantQiniu.getBucket());
            try {
                Response response = uploadManager.put(file, fileName, upToken, null, null);
                // 解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

                String returnPath = constantQiniu.getPath() + "/" + putRet.key;
                return returnPath;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public ResultVO addNormalUserUpload(String personId, String disputeID, String disputeStatus, String fileType, String fileDescription, String url) {
        //1.检查是否已经有该案件的附件存在
        //2.否则新增记录，是则做updata
        DisputecaseAccessory dA = disputecaseAccessoryRepository.findByDisputecaseId(disputeID);
        if(dA == null){
            String id = KeyUtil.genUniqueKey();
            NormalUserUpload normalUserUpload = new NormalUserUpload(personId, disputeStatus, fileType, fileDescription, url);
            List<NormalUserUpload> normalUserUploadList =  new ArrayList<NormalUserUpload>();
            normalUserUploadList.add(normalUserUpload);
            String normaluserUpload = JSONArray.fromObject(normalUserUploadList).toString();
            DisputecaseAccessory disputecaseAccessory = new DisputecaseAccessory(id, disputeID, null, normaluserUpload,null);
            disputecaseAccessoryRepository.save(disputecaseAccessory);
            return ResultVOUtil.ReturnBack(normalUserUpload, DisputecaseAccessoryEnum.ADDNORMLUSERUPLOAD_SUCCESS.getCode(), DisputecaseAccessoryEnum.ADDNORMLUSERUPLOAD_SUCCESS.getMsg());
        }
        else {
            DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputeID);
            String normalusrUpload = disputecaseAccessory.getNormaluserUpload();
            List<NormalUserUpload> normalUserUploadList = JSONArray.fromObject(normalusrUpload);
            NormalUserUpload normalUserUpload = new NormalUserUpload(personId, disputeStatus, fileType, fileDescription, url);
            normalUserUploadList.add(normalUserUpload);
            //DisputecaseAccessory disputecaseAccessory = new DisputecaseAccessory(id, disputeID, null, normaluserUpload);
            disputecaseAccessory.setNormaluserUpload(JSONArray.fromObject(normalUserUploadList).toString());
            disputecaseAccessoryRepository.save(disputecaseAccessory);
            return ResultVOUtil.ReturnBack(normalUserUpload, DisputecaseAccessoryEnum.ADDNORMLUSERUPLOAD_SUCCESS.getCode(), DisputecaseAccessoryEnum.ADDNORMLUSERUPLOAD_SUCCESS.getMsg());

        }

    }

    @Override
    public ResultVO normalFileList(String disputeId) {
        DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputeId);
        List<NormalUserUpload> normalUserUploadList = JSONArray.fromObject(disputecaseAccessory.getNormaluserUpload());
        return ResultVOUtil.ReturnBack(normalUserUploadList, DisputecaseAccessoryEnum.GETNORMALUSERUPLOADLIST_SUCCESS.getCode(), DisputecaseAccessoryEnum.GETNORMALUSERUPLOADLIST_SUCCESS.getMsg());
    }

    @Override
    public ResultVO addInquireHospital(String disputeId, String inquireHospital) {
        DisputecaseAccessory dA = disputecaseAccessoryRepository.findByDisputecaseId(disputeId);
        if(dA == null){
            String id = KeyUtil.genUniqueKey();
            DisputecaseAccessory disputecaseAccessory = new DisputecaseAccessory();
            disputecaseAccessory.setId(id);
            disputecaseAccessory.setDisputecaseId(disputeId);
            disputecaseAccessory.setInquireHospital(inquireHospital);
            disputecaseAccessoryRepository.save(disputecaseAccessory);
            return ResultVOUtil.ReturnBack(DisputecaseAccessoryEnum.ADDINQUIREHOSPITAL_SUCCESS.getCode(), DisputecaseAccessoryEnum.ADDINQUIREHOSPITAL_SUCCESS.getMsg());
        }
        else {
            dA.setInquireHospital(inquireHospital);
            disputecaseAccessoryRepository.save(dA);
            return ResultVOUtil.ReturnBack(DisputecaseAccessoryEnum.ADDINQUIREHOSPITAL_SUCCESS.getCode(), DisputecaseAccessoryEnum.ADDINQUIREHOSPITAL_SUCCESS.getMsg());

        }
    }
}
