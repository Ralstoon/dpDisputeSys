package com.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.Disputecase;
import com.seu.domian.DisputecaseAccessory;
import com.seu.domian.DisputecaseProcess;
import com.seu.domian.Mediator;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.VOForm.ManagerCaseForm;
import com.seu.repository.*;
import com.seu.service.impl.DisputeProgressServiceImpl;
import com.seu.utils.GetWorkingTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @ClassName ManagerController
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/11/24 0024 下午 3:25
 * @Version 1.0
 **/

@RestController
@RequestMapping("/DisputeWeb")
@Slf4j
public class ManagerController {
    @Autowired
    private DisputecaseRepository disputecaseRepository;

    @PostMapping("/getProvinceChartData")
    public ResultVO getProvinceChartData(@RequestBody JSONObject obj){
        String province=obj.getString("province");
        JSONObject res=JSONObject.parseObject("{}");
        List<Object> cityList=disputecaseRepository.findDistinctProvince(province);
        ArrayList<String> arr=new ArrayList<>();
        for(Object o:cityList)
            arr.add(o.toString());
        res.put("opinion",arr);
        JSONArray res_1= JSONArray.parseArray("[]");
        for(String one:arr){
            JSONObject o1=JSONObject.parseObject("{}");
            o1.put("name",one);o1.put("value",disputecaseRepository.getCountByCity(one));
            res_1.add(o1);
        }
        res.put("opinionData",res_1);
        return ResultVOUtil.ReturnBack(res,200,"成功");
    }

    @PostMapping("/getCityChartData")
    public ResultVO getCityChartData(@RequestBody JSONObject map){
        String province=map.getString("province");
        String city=map.getString("city");
        JSONObject res=JSONObject.parseObject("{}");
        List<Object> centerList=disputecaseRepository.findDistinctCenter(province,city);
        ArrayList<String> arr=new ArrayList<>();
        for(Object o:centerList)
            arr.add(o.toString());
        res.put("opinion",arr);
        JSONArray res_1= JSONArray.parseArray("[]");
        for(String one:arr){
            JSONObject o1=JSONObject.parseObject("{}");
            o1.put("name",one);o1.put("value",disputecaseRepository.getCountByCenter(one));
            res_1.add(o1);
        }
        res.put("opinionData",res_1);
        return ResultVOUtil.ReturnBack(res,200,"成功");
    }

    @Autowired
    private GetWorkingTimeUtil getWorkingTimeUtil;
    @Autowired
    private DisputecaseProcessRepository disputecaseProcessRepository;
    @Autowired
    private DisputecaseApplyRepository disputecaseApplyRepository;
    @Autowired
    private MediatorRepository mediatorRepository;
    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;
    @Autowired
    private DisputeProgressServiceImpl disputeProgressServiceImpl;

