package com.seu.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.DisputecaseAccessory;
import com.seu.domian.DisputecaseProcess;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.ChangeAuthorityForm;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import com.seu.form.VOForm.NormalUserUploadListForm;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.repository.DisputecaseProcessRepository;
import com.seu.service.*;
import com.seu.utils.DisputeProcessReturnMap;
import com.seu.utils.VerifyProcessUtil;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/DisputeWeb")
@CrossOrigin
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
    // TODO 流程测试
    // TODO 完成以下todo后删除
    // TODO 加入上传 受理、不予立案的通知书 并发送给用户(申请人和代理人)
    // TODO 向数据库process表中添加纠纷开始时间，并计算30个工作日后的结束时间(已完成)
    @PostMapping(value="/DisposeApply")
    @Transactional
    public ResultVO caseAccept(@RequestBody JSONObject map) throws Exception {
        Integer result=map.getInteger("result");
        String disputeId=map.getString("caseId");
        String ID=map.getString("id");
        String name=userService.findNameById(ID);

//        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
        List<Task> tasks=verifyProcessUtil.verifyTask(disputeId,"立案判断");
        Map<String,Object> var=new HashMap<>();
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputeId);
        if(result==0){
            var.put("caseAccept",0);  //不通过
            disputecaseProcess.setStatus("8");
        }

        else{
            var.put("caseAccept",1);  //通过
            disputecaseProcess.setStatus("1");
            /** 向数据库process表中添加纠纷开始时间，并计算30个工作日后的结束时间 */
            disputeProgressService.setStartTimeAndEndTime(disputeId);
        }

        disputeProgressService.completeCurrentTask(tasks.get(0).getId(),var);


        disputecaseProcessRepository.save(disputecaseProcess);
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

        String disputeId = map.get("CaseId");
        String ID = map.get("id");

        /** 为案件进程表添加调解员回避状态*/
        disputeProgressService.updateAvoidStatus(disputeId,ID);
        return ResultVOUtil.ReturnBack(DisputeProgressEnum.MEDIATORAVOID_SUCCESS.getCode(),DisputeProgressEnum.MEDIATORAVOID_SUCCESS.getMsg());
    }


    /** 用户选择调解员列表 */
    @PostMapping(value = "user/postMediatorList")
    public ResultVO getUserChoose(@RequestBody Map<String, String > map){

        String pickedList = map.get("MediatorPickedlist");
        String disputeId = map.get("CaseId");

        String mediatorList="";
        pickedList=pickedList.substring(1,pickedList.length()-1);
        for(String s:pickedList.split(",")){
            mediatorList+=(s.substring(1,s.length()-1))+",";
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
    public ResultVO getMyMediationData(@RequestBody JSONObject map){

        return disputeProgressService.getMyMediationData(map);
    }

    /** 管理者获取案件列表 */
    @PostMapping(value = "/manager/getCaseList")
    public ResultVO getManagerCaseList(@RequestBody JSONObject map) throws Exception{
        return disputeProgressService.getManagerCaseList(map);
    }


    /** 管理员获取统计管理页面列表 */
    @PostMapping(value = "/manager/getCase_judiciary")
    public ResultVO getManagerCaseJudiciary(@RequestBody Map<String, String> map){
//
        String id = map.get("id");

        return disputeProgressService.getManagerCaseJudiciary(id);
    }

    /** 管理员获取所有的调解员列表(分页) */
    @PostMapping(value = "/manager/getMediatorListWithPage")
    public ResultVO getMediatorListWithPage(@RequestBody JSONObject map){

        String caseId=map.getString("caseId");
        Integer page=map.getInteger("page")-1;  // 前端从一开始，后台从0开始
        Integer size=map.getInteger("size");
        PageRequest pageRequest=new PageRequest(page,size);
        return disputeProgressService.getMediatorList(caseId,pageRequest);
    }

    /** 管理员 获取所有调解员的授权信息 */
    @PostMapping(value = "/manager/getNameofAuthorityList")
    public ResultVO getNameofAuthorityList(@RequestBody JSONObject map){

        String id = map.getString("id");
        Integer page=map.getInteger("page")-1;  // 前端从一开始，后台从0开始
        Integer size=map.getInteger("size");
        PageRequest pageRequest=new PageRequest(page,size);

        return disputeProgressService.getNameofAuthorityList(id,pageRequest);
    }

    /**管理员发送授权调解员权限信息 */
    @PostMapping(value = "/manager/ChangeAuthorityNameList")
    public ResultVO changeAuthorityNameList(@RequestBody Map<String, String> map){

        String changeAuthorityFormList = map.get("param");
        return disputeProgressService.changeAuthorityNameList(changeAuthorityFormList);
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

    /** 发送医疗/损害鉴定结果数据 */
    @PostMapping(value = "/mediator/resultOfIndent")
    public ResultVO setResultOfIndent(@RequestBody Map<String, String> map){

        String caseId = map.get("CaseId");
        String resultOfIndent = map.get("resultOfIndent");
        //TODO 发送文件未做 wj
        return disputeProgressService.setResultOfIndent(caseId,resultOfIndent);
    }


    // TODO 如果有专家预约，要怎么发送数据来保证格式统一
    /** 发送预约数据 */
    @PostMapping(value = "/mediator/appoint")
    public ResultVO setAppoint(@RequestBody Map<String, String> map){
        String caseId = map.get("caseId");
        String currentStageContent = map.get("currentStageContent");
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


//    // TODO 待修改，完成后删除该todo
//    //发送问询医院数据
//    @PostMapping(value = "/detail/InquireInstitute")
//    @Transactional
//    public ResultVO inquireInstitute(@RequestBody Map<String, String> map){
//        // TODO 记得使用List<Task> tasks=verifyProcessUtil.verifyTask(disputeId,"问询医院");来确认当前任务是否是问询医院，否的话会自动返回异常
//        // TODO 问询医院的流程参数为 paramInquireHospital 0/1(Integer)
//        // TODO 问询医院每次进来都查找下task，上传保存资料，设置流程参数后完成当前任务
//
//        String caseId = map.get("caseId");
//        String inquireText = map.get("inquireText");
//
//        return disputecaseAccessoryService.addInquireHospital(caseId, inquireText);
//    }

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
    @PostMapping(value = "/user/reMediation")
    public ResultVO reMediation(@RequestBody Map<String, String> map){
        String caseId = map.get("CaseId");

        return disputeProgressService.reMediation(caseId);
    }

    /** 通知用户进行医疗鉴定 */
    @PostMapping(value = "/mediator/InformIndenty")
    public  ResultVO informIndenty(@RequestBody Map<String, String> map){
        String caseId = map.get("CaseId");
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

    // 闻讯医院
    @PostMapping(value = "/inqueryHospital")
    public ResultVO inqueryHospital(@RequestParam(value = "file", required=false) MultipartFile[] multipartFiles,
                                    @RequestParam("text") String text,
                                    @RequestParam("caseId") String disputeID,
                                    @RequestParam("isFinisihed") String isFinished) throws IOException {

        return disputecaseAccessoryService.addInquireHospital(multipartFiles, text, disputeID, isFinished);
    }

    // 获取闻讯医院结果
    @PostMapping(value = "/getInqueryHospitalList")
    public ResultVO getInqueryHospitalList(@RequestBody Map<String, String> map){

        String disputeId = map.get("caseId");

        ;

        return ResultVOUtil.ReturnBack(JSONArray.parseArray(disputecaseAccessoryRepository.findByDisputecaseId(disputeId).getInquireHospital()), 112, "获取闻讯列表成功");
    }

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
        String caseId=map.getString("caseId");
        Integer page=map.getInteger("page")-1;
        Integer size=map.getInteger("size");

        PageRequest pageRequest=new PageRequest(page,size);
        return disputeProgressService.getAdditionalAllocation(caseId,pageRequest);
    }

    /** 下拉框：管理员页面获取所有调解员(不分页) */
    @GetMapping("/manager/getAllMediator")
    public ResultVO getAllMediator(){
        return disputeProgressService.getAllMediator();
    }
    //提交专家申请书
    public ResultVO uploadExportApply(@RequestParam(value = "application", required=false) MultipartFile application,
                                             @RequestParam(value = "applicationDetail", required=false) MultipartFile[] applicationDetail,
                                             @RequestParam("caseId") String disputeId) throws IOException {

        return disputecaseAccessoryService.addExportApply(application, applicationDetail, disputeId);
    }

    //
}
