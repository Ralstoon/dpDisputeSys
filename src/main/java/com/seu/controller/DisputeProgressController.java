package com.seu.controller;


import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.ChangeAuthorityForm;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.DisputecaseRepository;
import com.seu.service.*;
import com.seu.utils.DisputeProcessReturnMap;
import javafx.geometry.Pos;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    /*
     *@Author 吴宇航
     *@Description  纠纷登记功能，由当前用户操作;
     *@Date 22:05 2018/7/20
     *@Param [disputeRegisterDetailForm, bindingResult, session]
     *@return com.seu.ViewObject.ResultVO
     **/
    // TODO 存在问题，即用户必须在进入纠纷登记页面后完成整个操作，否则不会做任何保存
    // TODO 问题2，未对用的身份信息进行认证（姓名、身份证）
    // TODO 大改，卸载登记controller中
//    @PostMapping(value="/disputeRegister")
//    @Transactional
//    public ResultVO disputeRegister(@Valid DisputeRegisterDetailForm disputeRegisterDetailForm,
//                                    BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            // bindingResult.getFieldError().getDefaultMessage()表示返回验证失败的地方，比如XX必填
//            Map<String,Object> param=new HashMap<>();
//            param.put("data",bindingResult.getFieldError().getDefaultMessage());
//            return ResultVOUtil.ReturnBack(param,DisputeProgressEnum.DISPUTEREGISTER_FAIL.getCode(),DisputeProgressEnum.DISPUTEREGISTER_FAIL.getMsg());
//        }
//        //1、存储纠纷信息
////        String userId=((NormalUser)session.getAttribute("currentUser")).getUserId();
//        String userId=disputeRegisterDetailForm.getID();
//        disputeRegisterDetailForm=new DisputeRegisterDetailForm(); //测试用，到时候删去
//        String disputeId=disputeProgressService.saveDisputeInfo(disputeRegisterDetailForm,userId);
//        System.out.println("disputeId："+disputeId);
//        String name= normalUserService.findNormalUserNameByFatherId(userId);
//        String email= normalUserService.findEmailByUserId(userId);
//        String phone = normalUserService.findPhoneByUserId(userId);
//
//
////        session.setAttribute("name",name);
//        Map<String,Object> vars=new HashMap<>();
//        vars.put("disputeId",disputeId);
//        vars.put("userId",userId);
//        vars.put("email",email);
//
//        vars.put("phone",phone);
//
//        //2、用纠纷信息id启动流程
//        disputeProgressService.startProcess(disputeId,vars);
//
//        //3、完成纠纷登记操作
//        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
//        disputeProgressService.completeCurrentTask(tasks.get(0).getId());
//        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),name);
//        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.DISPUTEREGISTER_SUCCESS.getCode(),DisputeProgressEnum.DISPUTEREGISTER_SUCCESS.getMsg());
//
//    }

    /*
     *@Author 吴宇航
     *@Description  //暂存确认功能，建立在调解员选定具体案例并且确认
     *@Date 21:07 2018/7/20
     *@Param [starterId, session] 假设前端传过来要确认的纠纷案例id或者当事人id，目前只写了当事人id
     *@return com.seu.ViewObject.ResultVO
     **/
    //TODO 假设是调解员登录
    @PostMapping(value="/temporaryConfirm")
    public ResultVO temporaryConfirm(@RequestParam("disputeId") String disputeId,
                                     @RequestParam("ID") String ID){
        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
        disputeProgressService.completeCurrentTask(tasks.get(0).getId());
        String name=mediatorService.findNameByID(ID);
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),name);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getCode(),DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getMsg());

    }

    /*
     *@Author 吴宇航
     *@Description  立案判断功能，管理者或有权限的调解员选定具体案例并且审核
     *@Date 21:11 2018/7/20
     *@Param [result通过与否, starterId当前审核案件用户id, session]
     *@return com.seu.ViewObject.ResultVO
     **/
    // TODO 流程测试
    @PostMapping(value="/PassApply")
    public ResultVO caseAccept(@RequestParam(value="result") Integer result,
                               @RequestParam("CaseId") String disputeId,
                               @RequestParam("id") String ID){
        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
        Map<String,Object> var=new HashMap<>();
        if(result==0)
            var.put("caseAccept",0);  //不通过
        else
            var.put("caseAccept",1);  //通过
        disputeProgressService.completeCurrentTask(tasks.get(0).getId(),var);
        String name=userService.findNameById(ID);
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),name);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getCode(),DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getMsg());
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



    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private DisputecaseRepository disputecaseRepository;


    /**
     * @Author: W
     * @Description: 查询待办任务，根据 参数 task
     * @Date: 18:08 2018/7/21
     * @param
     * @param size
     */
