package com.seu.service;

/**
 * @ClassName FrameStartUpService
 * @Description 定义一些框架启动时要执行的方法
 * @Author 吴宇航
 * @Date 2018/7/20 15:48
 * @Version 1.0
 **/
public interface FrameStartUpService {
    /** 在框架启动时将管理员和调解员导入activiti的用户和用户组*/
    void initActiUserAndGroup();
}
