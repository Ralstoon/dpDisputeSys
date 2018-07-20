package com.seu.service.impl;

import com.seu.domian.Admin;
import com.seu.domian.Mediator;
import com.seu.repository.AdminRepository;
import com.seu.repository.MediatorRepository;
import com.seu.service.FrameStartUpService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName FrameStartUpServiceImpl
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/20 15:50
 * @Version 1.0
 **/
@Service
@Slf4j
public class FrameStartUpServiceImpl implements FrameStartUpService {
    @Autowired
    private IdentityService identityService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private MediatorRepository mediatorRepository;


    @Override
    public void initActiUserAndGroup() {
        // TODO 直接全部取出有待优化，可以使用分页
        List<Admin> admins=adminRepository.findAll();
        List<Mediator> mediators=mediatorRepository.findAll();
        Group adminGroup=identityService.createGroupQuery().groupId("administrator").singleResult();
        Group mediatorGroup=identityService.createGroupQuery().groupId("mediator").singleResult();

        for(Admin adminOne:admins){
            User user=identityService.newUser(adminOne.getAdminId());
            identityService.createMembership(user.getId(),adminGroup.getId());
        }
        for(Mediator mediatorOne:mediators){
            User user=identityService.newUser(mediatorOne.getMediatorId());
            identityService.createMembership(user.getId(),mediatorGroup.getId());
        }
        log.info("【完成将管理员和调解员导入activiti用户和用户组】");
    }
}
