package com.seu.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.Disputecase;
import com.seu.domian.DisputecaseAccessory;
import com.seu.domian.DisputecaseProcess;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.ChangeAuthorityForm;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import com.seu.form.VOForm.NormalUserUploadListForm;
import com.seu.repository.*;
import com.seu.service.*;
import com.seu.utils.DisputeProcessReturnMap;
import com.seu.utils.GetTitleAndAbstract;
import com.seu.utils.Request2JSONobjUtil;
import com.seu.utils.VerifyProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/DisputeWeb")
@CrossOrigin
@Slf4j
public class DisputeProgressController {
    @Autowired
    private DisputeProgressService disputeProgressService;
    @Autowired
    private NormalUserService normalUserService;
    @Autowired
    private MediatorService mediatorService;

    @Autowired
    private UserService userService;

    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;
    @Autowired
    private DisputecaseProcessRepository disputecaseProcessRepository;

    @Autowired
    private VerifyProcessUtil verifyProcessUtil;


    /*
     *@Author 吴宇航
     *@Description  立案判断功能，管理者或有权限的调解员选定具体案例并且审核
     *@Date 21:11 2018/7/20
     *@Param [result通过与否, starterId当前审核案件用户id, session]
     *@return com.seu.ViewObject.ResultVO
     **/
    @PostMapping(value="/DisposeApply")
    @Transactional
    public ResultVO caseAccept(@RequestParam(value = "file", required=false) MultipartFile multipartFile,
                               @RequestParam("result") Integer result,
                               @RequestParam("caseId") String disputeId,
                               @RequestParam("id") String ID) throws Exception {
        log.info("\n开始 立案判断\n");
        String name=userService.findNameById(ID);
        List<Task> tasks=verifyProcessUtil.verifyTask(disputeId,"立案判断");
        Map<String,Object> var=new HashMap<>();
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputeId);
        String status=null;
        if(result==0){
            var.put("caseAccept",0);  //不通过
//            disputecaseProcess.setStatus("8");
            status="8";
        }

        else{
            var.put("caseAccept",1);  //通过
//            disputecaseProcess.setStatus("1");
            status="1";
        }
        if(result!=0){
            /** 向数据库process表中添加纠纷开始时间，并计算30个工作日后的结束时间 */
            log.info("\n开始 计算工作日\n");
            disputeProgressService.setStartTimeAndEndTime(disputeId);
            log.info("\n结束 计算工作日\n");
        }