    @PostMapping("/manager/getCase_judiciary")
    public ResultVO getCaseJudiciary(@RequestBody JSONObject map) throws Exception{
        return disputeProgressServiceImpl.getManagerCaseList(map);
//        String province=map.getString("province");
//        String city=map.getString("city");
//        String mediate_center=map.getString("mediate_center");
//        if(province==null)
//            province="%";
//        if(city==null)
//            city="%";
//        if(mediate_center==null)
//            mediate_center="%";
//        PageRequest pageRequest=new PageRequest(map.getInteger("page")-1,map.getInteger("size"));
//        String filterStatus=map.getString("filterStatus");
//        String filterMediator=map.getString("filterMediator");
//        Date startTime=map.getDate("startTime");
//        Date endTime=map.getDate("endTime");
//        Page<Disputecase> disputecasePage=disputecaseRepository.findWith4Conditions(filterStatus, filterMediator, startTime,endTime,province,city,mediate_center,pageRequest);
//        Integer totalPages=disputecasePage.getTotalPages();
//        List<ManagerCaseForm> managerCaseFormList=new ArrayList<>();
//        for(Disputecase disputecase:disputecasePage.getContent()) {
//            ManagerCaseForm managerCaseForm = new ManagerCaseForm();
//            managerCaseForm.setDate(disputecase.getApplyTime());
//            managerCaseForm.setName(disputecase.getCaseName());
//            managerCaseForm.setCaseId(disputecase.getId());
//
//
//            managerCaseForm.setCountdown(getWorkingTimeUtil.calRemainTime(disputecase.getId()));
//
//            /** 案件进程到process表中查询 */
//            String status = disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus();
//            managerCaseForm.setStatus(status);
//            /** 申请人到apply表中查找 */
//            String applerListStr = disputecase.getProposerId();
//            String[] applyList = applerListStr.trim().split(",");
//            List<String> proposerList = new ArrayList<>();
//            for (String s : applyList) {
//                String nameOne = disputecaseApplyRepository.findOne(s).getName();
//                proposerList.add(nameOne);
//            }
//            managerCaseForm.setApplicant(proposerList);
//            /** 被申请人，即医院需要解析医疗过程 */
//            Set<String> respondentList = new HashSet<>();
//            String medicalProcess = disputecase.getMedicalProcess();
//            JSONArray processList = JSONArray.parseArray(medicalProcess);
//            if(processList==null)
//                continue;
//            for (int i = 0; i < processList.size(); ++i) {
//                JSONObject jsStr = processList.getJSONObject(i);
//                JSONObject jsStr_arr=jsStr.getJSONObject("InvolvedInstitute");
//                respondentList.add(jsStr_arr.getString("Hospital"));
//
//            }
//            managerCaseForm.setRespondent(new ArrayList<>(respondentList));
//            /** 当前调解员姓名和id，可能为空，通过调解员id号去mediator表中查找 */
//            String setCurrentMediatorName = null;
//            String setCurrentMediatorId = null;
//            if (disputecase.getMediatorId() == null || disputecase.getMediatorId() == ""){
//                setCurrentMediatorName = "";
//                setCurrentMediatorId = "";
//            }else {
//                setCurrentMediatorName=mediatorRepository.findByFatherId(disputecase.getMediatorId()).getMediatorName();
//                setCurrentMediatorId=disputecase.getMediatorId();
//            }
//            managerCaseForm.setCurrentMediator(setCurrentMediatorName);
//            managerCaseForm.setMediatorid(setCurrentMediatorId);
//            managerCaseForm.setCaseLevel(disputecase.getLevel());
//            /** 用户意向的调解员 */
//            DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputecase.getId());
//            String userChoose=disputecaseProcess.getUserChoose();
//            String mediatorApply = disputecaseProcess.getApplyStatus();
//            String mediatorIntention;
//            if(!(userChoose.trim()=="" || userChoose.trim().equals(""))){
//                for(String s:userChoose.trim().split(",")){
//                    //s是每个用户选择的调解员id号
//                    mediatorIntention = "no";
//                    for(String m:mediatorApply.trim().split(",")){
//                        //m是每个调解员id号
//                        if(m == s){
//                            mediatorIntention = "yes";
//                        }
//                    }
//                    Mediator mediator=mediatorRepository.findByFatherId(s);
//                    managerCaseForm.addUserIntention(mediator.getMediatorName(),mediator.getFatherId(),mediatorIntention);
//                }
//            }
//
//            DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputecase.getId());
//
//            managerCaseForm.setUserUpload(JSONArray.parseArray(disputecaseAccessory.getUserUpload()));
//            managerCaseFormList.add(managerCaseForm);
//        }
//        Map<String,Object> var=new HashMap<>();
//        var.put("CaseList",managerCaseFormList);
//        var.put("totalPages",totalPages);
//        System.out.println("===========================");
//        System.out.println(totalPages);
//        System.out.println(managerCaseFormList);
//        System.out.println("===========================");
//        return ResultVOUtil.ReturnBack(var,200,"success");

    }
}
