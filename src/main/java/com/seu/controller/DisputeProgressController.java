package com.seu.controller;


import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.Const;
import com.seu.domian.DisputeInfo;
import com.seu.domian.NormalUser;
import com.seu.enums.DisputeProgressEnum;
import com.seu.enums.LoginEnum;
import com.seu.enums.UpdateInfoEnum;
import com.seu.form.CommentForm;
import com.seu.form.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.DisputeInfoRepository;
import com.seu.service.DisputeProgressService;
import com.seu.service.INormalUserService;
import com.seu.service.NormalUserDetailService;
import com.seu.utils.CurrentTimeUtil;
import com.seu.utils.DisputeProcessReturnMap;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/disputeProgress")
@CrossOrigin
public class DisputeProgressController {
    @Autowired
    private DisputeProgressService disputeProgressService;
    @Autowired
    private NormalUserDetailService normalUserDetailService;

    @Autowired
    private INormalUserService iNormalUserService;


    /*
     *@Author 吴宇航
     *@Description  纠纷登记功能，由当前用户操作;
     *@Date 22:05 2018/7/20
     *@Param [disputeRegisterDetailForm, bindingResult, session]
     *@return com.seu.ViewObject.ResultVO
     **/
    // TODO 存在问题，即用户必须在进入纠纷登记页面后完成整个操作，否则不会做任何保存
    @PostMapping(value="/disputeRegister")
    @Transactional
    public ResultVO disputeRegister(@Valid DisputeRegisterDetailForm disputeRegisterDetailForm,
                                    BindingResult bindingResult,HttpSession session){
        if(bindingResult.hasErrors()){
            // bindingResult.getFieldError().getDefaultMessage()表示返回验证失败的地方，比如XX必填
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.DISPUTEREGISTER_FAIL.getCode(),DisputeProgressEnum.DISPUTEREGISTER_FAIL.getMsg());
        }
        //1、存储纠纷信息
        String userId=((NormalUser)session.getAttribute("currentUser")).getUserId();
        disputeRegisterDetailForm=new DisputeRegisterDetailForm(); //测试用，到时候删去
        String disputeId=disputeProgressService.saveDisputeInfo(disputeRegisterDetailForm,userId);
        System.out.println("disputeId："+disputeId);
        String name=normalUserDetailService.findNormalUserNameByUserId(userId);
        String email=normalUserDetailService.findEmailByUserId(userId);

        String phone = iNormalUserService.findPhoneByUserId(userId);

        session.setAttribute("name",name);
        Map<String,Object> vars=new HashMap<>();
        vars.put("disputeId",disputeId);
        vars.put("userId",userId);
        vars.put("email",email);

        vars.put("phone",phone);

        //2、用纠纷信息id启动流程
        disputeProgressService.startProcess(disputeId,vars);