        if(multipartFile!=null){
            log.info("\n开始 上传文件\n");
            disputecaseAccessoryService.addAcceptanceNotification(multipartFile, disputeId);
            log.info("\n结束 上传文件\n");
        }
        log.info("\n结束 立案判断\n");
        disputeProgressService.completeCurrentTask(tasks.get(0).getId(),var);
        log.info("\n开始 修改案件状态\n");
        System.out.println(status);
        System.out.println(disputeId);
        disputeProgressService.updateCaseStatus(disputeId,status);
        log.info("\n结束 修改案件状态\n");
        Map<String,Object> param=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),name);
        return ResultVOUtil.ReturnBack(param,DisputeProgressEnum.CASEACCEPT_SUCCESS.getCode(),DisputeProgressEnum.CASEACCEPT_SUCCESS.getMsg());
    }


    /*
     *@Author 吴宇航
     *@Description  用户提出对纠纷登记内容的修改,将之前登记的内容返还给前端
     *@Date 21:14 2018/7/20
     *@Param []
     *@return com.seu.ViewObject.ResultVO
     **/
    @PostMapping(value="/disputeRegisterModify")
    public ResultVO disputeRegisterModify(@RequestParam("ID") String ID,
                                          @RequestParam("disputeId") String disputeId){
//        String starterId=((NormalUser)session.getAttribute("currentUser")).getUserId();
        String signal="disputeRegisterModifySignal";
        Task currentTask=disputeProgressService.searchCurrentTasks(disputeId).get(0);
        // 先取出流程中的纠纷案件内容,设置为DisputeRegisterDetailForm形式
        DisputeRegisterDetailForm disputeRegisterDetailForm=disputeProgressService.getDisputeForm(disputeId) ;
        disputeProgressService.triggerSignalBoundary(signal);
        Task afterTask=disputeProgressService.searchCurrentTasks(disputeId).get(0);
        String name=normalUserService.findNormalUserNameByFatherId(ID);
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(currentTask.getName(),name);
        map.put("after task",afterTask.getName());
        map.put("disputeRegisterContent",disputeRegisterDetailForm);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.TRIGGERSIGNAL_SUCCESS.getCode(),DisputeProgressEnum.TRIGGERSIGNAL_SUCCESS.getMsg());
    }



    @PostMapping(value = "/historicTaskList")
    public ResultVO getHistoricTaskListByDispute(@RequestParam(value = "disputeId") String disputeId){
        List<HistoricTaskForm> historicTaskFormList = disputeProgressService
                .getHistoricTaskListByDisputeId(disputeId);
        Map<String,Object> map=new HashMap<>();
        map.put("historicTaskFormList",historicTaskFormList);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.SEARCH_HISTORICTASKLIST_SUCESS.getCode(),DisputeProgressEnum.SEARCH_HISTORICTASKLIST_SUCESS.getMsg());
    }



    /** 调解员申请回避 */
    @PostMapping(value = "/mediator/fordebarb")
    public ResultVO mediatorFordebarb(@RequestBody Map<String, String > map){

        String disputeId = map.get("caseId");
        String ID = map.get("id");

        /** 为案件进程表添加调解员回避状态*/
        disputeProgressService.updateAvoidStatus(disputeId,ID);
        return ResultVOUtil.ReturnBack(DisputeProgressEnum.MEDIATORAVOID_SUCCESS.getCode(),DisputeProgressEnum.MEDIATORAVOID_SUCCESS.getMsg());
    }


    /** 用户选择调解员列表 */
    @PostMapping(value = "user/postMediatorList")
    public ResultVO getUserChoose(@RequestBody Map<String, Object > map){

        List<String> pickedList = (List<String>) map.get("MediatorPickedlist");


        //String pickedList = map.get("MediatorPickedlist");
        String disputeId = (String) map.get("CaseId");

        String mediatorList="";
        for(int i = 0; i<pickedList.size(); i++){
            mediatorList = mediatorList + pickedList.get(i) + ",";
        }
        mediatorList=mediatorList.substring(0,mediatorList.length()-1);
        disputeProgressService.updateUserChoose(disputeId,mediatorList);
        return ResultVOUtil.ReturnBack(DisputeProgressEnum.USERCHOOSEMEDIATOR_SUCCESS.getCode(),DisputeProgressEnum.USERCHOOSEMEDIATOR_SUCCESS.getMsg());
    }

    /** 获取调解大厅中的数据 */
    @PostMapping(value = "/mediator/GetMediationHallData")
    public ResultVO getMediationHallData(@RequestBody JSONObject map){

        return disputeProgressService.getMediationHallData(map);
    }

    /** 获取我的调节中的数据 */
    @PostMapping(value = "/mediator/GetMyMediationData")
    public ResultVO getMyMediationData(@RequestBody JSONObject map) throws Exception{

        return disputeProgressService.getMyMediationData(map);
    }

    /** 管理者获取案件列表 */
    @PostMapping(value = "/manager/getCaseList")
    public ResultVO getManagerCaseList(@RequestBody JSONObject map) throws Exception{
        return disputeProgressService.getManagerCaseList(map);
    }


    /** 管理员获取统计管理页面列表
     * 上送司法厅的分页筛选*/
    @PostMapping(value = "/manager/getCase_judiciary")
    public ResultVO getManagerCaseJudiciary(@RequestBody Map<String, String> map){
//
        String id = map.get("id");

        return disputeProgressService.getManagerCaseJudiciary(id);
    }

    /** 管理员获取所有的调解员列表(分页) */
    @PostMapping(value = "/manager/getMediatorListWithPage")
    public ResultVO getMediatorListWithPage(@RequestBody JSONObject map){
        log.info("\n开始 [获取所有调解员列表]\n");
        String caseId=map.getString("caseId");
        Integer page=map.getInteger("page")-1;  // 前端从一开始，后台从0开始
        Integer size=map.getInteger("size");
        PageRequest pageRequest=new PageRequest(page,size);
        log.info("\n结束 [获取所有调解员列表]\n");
        return disputeProgressService.getMediatorList(caseId,pageRequest);
    }

    /** 管理员 获取所有调解员的授权信息 */
    @PostMapping(value = "/manager/getNameofAuthorityList")
    public ResultVO getNameofAuthorityList(@RequestBody JSONObject map){

        return disputeProgressService.getNameofAuthorityList(map);
    }

    /**管理员发送授权调解员权限信息 */
    @PostMapping(value = "/manager/ChangeAuthorityNameList")
    public ResultVO changeAuthorityNameList(@RequestBody JSONObject map){

        return disputeProgressService.changeAuthorityNameList(map);
    }

    //添加具体任务的评价
    @PostMapping(value = "/addTaskComment")
    public ResultVO addTaskComment(@RequestBody Map<String, String> map){

        String comment = map.get("comment");
        String taskId = map.get("taskId");
        String disputeId = map.get("disputeId");

        CommentForm commentForm = disputeProgressService.addCommentByTaskId(taskId, disputeId, comment);
        return ResultVOUtil.ReturnBack(commentForm,DisputeProgressEnum.ADD_TASKCOMMIT_SUCCESS.getCode(),DisputeProgressEnum.ADD_TASKCOMMIT_SUCCESS.getMsg());
    }

    /** 进入调解时 获取当前调解阶段、是否具备医疗鉴定资格、医疗鉴定与否、是否具备专家预约资格，当前阶段中的当前步骤（医疗鉴定中、预约中、正在调解中、调解结束）*/
    @PostMapping(value = "/mediator/getMediationStage")
    public ResultVO getMediationStage(@RequestBody Map<String, String> map){

        String caseId = map.get("caseId");

        return disputeProgressService.getMediationStage(caseId);
    }

    /** 发送医疗鉴定结果数据 */
    @PostMapping(value = "/mediator/resultOfIndent")
    public ResultVO setResultOfIndent(@RequestParam("caseId") String caseId,
                                      @RequestParam("text") String text,
                                      @RequestParam(value = "files") MultipartFile[] multipartFiles,
                                      @RequestParam(value = "stage") String stage){

        return disputeProgressService.setResultOfIndent(caseId,text,multipartFiles,stage);
    }

    @Autowired
    private DisputecaseRepository disputecaseRepository;

