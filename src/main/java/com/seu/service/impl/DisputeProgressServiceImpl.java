package com.seu.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.converter.CaseStatusnum2Statustr;
import com.seu.converter.Disputecase2DisputeRegisterDetailForm;
import com.seu.domian.Disputecase;
import com.seu.domian.DisputecaseApply;
import com.seu.domian.DisputecaseProcess;
import com.seu.domian.Mediator;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import com.seu.form.VOForm.ManagerCaseForm;
import com.seu.form.VOForm.MediationHallDataForm;
import com.seu.repository.*;
import com.seu.service.DisputeProgressService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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
    private DisputecaseRepository disputecaseRepository;
    @Autowired
    private NormalUserRepository normalUserRepository;
    @Autowired
    private DisputecaseProcessRepository disputecaseProcessRepository;
    @Autowired
    private DisputecaseApplyRepository disputecaseApplyRepository;
    @Autowired
    private MediatorRepository mediatorRepository;

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


    // TODO 重写该方法
//    @Override
//    public String saveDisputeInfo(DisputeRegisterDetailForm disputeRegisterDetailForm,String starterId) {
//        Disputecase disputecase =new Disputecase();
//        disputecase.setUserId(starterId);
//        String disputeId=KeyUtil.genUniqueKey();
//        disputecase.setDisputeId(disputeId);
//        disputecase =DisputeRegisterDetailForm2DisputeInfo.convert(disputeRegisterDetailForm, disputecase);
//        disputecaseRepository.save(disputecase);
//        return disputeId;
//
//    }

    @Override
    public DisputeRegisterDetailForm getDisputeForm(String disputeId) {
        Disputecase disputecase = disputecaseRepository.getOne(disputeId);
        return Disputecase2DisputeRegisterDetailForm.convert(disputecase);
    }


    @Override
    public List<DisputeCaseForm> getDisputeListByUserId(String userId) {
        /** 从normal_user表中取出身份证 */
        String idCard=normalUserRepository.findByFatherId(userId).getIdCard();
        /** 从apply表中取出所有该用户的相关案件 */
        List<DisputecaseApply> disputecaseApplyList=disputecaseApplyRepository.findAllByIdCard(idCard);
        /** 格式转换为前端需要看到的Form形式 */
        List<DisputeCaseForm> disputeCaseFormList=new ArrayList<>();
        for(DisputecaseApply disputecaseApply:disputecaseApplyList){
            DisputeCaseForm disputeCaseForm=new DisputeCaseForm();
            String caseId=disputecaseApply.getDisputecaseId();
            disputeCaseForm.setTrueName(disputecaseApply.getName());
            disputeCaseForm.setRole((disputecaseApply.getRole()=="0")?"申请人":"代理人");
            disputeCaseForm.setId(caseId);
            Disputecase disputecase=disputecaseRepository.getOne(caseId);
            disputeCaseForm.setDate(disputecase.getApplyTime());
            disputeCaseForm.setName(disputecase.getCaseName());
            DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
            disputeCaseForm.setStatus(CaseStatusnum2Statustr.convert(disputecaseProcess.getStatus()));

            disputeCaseFormList.add(disputeCaseForm);
        }
        return disputeCaseFormList;
        /** 分页下的代码,也需要改 */
//        PageRequest request=new PageRequest(page,size);
//        Page<Disputecase> disputeInfoPage= disputecaseRepository.findByUserId(userId,request);
//        List<DisputeCaseForm> disputeCaseFormList=new ArrayList<>();
//        for(Disputecase disputecaseOne :disputeInfoPage.getContent()){
//            DisputeCaseForm disputeCaseForm=new DisputeCaseForm();
//            //设置DisputeRegisterDetailForm属性
//            disputeCaseForm.setDisputeRegisterDetailForm(Disputecase2DisputeRegisterDetailForm.convert(disputecaseOne));
//            //设置userName属性
////            String name=normalUserDetailRepository.findByUserId(disputecaseOne.getUserId()).getName();
//            String name=normalUserRepository.findByFatherId(disputecaseOne.getUserId()).getName();
//            disputeCaseForm.setUserName(name);
//            //设置time属性
//            String disputeId= disputecaseOne.getDisputeId();
//            Date time=runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(disputeId).singleResult().getStartTime(); //2018-07-21 13:42:49.734 yyyy-MM-dd HH:mm:ss.
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            disputeCaseForm.setRegisterTime(sdf.format(time).toString());
//            //设置disputeId
//            disputeCaseForm.setDisputeId(disputecaseOne.getDisputeId());
//
//            disputeCaseFormList.add(disputeCaseForm);
//        }
//        return disputeCaseFormList;

    }