//    @PostMapping(value = "/taskList")
//    public ResultVO testlist(@RequestParam(value = "page",defaultValue = "1") Integer page,
//                             @RequestParam(value = "size",defaultValue = "10") Integer size,
//                             @RequestParam("task") String task){
//        //todo 身份认证
//        List<DisputeCaseForm> disputeCaseFormList=disputeProgressService.getDisputeListByTask(task,page-1,size);
//        Map<String,Object> map=new HashMap<>();
//        map.put("disputeCaseList",disputeCaseFormList);
//        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.SEARCH_TASK_SUCCESS.getCode(),DisputeProgressEnum.SEARCH_TASK_SUCCESS.getMsg());
//    }

    @PostMapping(value = "/historicTaskList")
    public ResultVO getHistoricTaskListByDispute(@RequestParam(value = "disputeId") String disputeId){
        List<HistoricTaskForm> historicTaskFormList = disputeProgressService
                .getHistoricTaskListByDisputeId(disputeId);
        Map<String,Object> map=new HashMap<>();
        map.put("historicTaskFormList",historicTaskFormList);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.SEARCH_HISTORICTASKLIST_SUCESS.getCode(),DisputeProgressEnum.SEARCH_HISTORICTASKLIST_SUCESS.getMsg());
    }

    // TODO 流程测试
    /*
     *@Author 吴宇航
     *@Description  //调解员确定用户(纠纷案件) 完成”调解员选用户“任务
     *@Date 17:01 2018/7/24
     *@Param [disputeId, session]
     *@return com.seu.ViewObject.ResultVO
     **/
    @PostMapping(value = "/mediator/forhandler")
    public ResultVO mediatorSelectUser(@RequestParam("CaseId") String disputeId,
                                       @RequestParam("id") String ID){
        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
        Task currentTask=null;
        for(Task task:tasks){
            if(task.getName().equals("调解员选用户")){
                currentTask=task;
                break;
            }
        }
        disputeProgressService.completeCurrentTask(currentTask.getId());
        /** 为案件进程表添加调解员申请状态*/
        disputeProgressService.updateApplyStatus(disputeId,ID);
        return ResultVOUtil.ReturnBack(DisputeProgressEnum.MEDIATORSELECTCASE_SUCCESS.getCode(),DisputeProgressEnum.MEDIATORSELECTCASE_SUCCESS.getMsg());
    }

    /** 调解员申请回避 */
    @PostMapping(value = "/mediator/fordebarb")
    public ResultVO mediatorFordebarb(@RequestParam("CaseId") String disputeId,
                                       @RequestParam("id") String ID){

        /** 为案件进程表添加调解员回避状态*/
        disputeProgressService.updateAvoidStatus(disputeId,ID);
        return ResultVOUtil.ReturnBack(DisputeProgressEnum.MEDIATORAVOID_SUCCESS.getCode(),DisputeProgressEnum.MEDIATORAVOID_SUCCESS.getMsg());
    }


    /** 用户选择调解员列表 */
    @PostMapping(value = "user/postMediatorList")
    public ResultVO getUserChoose(@RequestParam("MediatorPickedlist") String pickedList,
                                  @RequestParam("CaseId") String disputeId){
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
    @GetMapping(value = "/mediator/GetMediationHallData")
    public ResultVO getMediationHallData(@RequestParam("id") String id){
        return disputeProgressService.getMediationHallData(id);
    }

    /** 获取我的调节中的数据 */
    @GetMapping(value = "/mediator/GetMyMediationData")
    public ResultVO getMyMediationData(@RequestParam("id") String id){
        return disputeProgressService.getMyMediationData(id);
    }

    /** 管理者获取案件列表 */
    @GetMapping(value = "/manager/getCaseList")
    public ResultVO getManagerCaseList(@RequestParam("id") String id){
        return disputeProgressService.getManagerCaseList(id);
    }


    /** 管理员获取统计管理页面列表 */
    @GetMapping(value = "/manager/getCase_judiciary")
    public ResultVO getManagerCaseJudiciary(@RequestParam("id") String id){
        return disputeProgressService.getManagerCaseJudiciary(id);
    }

    /** 管理员获取调解员列表（用于给案件分配调解员） */
    @GetMapping(value = "/getMediatorList")
    public ResultVO getMediatorList(@RequestParam("id") String id){
        return disputeProgressService.getMediatorList(id);
    }

    /** 管理员 获取所有调解员的授权信息 */
    @GetMapping(value = "/manager/getNameofAuthorityList")
    public ResultVO getNameofAuthorityList(@RequestParam("id") String id){
        return disputeProgressService.getNameofAuthorityList(id);
    }

    /**管理员发送授权调解员权限信息 */
    @PostMapping(value = "/manager/ChangeAuthorityNameList")
    public ResultVO changeAuthorityNameList(@RequestParam("param") String changeAuthorityFormList){
        return disputeProgressService.changeAuthorityNameList(changeAuthorityFormList);
    }

    //添加具体任务的评价
    @PostMapping(value = "/addTaskComment")
    public ResultVO addTaskComment(@RequestParam(value = "comment") String comment,
                                   @RequestParam(value = "taskId") String taskId,
                                   @RequestParam(value = "disputeId") String disputeId){
        CommentForm commentForm = disputeProgressService.addCommentByTaskId(taskId, disputeId, comment);
        return ResultVOUtil.ReturnBack(commentForm,DisputeProgressEnum.ADD_TASKCOMMIT_SUCCESS.getCode(),DisputeProgressEnum.ADD_TASKCOMMIT_SUCCESS.getMsg());
    }

    /** 进入调解时 获取当前调解阶段、是否具备医疗鉴定资格、医疗鉴定与否、是否具备专家预约资格，当前阶段中的当前步骤（医疗鉴定中、预约中、正在调解中、调解结束）*/
    @GetMapping(value = "/mediator/getMediationStage")
    public ResultVO getMediationStage(@RequestParam("CaseId") String caseId){
        return disputeProgressService.getMediationStage(caseId);
    }

    /** 发送鉴定结果数据 */
    @PostMapping(value = "/mediator/resultOfIndent")
    public ResultVO setResultOfIndent(@RequestParam("CaseId") String caseId,
                                      @RequestParam("resultOfIndent") String resultOfIndent){
        return disputeProgressService.setResultOfIndent(caseId,resultOfIndent);
    }

    /** 发送预约数据 */
    @PostMapping(value = "/mediator/appoint")
    public ResultVO setAppoint(@RequestParam("CaseId") String caseId,
                               @RequestParam("currentStageContent") String currentStageContent){
        return disputeProgressService.setAppoint(caseId,currentStageContent);
    }
    //管理员选择具体某个调解员调解某个案件   wj
    @PostMapping(value = "/manager/PostMediatorForCase")
    public ResultVO decideMediatorDisputeCase(@RequestParam(value = "MediatorId") String mediatorId,
                                              @RequestParam(value = "CaseId") String disputeId){
        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
        Task currentTask=null;
        for(Task task:tasks){
            if(task.getName().equals("管理员做决定")){
                currentTask=task;
                break;
            }
        }
        disputeProgressService.completeCurrentTask(currentTask.getId());

        return disputeProgressService.decideMediatorDisputeCase(mediatorId, disputeId);
    }

    //发送问询医院数据
    @PostMapping(value = "/detail/InquireInstitute")
    public ResultVO inquireInstitute(@RequestParam(value = "CaseId") String caseId,
                                     @RequestParam(value = "InquireText") String inquireText){

        //todo activiti test
        List<Task> tasks=disputeProgressService.searchCurrentTasks(caseId);
        Task currentTask=null;
        for(Task task:tasks){
            if(task.getName().equals("向院方调研")){
                currentTask=task;
                break;
            }
        }
        disputeProgressService.completeCurrentTask(currentTask.getId());

        return disputecaseAccessoryService.addInquireHospital(caseId, inquireText);
    }

    //发送调解成功
    @PostMapping(value = "/mediator/MediationFailure")
    public ResultVO mediationFailure(@RequestParam(value = "CaseId") String caseId){

        return disputeProgressService.setMediationFailure(caseId);
    }

    //发送调解成功
    @PostMapping(value = "/user/CaseRepeal")
    public ResultVO caseRepeal(@RequestParam(value = "CaseId") String caseId){

        return disputeProgressService.setCaseRepeal(caseId);
    }

    //发送调解成功
    @PostMapping(value = "/user/CaseLitigation")
    public ResultVO caseLitigation(@RequestParam(value = "CaseId") String caseId){

        return disputeProgressService.setCaseLitigation(caseId);
    }



}