//    // 10/12 20:55 添加 wj
//    // 请求纠纷要素功能
//    @PostMapping(value = "/mediator/getKeyWordList")
//    public ResultVO getKeyWordList(@RequestBody Map<String, String> map){
//        String id = map.get("id");//操作人id
//        String caseId = map.get("caseId");
//        JSONArray jsonArray = JSONArray.parseArray(disputecaseRepository.findOne(caseId).getMedicalProcess());
//
//        JSONArray result = JSONArray.parseArray("[]");
//
//
//        for(Object stage : jsonArray){
//
//
//            //获取resultOfRegConflict列表
//            JSONArray resultOfRegConflict = ((JSONObject) stage).getJSONArray("resultOfRegConflict");
//            for(Object eachResult: resultOfRegConflict){
//                JSONObject jsonObject = JSONObject.parseObject("{}");
//                if(
//                        ((JSONObject)eachResult).getString("test").isEmpty()
//
//
//                                && ((List<String>)((JSONObject)eachResult).get("operation")).size() == 0
//
//                                && ((JSONObject)eachResult).getString("medicine").isEmpty()
//
//                                && ((JSONObject)eachResult).getString("added").isEmpty()
//
//                                && ((JSONObject)eachResult).getString("syndrome").isEmpty()
//
//
//                                && ((List<String>)((JSONObject) eachResult).get("diseasesymptomAfter")).size() ==0
//
//
//                                && ((List<String>)((JSONObject) eachResult).get("diseaseAfter")).size() ==0
//
//                                && ((List<String>)((JSONObject) eachResult).get("defaultBehavior")).size() ==0
//
//                                && ((List<String>)((JSONObject) eachResult).get("diseaseBefore")).size() ==0
//
//                                && ((List<String>)((JSONObject) eachResult).get("diseasesymptomBefore")).size() ==0)
//
//                    continue;
//                if( ((JSONObject)eachResult).get("test") != null && !((JSONObject)eachResult).getString("test").isEmpty() ){
//                    jsonObject = JSONObject.parseObject("{}");
//                    jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                    jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                    jsonObject.put("subRegConfKind", "检验内容");
//                    jsonObject.put("value", ((JSONObject)eachResult).getString("test"));
//                    result.add(jsonObject);
//                }
//                if( ((JSONObject)eachResult).get("operation") != null && !((JSONObject)eachResult).getString("operation").isEmpty() && ((List<String>)((JSONObject)eachResult).get("operation")).size() != 0 ){
//
//                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("operation")){
//                        jsonObject = JSONObject.parseObject("{}");
//                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                        jsonObject.put("subRegConfKind", "手术名称");
//                        jsonObject.put("value", eachOperation);
//                        result.add(jsonObject);
//                    }
//                }
//
//                if( ((JSONObject)eachResult).get("medicine") != null && !((JSONObject)eachResult).getString("medicine").isEmpty() ){
//                    jsonObject = JSONObject.parseObject("{}");
//                    jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                    jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                    jsonObject.put("subRegConfKind", "药品名称");
//                    jsonObject.put("value", ((JSONObject)eachResult).getString("medicine"));
//                    result.add(jsonObject);
//                }
//                if( ((JSONObject)eachResult).get("added") != null && !((JSONObject)eachResult).getString("added").isEmpty() ){
//                    jsonObject = JSONObject.parseObject("{}");
//                    jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                    jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                    jsonObject.put("subRegConfKind", "补充说明");
//                    jsonObject.put("value", ((JSONObject)eachResult).getString("added"));
//                    result.add(jsonObject);
//                }
//
//                if( ((JSONObject)eachResult).get("syndrome") != null && !((JSONObject)eachResult).getString("syndrome").isEmpty() ){
//
//                        jsonObject = JSONObject.parseObject("{}");
//                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                        jsonObject.put("subRegConfKind", "并发症");
//                        jsonObject.put("value", ((JSONObject)eachResult).getString("syndrome"));
//                        result.add(jsonObject);
//
//                }
//
//                if( ((JSONObject)eachResult).get("diseasesymptomAfter") != null && !((JSONObject)eachResult).getString("diseasesymptomAfter").isEmpty() && ((List<String>)((JSONObject) eachResult).get("diseasesymptomAfter")).size() !=0){
//                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("diseasesymptomAfter")){
//                        jsonObject = JSONObject.parseObject("{}");
//                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                        jsonObject.put("subRegConfKind", "症状(医疗行为后)");
//                        jsonObject.put("value", eachOperation);
//                        result.add(jsonObject);
//                    }
//                }
//
//                if (((JSONObject) eachResult).get("diseasemAfter") != null && !((JSONObject) eachResult).getString("diseasemAfter").isEmpty() && ((List<String>)((JSONObject) eachResult).get("diseaseAfter")).size() !=0) {
//                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("diseasemAfter")){
//                        jsonObject = JSONObject.parseObject("{}");
//                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                        jsonObject.put("subRegConfKind", "疾病名称(医疗行为后)");
//                        jsonObject.put("value", eachOperation);
//                        result.add(jsonObject);
//                    }
//                }
//
//
//                if (((JSONObject) eachResult).get("defaultBehavior") != null && !((JSONObject) eachResult).getString("defaultBehavior").isEmpty() && ((List<String>)((JSONObject) eachResult).get("defaultBehavior")).size() !=0) {
//                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("defaultBehavior")){
//                        jsonObject = JSONObject.parseObject("{}");
//                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                        jsonObject.put("subRegConfKind", "过失类型");
//                        jsonObject.put("value", eachOperation);
//                        result.add(jsonObject);
//                    }
//                }
//
//                if (((JSONObject) eachResult).get("diseaseBefore") != null && !((JSONObject) eachResult).getString("diseaseBefore").isEmpty() && ((List<String>)((JSONObject) eachResult).get("diseaseBefore")).size() !=0) {
//                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("diseaseBefore")){
//                        jsonObject = JSONObject.parseObject("{}");
//                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                        jsonObject.put("subRegConfKind", "疾病名称(医疗行为前)");
//                        jsonObject.put("value", eachOperation);
//                        result.add(jsonObject);
//                    }
//                }
//
//                if (((JSONObject) eachResult).get("diseasesymptomBefore") != null && !((JSONObject) eachResult).getString("diseasesymptomBefore").isEmpty() && ((List<String>)((JSONObject) eachResult).get("diseasesymptomBefore")).size() !=0 ) {
//                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("diseasesymptomBefore")){
//                        jsonObject = JSONObject.parseObject("{}");
//                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
//                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
//                        jsonObject.put("subRegConfKind", "症状(医疗行为前)");
//                        jsonObject.put("value", eachOperation);
//                        result.add(jsonObject);
//                    }
//                }
//            }
//        }
//        Map<String, Object> map11 = new HashMap<>();
//        result.stream().filter(each->((JSONObject)each).getString("value").isEmpty());
//        map11.put("keyWordList", result);
//        map11.put("brief", disputecaseRepository.getOne(caseId).getBriefCase());
//        return ResultVOUtil.ReturnBack(map11,111,"请求纠纷要素");
//    }


    /** 发送预约数据 */
    @PostMapping(value = "/mediator/appoint")
    public ResultVO setAppoint(@RequestParam("caseId") String caseId,
                               @RequestParam("id") String id,
                               @RequestParam("currentStageContent") String currentStageContent,
                               @RequestParam(value = "application", required=false) MultipartFile application,
                               @RequestParam(value = "applicationDetail", required=false) MultipartFile[] applicationDetail) throws Exception {
        disputecaseAccessoryService.addExportApply(application, applicationDetail, caseId);
        return disputeProgressService.setAppoint(caseId,currentStageContent);
    }
    //管理员选择具体某个调解员调解某个案件 （管理者做决定）  wj
    @PostMapping(value = "/manager/PostMediatorForCase")
    @Transactional
    public ResultVO decideMediatorDisputeCase(@RequestBody Map<String, String> map){

        String mediatorId = map.get("MediatorId");
        String disputeId = map.get("CaseId");
        if(mediatorId==null||mediatorId.trim()=="" ||mediatorId.trim().equals("")){
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.FINDMEDIATOR_FAIL.getCode(),DisputeProgressEnum.FINDMEDIATOR_FAIL.getMsg());
        }
        List<Task> tasks=verifyProcessUtil.verifyTask(disputeId,"管理员做决定");
        Task currentTask=null;
        for(Task task:tasks){
            if(task.getName().equals("管理员做决定")){
                currentTask=task;
                break;
            }
        }
        disputeProgressService.completeCurrentTask(currentTask.getId());
        disputeProgressService.updateCaseStatus(disputeId,"2");
        return disputeProgressService.decideMediatorDisputeCase(mediatorId, disputeId);
    }




    //发送调解失败
    @PostMapping(value = "/mediator/MediationFailure")
    public ResultVO mediationFailure(@RequestBody Map<String, String> map){

        String caseId = map.get("caseId");
//        List<Task> tasks=disputeProgressService.searchCurrentTasks(caseId);
//        Map<String,Object> var=new HashMap<>();
//        var.put("paramMediateResult",)
//        disputeProgressService.completeCurrentTask(tasks.get(0).getId());
        return disputeProgressService.setMediationFailure(caseId);
    }

    //发送调解成功
    @PostMapping(value = "mediator/MediationSuccess")
    public ResultVO caseRepeal(@RequestBody Map<String, String> map){

        String caseId = map.get("caseId");
//        List<Task> tasks=disputeProgressService.searchCurrentTasks(caseId);
        Map<String,Object> var=new HashMap<>();
        var.put("paramMediateResult",0);
//        disputeProgressService.completeCurrentTask(tasks.get(0).getId(),var);
        return disputeProgressService.setCaseSuccess(caseId);
    }

    /** 申请再次调解*/
    @PostMapping(value = "/mediator/reMediation")
    public ResultVO reMediation(@RequestBody Map<String, String> map){
        String caseId = map.get("caseId");

        return disputeProgressService.reMediation(caseId);
    }

    /** 通知用户进行医疗鉴定 */
    @PostMapping(value = "/mediator/InformIndenty")
    public  ResultVO informIndenty(@RequestBody Map<String, String> map){
        String caseId = map.get("caseId");
        return disputeProgressService.informIndenty(caseId);
    }

    /** 获取专家库 */
    @GetMapping(value = "/getExpertsList")
    public ResultVO getExpertsList(){
        return disputeProgressService.getExpertsList();
    }

    /** 获取调解员权限 */
    @PostMapping("/Mediator/getAuthority")
    public ResultVO getAuthority(@RequestBody Map<String,String> map){
        String id=map.get("id");
        return disputeProgressService.getAuthority(id);
    }

    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;




    // 提交告知书确认函
    @PostMapping(value = "/uploadNotificationAffirm")
    public ResultVO uploadNotificationAffirm(@RequestParam(value = "file", required=false) MultipartFile multipartFile,
                                             @RequestParam("caseId") String disputeId) throws IOException {

        return disputecaseAccessoryService.addNotificationAffirm(multipartFile, disputeId);
    }

    // 提交代理人委托书
    @PostMapping(value = "/uploadProxyCertification")
    public ResultVO uploadProxyCertification(@RequestParam(value = "file", required=false) MultipartFile multipartFile,
                                             @RequestParam("caseId") String disputeId) throws IOException {

        return disputecaseAccessoryService.addProxyCertification(multipartFile, disputeId);
    }


    /** 管理者获取该案件 的用户意向调解员 */
    public ResultVO getUserChoose(@RequestBody JSONObject map){
        return disputeProgressService.getUserChooseMediator(map);
    }

    /** 管理者获取该案件的 另外分配：剔除 用户意向和申请回避 */
    @PostMapping("/manager/getMediatorList")
    public ResultVO additionalAllocation(@RequestBody JSONObject map){
        log.info("\n开始 [另外分配]\n");
        String caseId=map.getString("caseId");
        Integer page=map.getInteger("page")-1;
        Integer size=map.getInteger("size");

        PageRequest pageRequest=new PageRequest(page,size);
        log.info("\n结束 [另外分配]\n");
        return disputeProgressService.getAdditionalAllocation(caseId,pageRequest);
    }

    /** 下拉框：管理员页面获取所有调解员(不分页) */
    @GetMapping("/manager/getAllMediator")
    public ResultVO getAllMediator(){
        return disputeProgressService.getAllMediator();
    }

