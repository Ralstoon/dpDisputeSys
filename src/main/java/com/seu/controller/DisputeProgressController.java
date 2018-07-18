package com.seu.controller;


import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.enums.DisputeProgressEnum;
import com.seu.enums.UpdateInfoEnum;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.service.DisputeProgressService;
import com.seu.utils.CurrentTimeUtil;
import com.seu.utils.DisputeProcessReturnMap;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    /*
      启动流程实例，目前不考虑用户组问题
     */
    @PostMapping(value = "/startup")
    public ResultVO startDisputeProgress(){
        //TODO 尚未考虑用户
        boolean isStartup=disputeProgressService.startProcess();
        if(isStartup==true)
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.STARTUP_SUCCESS.getCode(),DisputeProgressEnum.STARTUP_SUCCESS.getMsg());
        else
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.STARTUP_FAIL.getCode(),DisputeProgressEnum.STARTUP_FAIL.getMsg());
    }

    /*
    纠纷登记功能，由当前用户操作
     */
    @PostMapping(value="/disputeRegister")
    public ResultVO disputeRegister(@Valid DisputeRegisterDetailForm disputeRegisterDetailForm,
                                    BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            // bindingResult.getFieldError().getDefaultMessage()表示返回验证失败的地方，比如XX必填
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.DISPUTEREGISTER_FAIL.getCode(),DisputeProgressEnum.DISPUTEREGISTER_FAIL.getMsg());
        }
        disputeRegisterDetailForm=new DisputeRegisterDetailForm(); //测试用，到时候删去
        Map<String,Object> var=new HashMap<>();
        var.put("disputeRegisterContent",disputeRegisterDetailForm);
        List<Task> tasks=disputeProgressService.searchCurrentTasks("当前用户"); //TODO 完善当前用户
        disputeProgressService.completeCurrentTask(tasks.get(0).getId(),var);
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),"受理人");//TODO 完善受理人名字


        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.DISPUTEREGISTER_SUCCESS.getCode(),DisputeProgressEnum.DISPUTEREGISTER_SUCCESS.getMsg());

    }

    /*
        暂存确认功能
        TODO 目前只做了通过功能，不知是否会不通过
    */
    @PostMapping(value="/temporaryConfirm")
    public ResultVO temporaryConfirm(){

        List<Task> tasks=disputeProgressService.searchCurrentTasks("当前用户"); //TODO 完善当前用户
        disputeProgressService.completeCurrentTask(tasks.get(0).getId());
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),"受理人");//TODO 完善受理人名字

        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getCode(),DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getMsg());

    }
    /*
        立案判断功能
        判断结果为 通过和不通过    TODO 不通过需要给出理由吗？
        传入参数： result ： true/false
                  reason ：
        传给流程中判断，0为通过，1为不通过
    */
    @PostMapping(value="/caseAccept")
    public ResultVO caseAccept(@RequestParam(value="result") boolean result){
        List<Task> tasks=disputeProgressService.searchCurrentTasks("当前用户"); //TODO 完善当前用户
        Map<String,Object> var=new HashMap<>();
        if(result==true)
            var.put("caseAccept",0);
        else
            var.put("caseAccept",1);
        disputeProgressService.completeCurrentTask(tasks.get(0).getId(),var);
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(tasks.get(0).getName(),"受理人");//TODO 完善受理人名字
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getCode(),DisputeProgressEnum.TEMPORARYCONFIRM_SUCCESS.getMsg());

    }


    /*
        用户提出对纠纷登记内容的修改,将之前登记的内容返还给前端
    */
    @PostMapping(value="/disputeRegisterModify")
    public ResultVO disputeRegisterModify(){
        String signal="disputeRegisterModifySignal";
        Task currentTask=disputeProgressService.searchCurrentTasks().get(0);
        // 先取出流程中的纠纷案件内容,参数名为disputeRegisterContent
        DisputeRegisterDetailForm disputeRegisterDetailForm=(DisputeRegisterDetailForm)disputeProgressService.getParamFormMPI("disputeRegisterContent") ;
        disputeProgressService.triggerSignalBoundary(signal);
        Task afterTask=disputeProgressService.searchCurrentTasks().get(0);
        Map<String,Object> map=DisputeProcessReturnMap.initDisputeProcessReturnMap(currentTask.getName(),"受理人");//TODO 完善受理人名字
        map.put("after task",afterTask.getName());
        map.put("disputeRegisterContent",disputeRegisterDetailForm);
        return ResultVOUtil.ReturnBack(map,DisputeProgressEnum.TRIGGERSIGNAL_SUCCESS.getCode(),DisputeProgressEnum.TRIGGERSIGNAL_SUCCESS.getMsg());
    }


}
