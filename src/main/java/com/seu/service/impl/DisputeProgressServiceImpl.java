package com.seu.service.impl;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.converter.DisputeInfo2DisputeRegisterDetailForm;
import com.seu.converter.DisputeRegisterDetailForm2DisputeInfo;
import com.seu.domian.DisputeInfo;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.CommentForm;
import com.seu.form.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.DisputeInfoRepository;
import com.seu.repository.NormalUserRepository;
import com.seu.service.DisputeProgressService;
import com.seu.utils.KeyUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class DisputeProgressServiceImpl implements DisputeProgressService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private DisputeInfoRepository disputeInfoRepository;
    @Autowired
    private NormalUserRepository normalUserRepository;


    private ProcessDefinition pd;
    private Deployment deployment;

    /*
     *@Author 吴宇航
     *@Description  根据starterId来启动流程实例
     *@Date 20:33 2018/7/20
     *@Param [starterId]
     *@return boolean
     **/
    @Override
    public ResultVO startProcess(String disputeId) {
        // TODO 判断用户是否有未完成的纠纷流程实例
//        if(runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(starterId).list().size()!=0){
//            return ResultVOUtil.ReturnBack(DisputeProgressEnum.PROCESSINSTANCE_HASEXIST.getCode(),DisputeProgressEnum.PROCESSINSTANCE_HASEXIST.getMsg());
//        }
        //该用户尚未创建流程实例，可以创建
        deployment=repositoryService.createDeployment().addClasspathResource("processes/disputeProgress.bpmn").deploy();
        pd=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance pi=runtimeService.startProcessInstanceById(pd.getId(),disputeId);
        if(pi==null)
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.STARTUP_FAIL.getCode(),DisputeProgressEnum.STARTUP_FAIL.getMsg());
        else
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.STARTUP_SUCCESS.getCode(),DisputeProgressEnum.STARTUP_SUCCESS.getMsg());
    }
    public ResultVO startProcess(String disputeId,Map<String,Object> vars){
        // TODO 判断用户是否有未完成的纠纷流程实例
//        if(runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(starterId).list().size()!=0){
//            return ResultVOUtil.ReturnBack(DisputeProgressEnum.PROCESSINSTANCE_HASEXIST.getCode(),DisputeProgressEnum.PROCESSINSTANCE_HASEXIST.getMsg());
//        }
        //该用户尚未创建流程实例，可以创建
        deployment=repositoryService.createDeployment().addClasspathResource("processes/disputeProgress.bpmn")
                                                       .addClasspathResource("processes/SubProcess1.bpmn")
                                                       .addClasspathResource("processes/SubProcess2.bpmn")
                                                       .addClasspathResource("processes/SubProcess3.bpmn")
                                                       .addClasspathResource("processes/SubProcess4.bpmn")
                                                       .addClasspathResource("processes/SubProcess5.bpmn")
                                                       .addClasspathResource("processes/SubProcess6.bpmn").deploy();
        pd=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance pi=runtimeService.startProcessInstanceById(pd.getId(),disputeId,vars);
        if(pi==null)
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.STARTUP_FAIL.getCode(),DisputeProgressEnum.STARTUP_FAIL.getMsg());
        else
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.STARTUP_SUCCESS.getCode(),DisputeProgressEnum.STARTUP_SUCCESS.getMsg());
    }



    /*
    查询当前任务
     */
    @Override
    public List<Task> searchCurrentTasks(String disputeId) {
        ProcessInstance pi=runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(disputeId).singleResult();
        return taskService.createTaskQuery().processInstanceId(pi.getId()).list();
    }