//    // 调解前预约 调解员判断 是否进行 专家预约，调解员线下确认时间
//    @PostMapping("/mediator/appointBeforeMediate")
//    public ResultVO appointBeforeMediate(Map<String, Object> map){
//        String disputeId = (String)map.get("caseId");
//        Date mediteTime = (Date)map.get("mediteTime");
//        String mediationPlace =(String)map.get("mediationPlace");
//        //String appointResult = (String)map.get("appointResult");
//
//        String ms=disputecaseProcessRepository.findByDisputecaseId(disputeId).getMediateStage();
//        JSONObject mediateStage=JSONObject.parseObject(ms);
//        Integer stage=mediateStage.getInteger("stage");
//        JSONObject currentStageContent=mediateStage.getJSONArray("stageContent").getJSONObject(stage-1);
//        currentStageContent.getJSONObject("particopateContact").put("mediationTime", mediteTime);
//        currentStageContent.getJSONObject("particopateContact").put("mediationPlace", mediationPlace);
//
//        Map<String, Object> var = new HashMap<>();
//        var.put("disputeId", disputeId);
//        var.put("appointResult", (String)map.get("appointResult"));
//
//        String pid=disputecaseActivitiRepository.getOne(disputeId).getProcessId();
//        Task currentTask=disputeProgressService.searchCurrentTasks(disputeId).get(0);
//
//        disputeProgressService.completeCurrentTask(currentTask.getId(), var);
//        return null;
//    }

