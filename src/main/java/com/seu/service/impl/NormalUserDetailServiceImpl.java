package com.seu.service.impl;

import com.seu.converter.NormalUserDetailForm2NormalUserDetail;
import com.seu.domian.NormalUserDetail;
import com.seu.form.NormalUserDetailForm;
import com.seu.repository.NormalUserDetailRepository;
import com.seu.service.NormalUserDetailService;
import com.seu.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NormalUserDetailServiceImpl implements NormalUserDetailService {

    @Autowired
    private NormalUserDetailRepository normalUserDetailRepository;


    @Override
    public int registerWithNormalUserDetail(String user_id) {
        NormalUserDetail normalUserDetail=new NormalUserDetail();
        String detail_id=KeyUtil.genUniqueKey();
        normalUserDetail.setDetailId(detail_id);
        normalUserDetail.setUserId(user_id);
        NormalUserDetail result=normalUserDetailRepository.save(normalUserDetail);
        if(result==null){
            return -1;
        }
        return 1;
    }

    @Override
    public NormalUserDetailForm updateNormalUserDetail(NormalUserDetailForm normalUserDetailForm,String userId) {
        //先从NormalUserDetailForm转换为NormalUserDetail
        NormalUserDetail normalUserDetail=normalUserDetailRepository.findByUserId(userId);
        NormalUserDetail result=NormalUserDetailForm2NormalUserDetail.convert(normalUserDetailForm,normalUserDetail);
//        result.setUserId(userId);
        //更新NormalUserDetail
        NormalUserDetail resutDetail = normalUserDetailRepository.save(result);
        if(resutDetail==null){
            return null;
        }
        BeanUtils.copyProperties(resutDetail,normalUserDetailForm);
        return normalUserDetailForm;
    }

    @Override
    public String findNormalUserNameByUserId(String userId) {
        NormalUserDetail normalUserDetail=normalUserDetailRepository.findByUserId(userId);
        return normalUserDetail.getName();
    }

    @Override
    public String findEmailByUserId(String userId) {
        NormalUserDetail normalUserDetail=normalUserDetailRepository.findByUserId(userId);
        return normalUserDetail.getEmail();
    }
}