//    @Override
//    public List<Task> searchCurrentTasks() {
//        return taskService.createTaskQuery().processInstanceId(pi.getId()).list();
//    }


    @Override
    public void completeCurrentTask(String taskId) {
        taskService.complete(taskId);
    }
    @Override
    public void completeCurrentTask(String taskId, Map<String, Object> vars) {
        taskService.complete(taskId,vars);
    }




    @Override
    public void triggerSignalBoundary(String signal) {
        runtimeService.signalEventReceived(signal);
    }

    @Override
    public void triggerSignalBoundary(String signal, Map<String, Object> vars) {
        runtimeService.signalEventReceived(signal,vars);
    }



    @Override
    public String saveDisputeInfo(DisputeRegisterDetailForm disputeRegisterDetailForm,String starterId) {
        DisputeInfo disputeInfo=new DisputeInfo();
        disputeInfo.setUserId(starterId);
        String disputeId=KeyUtil.genUniqueKey();
        disputeInfo.setDisputeId(disputeId);
        disputeInfo=DisputeRegisterDetailForm2DisputeInfo.convert(disputeRegisterDetailForm,disputeInfo);
        disputeInfoRepository.save(disputeInfo);
        return disputeId;

    }

    @Override
    public DisputeRegisterDetailForm getDisputeForm(String disputeId) {
        DisputeInfo disputeInfo=disputeInfoRepository.getOne(disputeId);
        return DisputeInfo2DisputeRegisterDetailForm.convert(disputeInfo);
    }


    @Override
    public List<DisputeCaseForm> getDisputeListByUserId(String userId, Integer page, Integer size) {
        PageRequest request=new PageRequest(page,size);
        Page<DisputeInfo> disputeInfoPage=disputeInfoRepository.findByUserId(userId,request);
        List<DisputeCaseForm> disputeCaseFormList=new ArrayList<>();
        for(DisputeInfo disputeInfoOne:disputeInfoPage.getContent()){
            DisputeCaseForm disputeCaseForm=new DisputeCaseForm();
            //设置DisputeRegisterDetailForm属性
            disputeCaseForm.setDisputeRegisterDetailForm(DisputeInfo2DisputeRegisterDetailForm.convert(disputeInfoOne));
            //设置userName属性
//            String name=normalUserDetailRepository.findByUserId(disputeInfoOne.getUserId()).getName();
            String name=normalUserRepository.findByFatherId(disputeInfoOne.getUserId()).getName();
            disputeCaseForm.setUserName(name);
            //设置time属性
            String disputeId=disputeInfoOne.getDisputeId();
            Date time=runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(disputeId).singleResult().getStartTime(); //2018-07-21 13:42:49.734 yyyy-MM-dd HH:mm:ss.
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            disputeCaseForm.setRegisterTime(sdf.format(time).toString());
            //设置disputeId
            disputeCaseForm.setDisputeId(disputeInfoOne.getDisputeId());

            disputeCaseFormList.add(disputeCaseForm);
        }
        return disputeCaseFormList;

    }

    @Override
    public List<DisputeCaseForm> getDisputeListByTask(String task, Integer page, Integer size) {

        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup(task).listPage(page,size);
        List<DisputeCaseForm> disputeCaseFormList=new ArrayList<>();
        for(Task taskItem: taskList){
            DisputeCaseForm disputeCaseForm=new DisputeCaseForm();
            //设置DisputeRegisterDetailForm属性
            disputeCaseForm.setDisputeRegisterDetailForm(DisputeInfo2DisputeRegisterDetailForm.convert(new DisputeInfo()));

            //ProcessInstance processInstance = null;
            String disputeID = null;
            while(disputeID == null){
                disputeID = runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceId(taskItem.getProcessInstanceId())
                        .singleResult().getBusinessKey();
            }

            DisputeInfo disputeInfo = disputeInfoRepository.findByDisputeId(disputeID);


            //设置userName属性
//            String name=normalUserDetailRepository.findByUserId(disputeInfo.getUserId()).getName();
            String name=normalUserRepository.findByFatherId(disputeInfo.getUserId()).getName();
            disputeCaseForm.setUserName(name);
            //设置time属性
            String disputeId=disputeInfo.getDisputeId();
            Date time=runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(disputeId).singleResult().getStartTime(); //2018-07-21 13:42:49.734 yyyy-MM-dd HH:mm:ss.
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            disputeCaseForm.setRegisterTime(sdf.format(time).toString());
            //设置disputeId
            disputeCaseForm.setDisputeId(disputeInfo.getDisputeId());

            //设置content


            disputeCaseFormList.add(disputeCaseForm);

        }
        //String disputeID = runtimeService.createProcessInstanceQuery().processInstanceId(taskList.get(0).getProcessInstanceId()).singleResult().getBusinessKey();
        return disputeCaseFormList;
    }

    @Override
    public List<HistoricTaskForm> getHistoricTaskListByDisputeId(String disputeId) {

//        String processInstanceId = runtimeService
//                .createProcessInstanceQuery()
//                .processInstanceBusinessKey(disputeId)
//                .singleResult().getProcessInstanceId();
        String processInstanceId = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(disputeId)
                .singleResult().getId();

        List<HistoricTaskInstance> taskInstanceList = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list();

        List<HistoricTaskForm> historicTaskFormList = new ArrayList<>();

        for (HistoricTaskInstance taskInstanceItem: taskInstanceList) {
            HistoricTaskForm historicTaskForm = new HistoricTaskForm();
            historicTaskForm.setDisputeId(disputeId);
            historicTaskForm.setTaskName(taskInstanceItem.getName());
            historicTaskForm.setCreateTime(taskInstanceItem.getStartTime());
            historicTaskForm.setCompleteTime(taskInstanceItem.getEndTime());
            historicTaskForm.setTaskId(taskInstanceItem.getId());
            historicTaskFormList.add(historicTaskForm);
        }

        return historicTaskFormList;
    }

    @Override
    public CommentForm addCommentByTaskId(String taskId, String disputeId, String comment) {
//        String processInstanceId = runtimeService
//                .createProcessInstanceQuery()
//                .processInstanceBusinessKey(disputeId)
//                .singleResult().getProcessInstanceId();
//        //String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
//        taskService.addComment(taskId, processInstanceId, comment);
//        //taskService.add
//        return new CommentForm(comment, taskId, historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult().getName());
    return null;
    }
}
