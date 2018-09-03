package com.seu.controller;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.elasticsearch.MyTransportClient;
import com.seu.enums.DisputeRegisterEnum;
import com.seu.repository.DisputecaseActivitiRepository;
import com.seu.repository.DisputecaseProcessRepository;
import com.seu.repository.MediatorRepository;
import com.seu.service.DisputeProgressService;
import com.seu.service.DisputeRegisterService;
import com.seu.service.MediatorService;
import com.sun.deploy.net.URLEncoder;
import org.activiti.engine.task.Task;
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
    private DisputeRegisterService disputeRegisterService;
    @Autowired
    private DisputecaseActivitiRepository disputecaseActivitiRepository;
    @Autowired
    private DisputeProgressService disputeProgressService;

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

    /** 进入纠纷登记时，获取案件ID */
    @GetMapping(value = "/getCaseId")
    public ResultVO getCaseId(){
        return disputeRegisterService.getCaseId();
    }

    /** 发送涉事人员信息 */
    @PostMapping(value = "/InvolvedPeopleInfo")
    public ResultVO sendInvolvedPeopleInfo(@RequestParam("CaseId") String caseId,
                                           @RequestParam("InvolvedPeople") String involvedPeople){
        return disputeRegisterService.sendInvolvedPeopleInfo(caseId,involvedPeople);
    }

    /** 发送医疗过程数据 */
    @PostMapping(value = "/BasicDivideInfo")
    public ResultVO getBasicDivideInfo(@RequestParam("stageContent") String stageContent,
                                       @RequestParam("CaseId") String caseId,
                                       @RequestParam("mainRecStage") Integer mainRecStage,
                                       @RequestParam("Require") String require,
                                       @RequestParam("claimAmount") Integer claimAmount){
        disputeRegisterService.getBasicDivideInfo(stageContent,caseId,mainRecStage,require,claimAmount);
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Task currentTask=disputeProgressService.searchCurrentTasks(caseId).get(0);  // 纠纷登记
        disputeProgressService.completeCurrentTask(currentTask.getId());

        return  ResultVOUtil.ReturnBack(DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getCode(),DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getMsg());
    }
}
