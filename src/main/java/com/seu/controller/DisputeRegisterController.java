package com.seu.controller;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.elasticsearch.MyTransportClient;
import com.seu.repository.MediatorRepository;
import com.seu.service.DisputeRegisterService;
import com.seu.service.MediatorService;
import com.sun.deploy.net.URLEncoder;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;


/**
 * @ClassName DisputeRegisterController
 * @Description 纠纷登记界面
 * @Author 吴宇航
 * @Date 2018/8/20 20:12
 * @Version 1.0
 **/

@RestController
@RequestMapping(value = "/RegConflict")
@CrossOrigin
public class DisputeRegisterController {
    @Autowired
    DisputeRegisterService disputeRegisterService;

    @Autowired
    MediatorService mediatorService;

    @GetMapping(value = "/getDiseaseList")
    @Cacheable(value = "constantData",key ="'diseaseList'",unless = "#result.code!=0")
//    @Cacheable(unless = "#result.code!=0")
    public ResultVO getDiseaseList(){
        return disputeRegisterService.getDieaseList();
    }

    @GetMapping(value = "/getFourthPage")
    @Cacheable(value = "constantData",key ="'medicalBehaviorList'",unless = "#result.code!=1")
    public ResultVO getMedicalBehaviorList(){
        return disputeRegisterService.getMedicalBehaviorList();
    }

    @GetMapping(value = "/getRoomList")
    @Cacheable(value = "constantData",key ="'roomList'",unless = "#result.code!=2")
    public ResultVO getRoomList(){
        return disputeRegisterService.getRoomList();
    }



    /**
     *@Author 吴宇航
     *@Description  //TODO仅考虑单个keyword和room，为加入redis缓存
     *@Date 20:40 2018/8/28
     *@Param [keywords, room]
     *@return com.seu.ViewObject.ResultVO
     **/
    @GetMapping(value="/getOperations")
    public ResultVO getOperations(@RequestParam String keywords,
                                  @RequestParam String room) throws Exception{
        return disputeRegisterService.getOperations(keywords,room);
    }

    //用户选择调解员时，获取调解员列表
    @GetMapping(value = "/getMediatorList")
    public ResultVO getMediatorList(@RequestParam(value = "caseId") String id){

        return disputeRegisterService.getMediatorList(id);
    }
}
