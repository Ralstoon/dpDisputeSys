package com.seu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.ConstantData;
import com.seu.enums.DisputeRegisterEnum;
import com.seu.repository.DiseaseListRepository;
import com.seu.service.DisputeRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DisputeRegisterServiceImpl
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/20 20:43
 * @Version 1.0
 **/
@Service
public class DisputeRegisterServiceImpl implements DisputeRegisterService {

    @Autowired
    DiseaseListRepository diseaseListRepository;

    @Override
    public ResultVO getDieaseList() {
        ConstantData constantData=diseaseListRepository.findByName("disease_list");
        JSONObject jsStr=JSONObject.parseObject(constantData.getData());
        List<String> diseaseKind=new ArrayList<>();
        List<Object> diseaseName=new ArrayList<>();
        for(String key:jsStr.keySet()){
            diseaseKind.add(key);
            diseaseName.add(jsStr.get(key));
        }
        Map<String,Object> map=new HashMap<>();
        map.put("DiseaseKind",diseaseKind);
        map.put("DiseaseName",diseaseName);
        return ResultVOUtil.ReturnBack(map,DisputeRegisterEnum.GETDISEASELIST_SUCCESS.getCode(),DisputeRegisterEnum.GETDISEASELIST_SUCCESS.getMsg());
    }
}
