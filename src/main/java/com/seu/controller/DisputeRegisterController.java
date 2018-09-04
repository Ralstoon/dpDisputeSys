package com.seu.controller;

import com.alibaba.fastjson.JSONObject;
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
import com.seu.utils.Request2JSONobjUtil;
import com.sun.deploy.net.URLEncoder;
import org.activiti.engine.task.Task;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;


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
     *@Description  // TODO 仅考虑单个keyword和room，为加入redis缓存
     *@Date 20:40 2018/8/28
     *@Param [keywords, room]
     *@return com.seu.ViewObject.ResultVO
     **/
    @GetMapping(value="/getOperations")
    public ResultVO getOperations(@RequestBody Map<String ,String> map) throws Exception{
        String keywords=map.get("keywords");
        String room=map.get("room");
        return disputeRegisterService.getOperations(keywords,room);
    }

    //用户选择调解员时，获取调解员列表
    @GetMapping(value = "/getMediatorList")
    public ResultVO getMediatorList(@RequestBody Map<String,String> map){
        String id=map.get("caseId");
        return disputeRegisterService.getMediatorList(id);
    }

    /** 进入纠纷登记时，获取案件ID */
    @GetMapping(value = "/getCaseId")
    public ResultVO getCaseId(){
        return disputeRegisterService.getCaseId();
    }

    /** 发送涉事人员信息 */
    @PostMapping(value = "/InvolvedPeopleInfo")
    public ResultVO sendInvolvedPeopleInfo(@RequestBody Map<String,String> map){

        String caseId=map.get("CaseId");
        String involvedPeople=map.get("InvolvedPeople");
        return disputeRegisterService.sendInvolvedPeopleInfo(caseId,involvedPeople);
    }

    /** 发送医疗过程数据 */
    @PostMapping(value = "/BasicDivideInfo")
    public ResultVO getBasicDivideInfo(HttpServletRequest request){
        JSONObject map=Request2JSONobjUtil.convert(request);
        String stageContent=map.getString("stageContent");
        String caseId=map.getString("CaseId");
        Integer mainRecStage=map.getInteger("mainRecStage");
        String require=map.getString("Require");
        Integer claimAmount=map.getInteger("claimAmount");
        disputeRegisterService.getBasicDivideInfo(stageContent,caseId,mainRecStage,require,claimAmount);
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Task currentTask=disputeProgressService.searchCurrentTasks(caseId).get(0);  // 纠纷登记
        disputeProgressService.completeCurrentTask(currentTask.getId());

        return  ResultVOUtil.ReturnBack(DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getCode(),DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getMsg());
    }
}
