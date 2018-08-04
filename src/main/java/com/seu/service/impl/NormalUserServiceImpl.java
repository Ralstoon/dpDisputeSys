package com.seu.service.impl;

import com.seu.converter.NormalUserForm2NormalUser;
import com.seu.domian.NormalUser;
import com.seu.domian.User;
import com.seu.form.NormalUserForm;
import com.seu.repository.NormalUserRepository;
import com.seu.repository.UserRepository;
import com.seu.service.NormalUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;


@Service
public class NormalUserServiceImpl implements NormalUserService {


    @Autowired
    private NormalUserRepository normalUserRepository;
    @Autowired
    private UserRepository userRepository;


//    @Override
//    public int registerWithNormalUserDetail(String user_id) {
//        NormalUserDetail normalUserDetail=new NormalUserDetail();
//        String detail_id=KeyUtil.genUniqueKey();
//        normalUserDetail.setDetailId(detail_id);
//        normalUserDetail.setUserId(user_id);
//        NormalUserDetail result=normalUserDetailRepository.save(normalUserDetail);
//        if(result==null){
//            return -1;
//        }
//        return 1;
//    }

    @Override
    public NormalUserForm updateNormalUser(NormalUserForm normalUserForm){
        //先从NormalUserForm转换为NormalUser
        NormalUser normalUser=normalUserRepository.findByFatherId(normalUserForm.getFatherId());
        NormalUser result=NormalUserForm2NormalUser.convert(normalUserForm,normalUser);
        //更新NormalUser
        NormalUser saveResult = normalUserRepository.save(result);
        if(saveResult==null){
            return null;
        }
        BeanUtils.copyProperties(saveResult,normalUserForm);
        String ageStr=normalUserForm.getAge();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        normalUserForm.setAge(sdf.format(saveResult.getAge()));
        return normalUserForm;
    }

    @Override
    public String findNormalUserNameByFatherId(String fatherId) {
        NormalUser normalUser=normalUserRepository.findByFatherId(fatherId);
        return normalUser.getName();
    }

    @Override
    public String findEmailByUserId(String fatherId) {
        NormalUser normalUser=normalUserRepository.findByFatherId(fatherId);
        return normalUser.getEmail();
    }

    @Override
    public String findPhoneByUserId(String fatherId) {
        User user=userRepository.getOne(fatherId);
        return user.getPhone();
    }
}