//    //专家预约审核前，用户上传申请材料查看
//    @PostMapping("/exportAppointApplication")
//    public  ResultVO exportAppointApplication(Map<String, String> map) {
//
//        String disputeId = map.get("caseId");
//        return ResultVOUtil.ReturnBack(disputecaseAccessoryRepository.findByDisputecaseId(disputeId).getAppointExpert(),112, "获取用户 专家申请资料。");
//    }

    @Autowired
    private DisputecaseActivitiRepository disputecaseActivitiRepository;

    @Autowired
    private RuntimeService runtimeService;

    //专家预约审核
    @PostMapping("/exportAppointCheck")
    public ResultVO ExportAppointCheck(@RequestBody Map<String, String> map) throws Exception{
        String disputeId = map.get("caseId");

        String exportAppointCheck = map.get("profesorVerify");//意见， 同意或不同意1/0
        {
            // 修改process表状态
            DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputeId);
            if(Integer.parseInt(exportAppointCheck.trim())==1)
                disputecaseProcess.setParamProfessor("1");
            else if(Integer.parseInt(exportAppointCheck.trim())==0)
                disputecaseProcess.setParamProfessor("2");
            else
                throw new Exception("专家申请审核对process表paramProfessor字段修改出错！");
            disputecaseProcessRepository.save(disputecaseProcess);
        }
        Map<String, Object> var = new HashMap<>();
        var.put("profesorVerify", exportAppointCheck);
        String pid=disputecaseActivitiRepository.getOne(disputeId).getProcessId();
        if(exportAppointCheck.equals("1"))
            runtimeService.setVariable(pid,"paramProfesor",1);
        if(exportAppointCheck.equals("0"))
            runtimeService.setVariable(pid,"paramProfesor",2);
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(disputeId);
        disputecaseProcess.setIsSuspended(0);
        disputecaseProcessRepository.save(disputecaseProcess);

        Task currentTask=disputeProgressService.searchCurrentTasks(disputeId).get(0);
        disputeProgressService.completeCurrentTask(currentTask.getId(), var);

        return ResultVOUtil.ReturnBack(map,112,"审核完成");
    }

    // 发送调解文件，完成调解结果处理
    @PostMapping("/completeCurrentMediate")
    public ResultVO completeCurrentMediate(@RequestParam(value = "file", required=false) MultipartFile[] multipartFiles,
                                           @RequestParam("caseId") String disputeID){

        return null;
    }


    /** 问询医院时获取医院信息 */
    @PostMapping(value = "/getHospitalMessage")
    public ResultVO getHospitalMessage(@RequestBody JSONObject map){
        String caseId=map.getString("caseId");
        return disputeProgressService.getHospitalMessage(caseId);
    }

    /** 提交问讯结果（文本），问讯医院 */
    @PostMapping(value = "/inqueryHospital")
    public ResultVO inqueryHospital(@RequestBody JSONObject map){
        return disputeProgressService.inqueryHospital(map);
    }


