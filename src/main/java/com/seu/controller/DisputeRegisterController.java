package com.seu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.ConstantData;
import com.seu.domian.Disputecase;
import com.seu.domian.DisputecaseApply;
import com.seu.domian.Mediator;
import com.seu.elasticsearch.MyTransportClient;
import com.seu.enums.DisputeRegisterEnum;
import com.seu.form.ExpertAppointForm;
import com.seu.form.HistoryCaseForm;
import com.seu.repository.*;
import com.seu.service.DisputeProgressService;
import com.seu.service.DisputeRegisterService;
import com.seu.service.MediatorService;
import com.seu.utils.Request2JSONobjUtil;
//import com.sun.deploy.net.URLEncoder;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.task.Task;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

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
@Slf4j
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
     *@Description  //TODO仅考虑单个keyword和room，未加入redis缓存
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

//    /** 进入纠纷登记时，获取案件ID */
//    @GetMapping(value = "/getCaseId")
//    public ResultVO getCaseId(){
//        return disputeRegisterService.getCaseId();
//    }

//    /** 发送涉事人员信息 */
//    @PostMapping(value = "/InvolvedPeopleInfo")  //@RequestBody Map<String,String> map
//    public ResultVO sendInvolvedPeopleInfo(HttpServletRequest request){
//
//
//        JSONObject map=Request2JSONobjUtil.convert(request);
//        //String caseId=map.getString("CaseId");
//        String involvedPeople=map.getString("InvolvedPeople");
//        return disputeRegisterService.sendInvolvedPeopleInfo(involvedPeople);
//    }

    /** 发送医疗过程数据 */
    @PostMapping(value = "/BasicDivideInfo")
    public ResultVO getBasicDivideInfo(HttpServletRequest request){
        JSONObject map=Request2JSONobjUtil.convert(request);

        String involvedPeople = map.getString("InvolvedPeople");
        JSONObject basicDivideInfo = map.getJSONObject("basicDivideInfo");


        String caseId = map.getString("caseId");

        //caseId 为空
        if (caseId.equals("") || caseId == null){
            ResultVO res=disputeRegisterService.sendInvolvedPeopleInfo(involvedPeople);
            if(res.getCode()<0)
                return ResultVOUtil.ReturnBack(DisputeRegisterEnum.GETALLMESSAGE_FAIL.getCode(),DisputeRegisterEnum.GETALLMESSAGE_FAIL.getMsg());
            caseId = ((Map<String,String>)res.getData()).get("CaseId");


            String stageContent=basicDivideInfo.getJSONArray("stageContent").toJSONString();
            //String caseId=basicDivideInfo.getJSONObject("CaseId").toJSONString();
            Integer mainRecStage=basicDivideInfo.getInteger("mainRecSatge");//mainRecSatge
            String require=basicDivideInfo.getString("Require");
            Integer claimAmount=basicDivideInfo.getInteger("claimAmount");
            String mediationCenter=basicDivideInfo.getString("mediationCenter");
            String mediationCity=basicDivideInfo.getString("mediationCity");

            disputeRegisterService.getBasicDivideInfo(mediationCity, mediationCenter, stageContent,caseId,mainRecStage,require,claimAmount);
            log.info("\n医疗行为接受完成\n");
            //String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
            //Task currentTask=disputeProgressService.searchCurrentTasks(caseId).get(0);  // 纠纷登记
            //disputeProgressService.completeCurrentTask(currentTask.getId());

            Map<String,Object> var =new HashMap<>();
            var.put("disputeId",caseId);
            var.put("paramProfesor",0);
            var.put("paramAuthenticate",0);
            disputeProgressService.startProcess(caseId,var);
            Task currentTask=disputeProgressService.searchCurrentTasks(caseId).get(0);  // 纠纷登记
            disputeProgressService.completeCurrentTask(currentTask.getId());

            log.info("\n医疗行为任务完成\n");

            Map<String, String> result = new HashMap<>();
            result.put("caseId",caseId);
            return  ResultVOUtil.ReturnBack(result ,DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getCode(),DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getMsg());
        } else {
            String stageContent=basicDivideInfo.getJSONArray("stageContent").toJSONString();
            Integer mainRecStage=basicDivideInfo.getInteger("mainRecSatge");//mainRecSatge
            String require=basicDivideInfo.getString("Require");
            Integer claimAmount=basicDivideInfo.getInteger("claimAmount");

            String mediationCenter=basicDivideInfo.getString("mediationCenter");
            String mediationCity=basicDivideInfo.getString("mediationCity");

            disputeRegisterService.getBasicDivideInfo(mediationCity, mediationCenter, stageContent,caseId,mainRecStage,require,claimAmount);

            Map<String,String> var=new HashMap<>();
            var.put("caseId",caseId);
            return  ResultVOUtil.ReturnBack(var,DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getCode(),"修改成功");

        }


    }

    @Autowired
    private ConstantDataRepository constantDataRepository;

    //获取江苏省市级列表
    @PostMapping(value = "/getCityList")
    public ResultVO getCityList(){
        ConstantData constantData = constantDataRepository.findByName("room_list");
        JSONObject city = JSONObject.parseObject(constantData.getData());
        List<String> cityList = new ArrayList<>(city.keySet());
        Map map = new HashMap();
        map.put("cityList", cityList);
        return ResultVOUtil.ReturnBack(map, 1, "获取城市列表成功");
    }

    //获取对应市的县区列表
    @PostMapping(value = "/getZoneList")
    public ResultVO getZoneList(@RequestBody Map<String, String > map1){
        String city = map1.get("city");
        ConstantData constantData = constantDataRepository.findByName("room_list");
        JSONObject citys = JSONObject.parseObject(constantData.getData());
        List<String> zoneList = new ArrayList<>(citys.getJSONObject(city).keySet());
        Map map = new HashMap();
        map.put("zoneList", zoneList);
        map.put("mediationCenterList", zoneList);
        return ResultVOUtil.ReturnBack(map, 1, "获取县区列表成功");
    }

    //获取对应县区的医院列表
    @PostMapping(value = "/getHospitalList")
    public ResultVO getHospitalList(@RequestBody Map<String, String > map1){
        String city = map1.get("city");
        String zone = map1.get("zone");
        ConstantData constantData = constantDataRepository.findByName("room_list");
        JSONObject citys = JSONObject.parseObject(constantData.getData());
        List<String> hospitalList = new ArrayList<>(citys.getJSONObject(city).getJSONObject(zone).keySet());
        Map map = new HashMap();
        map.put("hospitalList", hospitalList);
        return ResultVOUtil.ReturnBack(map, 1, "获取医院列表成功");
    }

    //获取对应县区的科室列表
    @PostMapping(value = "/getDepartmentList")
    public ResultVO getDepartMentlList(@RequestBody Map<String, String > map1){
        String city = map1.get("city");
        String zone = map1.get("zone");
        String hospital = map1.get("hospital");
        ConstantData constantData = constantDataRepository.findByName("room_list");
        JSONObject citys = JSONObject.parseObject(constantData.getData());
        List<String> departmentList = (List<String>) citys.getJSONObject(city).getJSONObject(zone).get(hospital);
        Map map = new HashMap();
        map.put("departmentList", new HashSet<>(departmentList));
        return ResultVOUtil.ReturnBack(map, 1, "获取科室列表成功");
    }

    /** 发送纠纷登记数据，进行案件登记 */
    @PostMapping(value = "/allMessage")
    public ResultVO getAllMessage(@RequestBody JSONObject obj){

        return disputeRegisterService.getAllMessage(obj.getJSONObject("regConflictData"));
    }


    @Autowired
    private DisputecaseRepository disputecaseRepository;
    //收藏类案推荐成功
    @PostMapping(value = "/similarCasesCollect")
    public ResultVO similarCasesCollect(@RequestBody JSONObject obj){
        String caseId = obj.getString("caseId");
        String list = obj.getJSONObject("list").toJSONString();
        Disputecase disputecase = disputecaseRepository.findOne(caseId);
        disputecase.setRecommendedPaper(list);
        disputecaseRepository.save(disputecase);
        return ResultVOUtil.ReturnBack(123, "收藏类案推荐成功");
    }

    @Autowired
    private DisputecaseApplyRepository disputecaseApplyRepository;

    @PostMapping(value = "/getHistoryCase")
    public ResultVO getHistoryCase(@RequestBody JSONObject map){
        List<String> InvolvedPeople = (List<String>) map.get("cardId");
        int size= map.getInteger("size");
        int page=map.getInteger("page") -1;
        PageRequest pageRequest=new PageRequest(page,size);
        Page<Object[]> pages=null;
        Integer totalPages=null;
        List<HistoryCaseForm> HistoryCaseFormList = new ArrayList<>();
        for (int i = 0; i < InvolvedPeople.size(); i++){
            pages = disputecaseRepository.findHistoryCaseByUserId(InvolvedPeople.get(i),pageRequest);
            for(Object[] obj:pages.getContent()){

                Disputecase disputecase = disputecaseRepository.findOne(obj[2].toString());

                JSONArray arr = JSONArray.parseArray(disputecase.getMedicalProcess());
                List<String> hospitalList = new ArrayList<>();
                String hospitals = "";

                for (Object stage:arr){
                    Object involvedInstitute = ((com.alibaba.fastjson.JSONObject) stage).get("InvolvedInstitute");

//            for(Object hospital: (com.alibaba.fastjson.JSONArray)involvedInstitute){
//
//                hospitalList.add((String)(((com.alibaba.fastjson.JSONObject)hospital).get("Hospital")));
//            }
                    hospitalList.add(((JSONObject)involvedInstitute).getString("Hospital"));
                }

                for(String hospital: hospitalList){
                    hospitals = hospitals + hospital + "、";
                }

                String[] temp=disputecase.getProposerId().trim().split(",");
                List<String> names=new ArrayList<>();
                for(String s:temp){
                    names.add(disputecaseApplyRepository.findOne(s).getName());
                }


                HistoryCaseForm form=new HistoryCaseForm(obj[0].toString(),obj[1].toString(), obj[2].toString(), obj[3].toString(),String.join("、", names) ,hospitals.substring(0,hospitals.length()-1) );
                HistoryCaseFormList.add(form);
            }
            totalPages = pages.getTotalPages();
        }


        Map<String, Object> result = new HashMap<>();
        result.put("totalPages", totalPages);
        result.put("HistoryCaseFormList",HistoryCaseFormList);
        return ResultVOUtil.ReturnBack(result,123,"历史案件");
    }


}