        //3、完成纠纷登记操作
        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
        disputeProgressService.completeCurrentTask(tasks.get(0).getId());
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),(String)session.getAttribute("name"));
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.DISPUTEREGISTER_SUCCESS.getCode(),DisputeProgressEnum.DISPUTEREGISTER_SUCCESS.getMsg());

    }

    /*
     *@Author 吴宇航
     *@Description  //暂存确认功能，建立在调解员选定具体案例并且确认
     *@Date 21:07 2018/7/20
     *@Param [starterId, session] 假设前端传过来要确认的纠纷案例id或者当事人id，目前只写了当事人id
     *@return com.seu.ViewObject.ResultVO
     **/
    //TODO 假设是调解员登录且session中已经保存有调解员的name
    @PostMapping(value="/temporaryConfirm")
    public ResultVO temporaryConfirm(@RequestParam("disputeId") String disputeId,
                                     HttpSession session){
        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
        disputeProgressService.completeCurrentTask(tasks.get(0).getId());
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),(String)session.getAttribute("name"));
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getCode(),DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getMsg());

    }

    /*
     *@Author 吴宇航
     *@Description  立案判断功能，建立在管理员选定具体案例并且审核
     *@Date 21:11 2018/7/20
     *@Param [result通过与否, starterId当前审核案件用户id, session]
     *@return com.seu.ViewObject.ResultVO
     **/
    //TODO 不通过需要给出理由吗？
    //TODO 假设是管理员登录且session中已经保存有调解员的name
    @PostMapping(value="/caseAccept")
    public ResultVO caseAccept(@RequestParam(value="result") boolean result,
                               @RequestParam("disputeId") String disputeId,
                               HttpSession session){
        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
        Map<String,Object> var=new HashMap<>();
        if(result==true)
            var.put("caseAccept",0);
        else
            var.put("caseAccept",1);
        disputeProgressService.completeCurrentTask(tasks.get(0).getId(),var);
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),(String)session.getAttribute("name"));
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
    public ResultVO disputeRegisterModify(HttpSession session,
                                          @RequestParam("disputeId") String disputeId){
//        String starterId=((NormalUser)session.getAttribute("currentUser")).getUserId();
        String signal="disputeRegisterModifySignal";
        Task currentTask=disputeProgressService.searchCurrentTasks(disputeId).get(0);
        // 先取出流程中的纠纷案件内容,设置为DisputeRegisterDetailForm形式
        DisputeRegisterDetailForm disputeRegisterDetailForm=disputeProgressService.getDisputeForm(disputeId) ;
        disputeProgressService.triggerSignalBoundary(signal);
        Task afterTask=disputeProgressService.searchCurrentTasks(disputeId).get(0);
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(currentTask.getName(),(String)session.getAttribute("name"));
        map.put("after task",afterTask.getName());
        map.put("disputeRegisterContent",disputeRegisterDetailForm);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.TRIGGERSIGNAL_SUCCESS.getCode(),DisputeProgressEnum.TRIGGERSIGNAL_SUCCESS.getMsg());
    }

    /*
     *@Author 吴宇航
     *@Description  用户在每次进行任务前，都先要查询他所拥有的disputeId(未完成)，从中选出disputeId并进行相应的操作
     *@Date 13:22 2018/7/21
     *@Param [session, page, size] page从1开始，需要处理减一
     *@return com.seu.ViewObject.ResultVO
     **/
    @PostMapping(value = "/disputeList")
    public ResultVO getDisputeListByUserId(HttpSession session,
                                           @RequestParam(value = "page",defaultValue = "1") Integer page,
                                           @RequestParam(value = "size",defaultValue = "10") Integer size){
        String userId=((NormalUser)session.getAttribute(Const.CURRENT_USER)).getUserId();
        List<DisputeCaseForm> disputeCaseFormList=disputeProgressService.getDisputeListByUserId(userId,page-1,size);
        Map<String,Object> map=new HashMap<>();
        map.put("disputeCaseList",disputeCaseFormList);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.SEARCH_DISPUTECASELIST_SUCCESS.getCode(),DisputeProgressEnum.SEARCH_DISPUTECASELIST_SUCCESS.getMsg());
    }

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private DisputeInfoRepository disputeInfoRepository;


    /**
     * @Author: W
     * @Description: 查询待办任务，根据 参数 task
     * @Date: 18:08 2018/7/21
     * @param page
     * @param size
     */
    @PostMapping(value = "/taskList")
    public ResultVO testlist(HttpSession httpSession,
                             @RequestParam(value = "page",defaultValue = "1") Integer page,
                             @RequestParam(value = "size",defaultValue = "10") Integer size,
                             @RequestParam("task") String task){
        //todo 身份认证
        List<DisputeCaseForm> disputeCaseFormList=disputeProgressService.getDisputeListByTask(task,page-1,size);
        Map<String,Object> map=new HashMap<>();
        map.put("disputeCaseList",disputeCaseFormList);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.SEARCH_TASK_SUCCESS.getCode(),DisputeProgressEnum.SEARCH_TASK_SUCCESS.getMsg());
    }

    @PostMapping(value = "/historicTaskList")
    public ResultVO getHistoricTaskListByDispute(HttpSession session,
                                                 @RequestParam(value = "disputeId") String disputeId){
        // todo 身份认证(普通用户)

        List<HistoricTaskForm> historicTaskFormList = disputeProgressService
                .getHistoricTaskListByDisputeId(disputeId);
        Map<String,Object> map=new HashMap<>();
        map.put("historicTaskFormList",historicTaskFormList);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.SEARCH_HISTORICTASKLIST_SUCESS.getCode(),DisputeProgressEnum.SEARCH_HISTORICTASKLIST_SUCESS.getMsg());
    }
    /*
     *@Author 吴宇航
     *@Description  //调解员确定用户(纠纷案件) 完成”调解员选用户“任务
     *@Date 17:01 2018/7/24
     *@Param [disputeId, session]
     *@return com.seu.ViewObject.ResultVO
     **/
//    @PostMapping(value = "/mediatorSelectUser")
//    public ResultVO mediatorSelectUser(@RequestParam("disputeId") String disputeId,
//                                       HttpSession session){
//        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
//        Task currentTask=null;
//        for(Task task:tasks){
//            if(task.getName().equals("调解员选用户")){
//                currentTask=task;
//                break;
//            }
//        }
//        disputeProgressService.completeCurrentTask(currentTask.getId());
//        // TODO 添加参数，可能是local disputeId 关联的 意向 disputeId
//    }

    /*
     *@Author 吴宇航
     *@Description  //用户确认调解员 完成”用户选调解员“任务
     *@Date 17:01 2018/7/24
     *@Param [disputeId, session]
     *@return com.seu.ViewObject.ResultVO
     **/
//    @PostMapping(value = "/userSelectMediator")
//    public ResultVO userSelectMediator(@RequestParam("mediatorId") String mediatorId,
//                                       @RequestParam("disputeId") String disputeId,
//                                       HttpSession session){
//        List<Task> tasks=disputeProgressService.searchCurrentTasks(disputeId);
//        Task currentTask=null;
//        for(Task task:tasks){
//            if(task.getName().equals("用户选择调解员")){
//                currentTask=task;
//                break;
//            }
//        }
//        disputeProgressService.completeCurrentTask(currentTask.getId());
//        // TODO 添加参数，可能是local disputeId 关联的 意向 disputeId
//    }

    //添加具体任务的评价
    @PostMapping(value = "/addTaskComment")
    public ResultVO addTaskComment(HttpSession session,
                                   @RequestParam(value = "comment") String comment,
                                   @RequestParam(value = "taskId") String taskId,
                                   @RequestParam(value = "disputeId") String disputeId){
        CommentForm commentForm = disputeProgressService.addCommentByTaskId(taskId, disputeId, comment);
        return ResultVOUtil.ReturnBack(commentForm,DisputeProgressEnum.ADD_TASKCOMMIT_SUCCESS.getCode(),DisputeProgressEnum.ADD_TASKCOMMIT_SUCCESS.getMsg());
    }

}