//    // 问询医院
//    @PostMapping(value = "/inqueryHospital")
//    public ResultVO inqueryHospital(@RequestParam(value = "file", required=false) MultipartFile[] multipartFiles,
//                                    @RequestParam("text") String text,
//                                    @RequestParam("caseId") String disputeID,
//                                    @RequestParam("isFinisihed") String isFinished) throws IOException {
//
//        return disputecaseAccessoryService.addInquireHospital(multipartFiles, text, disputeID, isFinished);
//    }




    /** 获取问讯列表 */
    @PostMapping(value = "/getInqueryHospitalList")
    public ResultVO getInqueryHospitalList(@RequestBody JSONObject map){
        String caseId=map.getString("caseId");
        return disputeProgressService.getInqueryHospitalList(caseId);
    }



    /** 管理员获取专家管理界面数据 */
    @PostMapping("/manager/getExpertManageList")
    public ResultVO getExpertManageList(@RequestBody JSONObject map) throws Exception{
        int size=map.getInteger("size");
        int page=map.getInteger("page")-1;
        PageRequest pageRequest=new PageRequest(page,size);
        String filterStatus=map.getString("filterStatus");
        Integer status = 0;
        if(filterStatus.equals("0"))
            status = 0;

        if(filterStatus.equals("1"))
            status = 1;

        if(filterStatus.equals("2"))
            status = 2;

        if(filterStatus.equals(""))
            status = 3;
        return disputeProgressService.getExpertManageList(pageRequest,status);
    }




    //发送医疗过程信息，获取纠纷要素列表
    @PostMapping(value = "/mediator/getKeyWordList")
    public ResultVO getKeyWordList(HttpServletRequest request){
        JSONObject map= Request2JSONobjUtil.convert(request);
        String caseId = map.getString("caseId");//操作人id
        //String stageContent=map.getString("stageContent");
        String stageContent=disputecaseRepository.findOne(caseId).getMedicalProcess();

        JSONArray jsonArray = JSONArray.parseArray(stageContent);

        JSONArray result = JSONArray.parseArray("[]");


        for(Object stage : jsonArray){


            //获取resultOfRegConflict列表
            JSONArray resultOfRegConflict = ((JSONObject) stage).getJSONArray("resultOfRegConflict");
            for(Object eachResult: resultOfRegConflict){
                JSONObject jsonObject = JSONObject.parseObject("{}");
                if(
                        ((JSONObject)eachResult).getString("test").isEmpty()


                                && ((List<String>)((JSONObject)eachResult).get("operation")).size() == 0

                                && ((JSONObject)eachResult).getString("medicine").isEmpty()

                                && ((JSONObject)eachResult).getString("added").isEmpty()

                                && ((JSONObject)eachResult).getString("syndrome").isEmpty()


                                && ((List<String>)((JSONObject) eachResult).get("diseasesymptomAfter")).size() ==0


                                && ((List<String>)((JSONObject) eachResult).get("diseaseAfter")).size() ==0

                                && ((List<String>)((JSONObject) eachResult).get("defaultBehavior")).size() ==0

                                && ((List<String>)((JSONObject) eachResult).get("diseaseBefore")).size() ==0

                                && ((List<String>)((JSONObject) eachResult).get("diseasesymptomBefore")).size() ==0)

                    continue;
                if( ((JSONObject)eachResult).get("test") != null && !((JSONObject)eachResult).getString("test").isEmpty() ){
                    jsonObject = JSONObject.parseObject("{}");
                    jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                    jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                    jsonObject.put("subRegConfKind", "检验内容");
                    jsonObject.put("value", ((JSONObject)eachResult).getString("test"));
                    result.add(jsonObject);
                }
                if( ((JSONObject)eachResult).get("operation") != null && !((JSONObject)eachResult).getString("operation").isEmpty() && ((List<String>)((JSONObject)eachResult).get("operation")).size() != 0 ){

                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("operation")){
                        jsonObject = JSONObject.parseObject("{}");
                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                        jsonObject.put("subRegConfKind", "手术名称");
                        jsonObject.put("value", eachOperation);
                        result.add(jsonObject);
                    }
                }

                if( ((JSONObject)eachResult).get("medicine") != null && !((JSONObject)eachResult).getString("medicine").isEmpty() ){
                    jsonObject = JSONObject.parseObject("{}");
                    jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                    jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                    jsonObject.put("subRegConfKind", "药品名称");
                    jsonObject.put("value", ((JSONObject)eachResult).getString("medicine"));
                    result.add(jsonObject);
                }
                if( ((JSONObject)eachResult).get("added") != null && !((JSONObject)eachResult).getString("added").isEmpty() ){
                    jsonObject = JSONObject.parseObject("{}");
                    jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                    jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                    jsonObject.put("subRegConfKind", "补充说明");
                    jsonObject.put("value", ((JSONObject)eachResult).getString("added"));
                    result.add(jsonObject);
                }

                if( ((JSONObject)eachResult).get("syndrome") != null && !((JSONObject)eachResult).getString("syndrome").isEmpty() ){

                        jsonObject = JSONObject.parseObject("{}");
                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                        jsonObject.put("subRegConfKind", "并发症");
                        jsonObject.put("value", ((JSONObject)eachResult).getString("syndrome"));
                        result.add(jsonObject);

                }

                if( ((JSONObject)eachResult).get("diseasesymptomAfter") != null && !((JSONObject)eachResult).getString("diseasesymptomAfter").isEmpty() && ((List<String>)((JSONObject) eachResult).get("diseasesymptomAfter")).size() !=0){
                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("diseasesymptomAfter")){
                        jsonObject = JSONObject.parseObject("{}");
                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                        jsonObject.put("subRegConfKind", "症状(医疗行为后)");
                        jsonObject.put("value", eachOperation);
                        result.add(jsonObject);
                    }
                }

                if (((JSONObject) eachResult).get("diseaseAfter") != null && !((JSONObject) eachResult).getString("diseaseAfter").isEmpty() && ((List<String>)((JSONObject) eachResult).get("diseaseAfter")).size() !=0) {
                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("diseaseAfter")){
                        jsonObject = JSONObject.parseObject("{}");
                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                        jsonObject.put("subRegConfKind", "疾病名称(医疗行为后)");
                        jsonObject.put("value", eachOperation);
                        result.add(jsonObject);
                    }
                }


                if (((JSONObject) eachResult).get("defaultBehavior") != null && !((JSONObject) eachResult).getString("defaultBehavior").isEmpty() && ((List<String>)((JSONObject) eachResult).get("defaultBehavior")).size() !=0) {
                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("defaultBehavior")){
                        jsonObject = JSONObject.parseObject("{}");
                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                        jsonObject.put("subRegConfKind", "过失类型");
                        jsonObject.put("value", eachOperation);
                        result.add(jsonObject);
                    }
                }

                if (((JSONObject) eachResult).get("diseaseBefore") != null && !((JSONObject) eachResult).getString("diseaseBefore").isEmpty() && ((List<String>)((JSONObject) eachResult).get("diseaseBefore")).size() !=0) {
                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("diseaseBefore")){
                        jsonObject = JSONObject.parseObject("{}");
                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                        jsonObject.put("subRegConfKind", "疾病名称(医疗行为前)");
                        jsonObject.put("value", eachOperation);
                        result.add(jsonObject);
                    }
                }

                if (((JSONObject) eachResult).get("diseasesymptomBefore") != null && !((JSONObject) eachResult).getString("diseasesymptomBefore").isEmpty() && ((List<String>)((JSONObject) eachResult).get("diseasesymptomBefore")).size() !=0 ) {
                    for(String eachOperation: (List<String>)((JSONObject)eachResult).get("diseasesymptomBefore")){
                        jsonObject = JSONObject.parseObject("{}");
                        jsonObject.put("stageName", ((JSONObject) stage).getString("name"));
                        jsonObject.put("regConfKind", ((JSONObject)eachResult).getString("name"));
                        jsonObject.put("subRegConfKind", "症状(医疗行为前)");
                        jsonObject.put("value", eachOperation);
                        result.add(jsonObject);
                    }
                }
            }
        }

        Map<String, Object> map11 = new HashMap<>();
        result.stream().filter(each->((JSONObject)each).getString("value").isEmpty());
        map11.put("keyWordList", result);
        List<String> people = new ArrayList<>();
        people.add("");
        map11.put("brief", (GetTitleAndAbstract.generateCaseTitleDetail(stageContent,people)).get("detail"));
        return ResultVOUtil.ReturnBack(map11,111,"请求纠纷要素");
    }

    //详情中查看用户登记信息 以及 相关案例推荐
    @PostMapping(value = "/caseDetail")
    public ResultVO getcaseDetail(@RequestBody Map<String, String> map){

        String caseId = map.get("caseId");

        return disputeProgressService.getcaseDetail(caseId);
    }



    @Autowired
    private KeyWordsSearchService keyWordsSearchService;

    //类案详情页面的收藏
    @PostMapping(value = "/caseCollect")
    public ResultVO caseCollect(@RequestBody Map<String, String> map){
        String caseId = map.get("caseId");
        String caseName = map.get("caseName");
        String type = map.get("type");

        Disputecase disputecase = disputecaseRepository.findOne(caseId);
        JSONObject recommendedPaper = JSONObject.parseObject(disputecase.getRecommendedPaper());
        JSONArray dissension = recommendedPaper.getJSONArray(type);
        keyWordsSearchService.getCaseDetails(caseName,type,caseId).get("result");


        return ResultVOUtil.ReturnBack(123,"类案详情收藏成功");
    }

    //类案详情页面的收藏
    @PostMapping(value = "/cancelCaseCollect")
    public ResultVO cancelCaseCollect(@RequestBody Map<String, String> map){
        String caseId = map.get("caseId");
        String caseName = map.get("caseName");
        String type = map.get("type");

        Disputecase disputecase = disputecaseRepository.findOne(caseId);

        if (!disputecase.getRecommendedPaper().isEmpty()){
            JSONObject recommendedPaper = JSONObject.parseObject(disputecase.getRecommendedPaper());
            JSONArray dissension = recommendedPaper.getJSONArray("dissension");
            JSONArray dissension_dx = recommendedPaper.getJSONArray("dissension_dx");
            JSONArray dissension_ms = recommendedPaper.getJSONArray("dissension_ms");

            for(int i = 0; i< dissension.size();i++){
                if(dissension.getJSONObject(i).getString("disputeName").equals(caseName)){
                    dissension.remove(i);
                }
            }
            for(int i = 0; i< dissension_dx.size();i++){
                if(dissension_dx.getJSONObject(i).getString("disputeName").equals(caseName)){
                    dissension_dx.remove(i);
                }
            }
            for(int i = 0; i< dissension_ms.size();i++){
                if(dissension_ms.getJSONObject(i).getString("disputeName").equals(caseName)){
                    dissension_ms.remove(i);
                }
            }
        }



        return ResultVOUtil.ReturnBack(123,"类案详情取消收藏成功");
    }

    //2.审批 撤销申请
    @PostMapping(value = "/feedBackCaseCancelApply")
    public ResultVO feedBackCaseCancelApply(@RequestBody JSONObject obj){
        String caseId = obj.getString("caseId");
        String id = obj.getString("id");
        String repealApplyReason =obj.getString("repealApplyReason");//todo:db保存
        String repealApplyResult = obj.getString("repealApplyResult");

        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        if(repealApplyResult.equals("0"))
            disputecaseProcess.setStatus("0");
        if(repealApplyResult.equals("1"))
            disputecaseProcess.setStatus("6");
        disputecaseProcessRepository.save(disputecaseProcess);
        return ResultVOUtil.ReturnBack(123, ".审批 撤销申请");
    }

    //3.审批撤销调解\
    @PostMapping(value = "/feedBackCaseCancelMediate")
    public ResultVO feedBackCaseCancelMediate(@RequestBody JSONObject obj){
        String caseId = obj.getString("caseId");
        String id = obj.getString("id");
        String repealApplyReason =obj.getString("repealMediationReason");//todo:db保存
        String repealApplyResult = obj.getString("repealMediationResult");

        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        if(repealApplyResult.equals("0"))
            disputecaseProcess.setStatus("2");
        if(repealApplyResult.equals("1"))
            disputecaseProcess.setStatus("7");
        disputecaseProcessRepository.save(disputecaseProcess);
        return ResultVOUtil.ReturnBack(123, ".审批 撤销调解");
    }

    //4.审批更换调解员
    @PostMapping(value = "/changeMediatorResult")
    public ResultVO feedBackChangeMediator(@RequestBody JSONObject obj){
        String caseId = obj.getString("caseId");
        String id = obj.getString("id");
        String changeMediatorReason =obj.getString("changeMediatorReason");//todo:db保存
        String changeMediatorResult = obj.getString("changeMediatorResult");
        String mediatorId = obj.getString("mediatorId");

        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        Disputecase disputecase = disputecaseRepository.findOne(caseId);

        if(changeMediatorResult.equals("0"))
            disputecaseProcess.setStatus("2");
        if(changeMediatorResult.equals("1")){
            disputecase.setMediatorId(mediatorId);
            disputecaseProcess.setStatus("2");
        }
        disputecaseRepository.save(disputecase);
        disputecaseProcessRepository.save(disputecaseProcess);
        return ResultVOUtil.ReturnBack(123, ".审批 撤销申请");
    }

    //5.生成协议
    @PostMapping(value = "/protocal")
    public ResultVO protocal(@RequestParam(value = "id") String id,
                              @RequestParam(value = "caseId") String caseId,
                              @RequestParam(value = "protocalFile") MultipartFile multipartFile) throws IOException {

        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String url = disputecaseAccessoryService.uploadFile(inputStream, multipartFile.getOriginalFilename());
        DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        disputecaseAccessory.setProtocal(url);
        disputecaseAccessoryRepository.save(disputecaseAccessory);
        return ResultVOUtil.ReturnBack(123, "协议上传");
    }

    //6.司法确认
    @PostMapping(value = "/judicialConfirm")
    public ResultVO judicialConfirm(@RequestParam(value = "id") String id,
                                    @RequestParam(value = "caseId") String caseId,
                                    @RequestParam(value = "judicialConfirmText") String judicialConfirmText,
                                    @RequestParam(value = "judicialConfirmFile") MultipartFile multipartFile) throws IOException {
        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String url = disputecaseAccessoryService.uploadFile(inputStream, multipartFile.getOriginalFilename());
        DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        JSONObject result = JSONObject.parseObject("{}");
        result.put("judicialConfirmText", judicialConfirmText);
        result.put("judicialConfirmFile", url);
        disputecaseAccessory.setJudicialConfirm(result.toJSONString());
        disputecaseAccessoryRepository.save(disputecaseAccessory);

        return ResultVOUtil.ReturnBack(123, "司法确认上传");
    }

    //7.立案审批添加参数

    //调解员评判
    @PostMapping(value = "/postEvaluateOfMediator")
    public ResultVO postEvaluateOfMediator(@RequestBody JSONObject object){
        String caseId = object.getString("caseId");
        JSONObject ModeratorRegister = object.getJSONObject("moderatorRegister");
        Disputecase disputecase = disputecaseRepository.findOne(caseId);
        disputecase.setModeratorRegister(ModeratorRegister.toJSONString());
        disputecaseRepository.save(disputecase);
        return ResultVOUtil.ReturnBack(123,"调解员评判成功");
    }

}
