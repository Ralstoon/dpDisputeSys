package com.seu.service.impl;

import com.seu.domian.Mediator;
import com.seu.domian.User;
import com.seu.repository.MediatorRepository;
import com.seu.repository.UserRepository;
import com.seu.service.MediatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

/**
 * @ClassName MediatorServiceImpl
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/4 14:32
 * @Version 1.0
 **/
@Service
public class MediatorServiceImpl implements MediatorService {
    @Autowired
    private MediatorRepository mediatorRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public String findNameByID(String ID) {
        Mediator mediator=mediatorRepository.findByFatherId(ID);
        return mediator.getMediatorName();
    }

    @Override
    public boolean isMediator(String phone) {
        User user=userRepository.findByPhone(phone.trim());
        return user.getRole().equals("1")||user.getRole().equals("2");
    }
}