//    @Override
//    public List<DisputeCaseForm> getDisputeListByTask(String task, Integer page, Integer size) {
//        return null;
//        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup(task).listPage(page,size);
//        List<DisputeCaseForm> disputeCaseFormList=new ArrayList<>();
//        for(Task taskItem: taskList){
//            DisputeCaseForm disputeCaseForm=new DisputeCaseForm();
//            //设置DisputeRegisterDetailForm属性
//            disputeCaseForm.setDisputeRegisterDetailForm(Disputecase2DisputeRegisterDetailForm.convert(new Disputecase()));
//
//            //ProcessInstance processInstance = null;
//            String disputeID = null;
//            while(disputeID == null){
//                disputeID = runtimeService
//                        .createProcessInstanceQuery()
//                        .processInstanceId(taskItem.getProcessInstanceId())
//                        .singleResult().getBusinessKey();
//            }
//
//            Disputecase disputecase = disputecaseRepository.findByDisputeId(disputeID);
//
//
//            //设置userName属性
////            String name=normalUserDetailRepository.findByUserId(disputecase.getUserId()).getName();
//            String name=normalUserRepository.findByFatherId(disputecase.getUserId()).getName();
//            disputeCaseForm.setUserName(name);
//            //设置time属性
//            String disputeId= disputecase.getDisputeId();
//            Date time=runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(disputeId).singleResult().getStartTime(); //2018-07-21 13:42:49.734 yyyy-MM-dd HH:mm:ss.
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            disputeCaseForm.setRegisterTime(sdf.format(time).toString());
//            //设置disputeId
//            disputeCaseForm.setDisputeId(disputecase.getDisputeId());
//
//            //设置content
//
//
//            disputeCaseFormList.add(disputeCaseForm);
//
//        }
//        //String disputeID = runtimeService.createProcessInstanceQuery().processInstanceId(taskList.get(0).getProcessInstanceId()).singleResult().getBusinessKey();
//        return disputeCaseFormList;
//    }

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


    @Override
    public void updateApplyStatus(String disputeId, String ID) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputeId);
        String applyStatus=disputecaseProcess.getApplyStatus();
        if(applyStatus==null)
            applyStatus="";
        if (applyStatus.length()==0){
            applyStatus+=ID;
        }else {
            applyStatus+=","+ID;
        }
        disputecaseProcess.setApplyStatus(applyStatus);
        disputecaseProcessRepository.save(disputecaseProcess);
    }

    @Override
    public void updateAvoidStatus(String disputeId, String ID) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputeId);
        String avoidStatus=disputecaseProcess.getAvoidStatus();
        if(avoidStatus==null)
            avoidStatus="";
        if (avoidStatus.length()==0){
            avoidStatus+=ID;
        }else {
            avoidStatus+=","+ID;
        }
        disputecaseProcess.setApplyStatus(avoidStatus);
        disputecaseProcessRepository.save(disputecaseProcess);
    }

    @Override
    public void updateUserChoose(String disputeId, String mediatorList) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputeId);
        String oldUserChoose=disputecaseProcess.getUserChoose();
        oldUserChoose+=mediatorList;
        disputecaseProcess.setUserChoose(oldUserChoose);
        disputecaseProcessRepository.save(disputecaseProcess);
    }


    @Override
    public ResultVO getMediationHallData(String id) {
        List<Disputecase> disputecaseList=disputecaseRepository.findAll();
        List<MediationHallDataForm> mediationHallDataFormList=new ArrayList<>();
        for(Disputecase disputecase:disputecaseList){
            MediationHallDataForm mediationHallDataForm=new MediationHallDataForm();
            mediationHallDataForm.setDate(disputecase.getApplyTime());
            mediationHallDataForm.setName(disputecase.getCaseName());
            mediationHallDataForm.setNameId(disputecase.getId());
            /** 案件进程到process表中查询 */
            String status=disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus();
            mediationHallDataForm.setStatus(CaseStatusnum2Statustr.convert(status));
            /** 申请人到apply表中查找 */
            String applerListStr=disputecase.getProposerId();
            String[] applyList=applerListStr.trim().split(",");
            List<String> proposerList=new ArrayList<>();
            for(String s:applyList){
                String nameOne=disputecaseApplyRepository.findOne(s).getName();
                proposerList.add(nameOne);
            }
            mediationHallDataForm.setApplicant(proposerList);
            /** 被申请人，即医院需要解析医疗过程 */
            Set<String> respondentList=new HashSet<>();
            String medicalProcess=disputecase.getMedicalProcess();
            JSONArray processList=JSONArray.parseArray(medicalProcess);
            for(int i=0;i<processList.size();++i){
                JSONObject jsStr=processList.getJSONObject(i);
                respondentList.add(jsStr.get("医院").toString());
            }
            mediationHallDataForm.setRespondent(new ArrayList<>(respondentList));


            mediationHallDataFormList.add(mediationHallDataForm);
        }
        return ResultVOUtil.ReturnBack(mediationHallDataFormList,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }


    @Override
    public ResultVO getMyMediationData(String id) {
        /** 根据调解员id号查找所有与他相关的案件 */
        List<Disputecase> disputecaseList=disputecaseRepository.findByMediatorId(id);
        List<MediationHallDataForm> mediationHallDataFormList=new ArrayList<>();
        for(Disputecase disputecase:disputecaseList){
            MediationHallDataForm mediationHallDataForm=new MediationHallDataForm();
            mediationHallDataForm.setDate(disputecase.getApplyTime());
            mediationHallDataForm.setName(disputecase.getCaseName());
            mediationHallDataForm.setNameId(disputecase.getId());
            /** 案件进程到process表中查询 */
            String status=disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus();
            mediationHallDataForm.setStatus(CaseStatusnum2Statustr.convert(status));
            /** 申请人到apply表中查找 */
            String applerListStr=disputecase.getProposerId();
            String[] applyList=applerListStr.trim().split(",");
            List<String> proposerList=new ArrayList<>();
            for(String s:applyList){
                String nameOne=disputecaseApplyRepository.findOne(s).getName();
                proposerList.add(nameOne);
            }
            mediationHallDataForm.setApplicant(proposerList);
            /** 被申请人，即医院需要解析医疗过程 */
            Set<String> respondentList=new HashSet<>();
            String medicalProcess=disputecase.getMedicalProcess();
            JSONArray processList=JSONArray.parseArray(medicalProcess);
            for(int i=0;i<processList.size();++i){
                JSONObject jsStr=processList.getJSONObject(i);
                respondentList.add(jsStr.get("医院").toString());
            }
            mediationHallDataForm.setRespondent(new ArrayList<>(respondentList));


            mediationHallDataFormList.add(mediationHallDataForm);
        }
        return ResultVOUtil.ReturnBack(mediationHallDataFormList,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getManagerCaseList(String id) {
        List<Disputecase> disputecaseList=disputecaseRepository.findAll();
        List<ManagerCaseForm> managerCaseFormList=new ArrayList<>();
        for(Disputecase disputecase:disputecaseList) {
            ManagerCaseForm managerCaseForm = new ManagerCaseForm();
            managerCaseForm.setDate(disputecase.getApplyTime());
            managerCaseForm.setName(disputecase.getCaseName());
            managerCaseForm.setNameid(disputecase.getId());
            /** 案件进程到process表中查询 */
            String status = disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus();
            managerCaseForm.setStatus(CaseStatusnum2Statustr.convert(status));
            /** 申请人到apply表中查找 */
            String applerListStr = disputecase.getProposerId();
            String[] applyList = applerListStr.trim().split(",");
            List<String> proposerList = new ArrayList<>();
            for (String s : applyList) {
                String nameOne = disputecaseApplyRepository.findOne(s).getName();
                proposerList.add(nameOne);
            }
            managerCaseForm.setApplicant(proposerList);
            /** 被申请人，即医院需要解析医疗过程 */
            Set<String> respondentList = new HashSet<>();
            String medicalProcess = disputecase.getMedicalProcess();
            JSONArray processList = JSONArray.parseArray(medicalProcess);
            for (int i = 0; i < processList.size(); ++i) {
                JSONObject jsStr = processList.getJSONObject(i);
                respondentList.add(jsStr.get("医院").toString());
            }
            managerCaseForm.setRespondent(new ArrayList<>(respondentList));
            /** 当前调解员姓名和id，可能为空，通过调解员id号去mediator表中查找 */
            String setCurrentMediatorName = null;
            String setCurrentMediatorId = null;
            if (disputecase.getMediatorId() == null || disputecase.getMediatorId() == ""){
                setCurrentMediatorName = "";
                setCurrentMediatorId = "";
            }else {
                setCurrentMediatorName=mediatorRepository.findByFatherId(disputecase.getMediatorId()).getMediatorName();
                setCurrentMediatorId=disputecase.getMediatorId();
            }
            managerCaseForm.setCurrentMediator(setCurrentMediatorName);
            managerCaseForm.setMediatorid(setCurrentMediatorId);

            /** 用户意向的调解员 */
            DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputecase.getId());
            String userChoose=disputecaseProcess.getUserChoose();
            for(String s:userChoose.trim().split(",")){
                //s是每个用户选择的调解员id号
                Mediator mediator=mediatorRepository.findByFatherId(s);
                managerCaseForm.addUserIntention(mediator.getMediatorName(),mediator.getFatherId());
            }
            managerCaseFormList.add(managerCaseForm);
        }
        return ResultVOUtil.ReturnBack(managerCaseFormList,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }
}
