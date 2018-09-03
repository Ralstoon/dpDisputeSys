package com.seu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONStreamAware;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.InitConstant;
import com.seu.converter.CaseStatusnum2Statustr;
import com.seu.converter.Disputecase2DisputeRegisterDetailForm;
import com.seu.domian.*;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.*;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.*;
import com.seu.service.DisputeProgressService;
import com.seu.utils.AutoSendUtil;
import com.seu.utils.GetHospitalUtil;
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
    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;
    @Autowired
    private DisputecaseActivitiRepository disputecaseActivitiRepository;
    @Autowired
    private ExpertsRepository expertsRepository;

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
        DisputecaseActiviti disputecaseActiviti=new DisputecaseActiviti();
        disputecaseActiviti.setDisputecaseId(disputeId);
        deployment=repositoryService.createDeployment().addClasspathResource("processes/disputeProgress.bpmn")
                                                        .addClasspathResource("processes/SubProcess5.bpmn")
                                                         .addClasspathResource("processes/SubProcess6.bpmn").deploy();
        pd=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance pi=runtimeService.startProcessInstanceById(pd.getId(),disputeId);

        disputecaseActiviti.setProcessId(pi.getId());
        if(pi==null)
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.STARTUP_FAIL.getCode(),DisputeProgressEnum.STARTUP_FAIL.getMsg());
        else{
            disputecaseActivitiRepository.save(disputecaseActiviti);
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.STARTUP_SUCCESS.getCode(),DisputeProgressEnum.STARTUP_SUCCESS.getMsg());
        }
    }
    public ResultVO startProcess(String disputeId,Map<String,Object> vars){
        // TODO 判断用户是否有未完成的纠纷流程实例
//        if(runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(starterId).list().size()!=0){
//            return ResultVOUtil.ReturnBack(DisputeProgressEnum.PROCESSINSTANCE_HASEXIST.getCode(),DisputeProgressEnum.PROCESSINSTANCE_HASEXIST.getMsg());
//        }
        //该用户尚未创建流程实例，可以创建
        deployment=repositoryService.createDeployment().addClasspathResource("processes/disputeProgress.bpmn")
//                                                       .addClasspathResource("processes/SubProcess1.bpmn")
//                                                       .addClasspathResource("processes/SubProcess2.bpmn")
//                                                       .addClasspathResource("processes/SubProcess3.bpmn")
//                                                       .addClasspathResource("processes/SubProcess4.bpmn")
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
            mediationHallDataForm.setCaseId(disputecase.getId());
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
            mediationHallDataForm.setCaseId(disputecase.getId());
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
            managerCaseForm.setCaseId(disputecase.getId());
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

    @Override
    public ResultVO decideMediatorDisputeCase(String mediator, String caseId) {
        Disputecase disputecase = disputecaseRepository.findOne(caseId);
        disputecase.setMediatorId(mediator);
        Map map=new HashMap();
        map.put("meditorId", mediator);
        map.put("caseID", caseId);
        disputecaseRepository.save(disputecase);
       // disputecaseRepository
        return ResultVOUtil.ReturnBack(map,111, "管理员确定调解员成功");// todo 合并后确定返回码、信息
    }

    @Override
    public ResultVO setMediationSuccess(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("4");
        return ResultVOUtil.ReturnBack(112,"调解成功");
    }

    @Override
    public ResultVO setMediationFailure(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("3");
        return ResultVOUtil.ReturnBack(113,"调解失败");
    }

    @Override
    public ResultVO setCaseRepeal(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("7");
        return ResultVOUtil.ReturnBack(114,"撤销案件");
    }


    @Override
    public ResultVO getManagerCaseJudiciary(String id) {
        List<Disputecase> disputecaseList=disputecaseRepository.findAll();
        List<ManagerCaseForm> managerCaseFormList=new ArrayList<>();
        for(Disputecase disputecase:disputecaseList) {
            ManagerCaseForm managerCaseForm = new ManagerCaseForm();
            managerCaseForm.setDate(disputecase.getApplyTime());
            managerCaseForm.setName(disputecase.getCaseName());
            managerCaseForm.setCaseId(disputecase.getId());
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

            managerCaseFormList.add(managerCaseForm);
        }
        return ResultVOUtil.ReturnBack(managerCaseFormList,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getMediatorList(String id) {
        List<Mediator> mediatorList=mediatorRepository.findAll();
        List<OneMediatorForm> mediatorFormList=new ArrayList<>();
        for(Mediator mediator:mediatorList){
            String name=mediator.getMediatorName();
            String mediatorId=mediator.getFatherId();
            mediatorFormList.add(new OneMediatorForm(name,mediatorId));
        }
        return ResultVOUtil.ReturnBack(mediatorFormList,DisputeProgressEnum.GETMEDIATORLIST_SUCCESS.getCode(),DisputeProgressEnum.GETMEDIATORLIST_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getNameofAuthorityList(String id) {
        List<Mediator> mediatorList=mediatorRepository.findAll();
        List<MediatorAuthorityForm> mediatorAuthorityFormList=new ArrayList<>();
        for(Mediator mediator:mediatorList){
            String name=mediator.getMediatorName();
            String mediatorId=mediator.getFatherId();
            String authority_confirm=mediator.getAuthorityConfirm();
            String authority_judiciary=mediator.getAuthorityJudiciary();
            mediatorAuthorityFormList.add(new MediatorAuthorityForm(name,mediatorId,authority_confirm,authority_judiciary));
        }
        return ResultVOUtil.ReturnBack(mediatorAuthorityFormList,DisputeProgressEnum.GETNAMEOFAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.GETNAMEOFAUTHORITY_SUCCESS.getMsg());
    }

    @Override
    public ResultVO changeAuthorityNameList(String changeAuthorityFormList) {
        JSONArray jsArr=JSONArray.parseArray(changeAuthorityFormList);
        int len=jsArr.size();
        for(int i=0;i<len;++i){
            JSONObject jsobj=jsArr.getJSONObject(i);
            String id=jsobj.getString("nameid");
            String authority1="0";
            if(jsobj.get("authority1").toString().trim().equals("true"))
                authority1="1";
            String authority2="0";
            if(jsobj.get("authority2").toString().trim().equals("true"))
                authority2="1";
            Mediator mediator=mediatorRepository.findByFatherId(id);
            mediator.setAuthorityConfirm(authority1);
            mediator.setAuthorityJudiciary(authority2);
            mediatorRepository.save(mediator);
        }
        return  ResultVOUtil.ReturnBack(DisputeProgressEnum.CHANGEMEDIATORAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.CHANGEMEDIATORAUTHORITY_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getMediationStage(String caseId) {
        MediationStageForm mediationStageForm=new MediationStageForm();
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        /** 防止该json字段初始化的时候没有值 */
        if(currentProcess.getMediateStage()==null ||currentProcess.getMediateStage()=="") {
            currentProcess.setMediateStage(InitConstant.init_mediateStage);
            currentProcess=disputecaseProcessRepository.save(currentProcess);
        }


        /** 获取当前调解阶段 */
        JSONArray stage=JSONArray.parseArray(currentProcess.getMediateStage());
        mediationStageForm.setStage(stage.size());
        JSONObject currentStage=stage.getJSONObject(stage.size()-1);
        /** 获取当前步骤0 鉴定中  1 预约中  2调解中，并决定下面那些操作不要做 */
        Integer currentStatus=null;
        /** accessory表查询鉴定结果 */


        /** 是否具备鉴定资格以及是否做过鉴定,先从案件表的金额判断，后从流程图的参数判断 */
        Disputecase disputecase=disputecaseRepository.getOne(caseId);
        ProcessInstance pi=runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(caseId).singleResult();
        if(disputecase.getClaimMoney().trim()=="2") {  // 10w以上
            mediationStageForm.setIdentiQualify(true);
//            String temp=pi.getProcessVariables().get("paramAuthenticate").toString().trim();
            String temp=taskService.getVariable(pi.getId(),"paramAuthenticate").toString().trim();
            if(temp=="0") {  //尚未做过鉴定
                mediationStageForm.setIdentified(false);
                currentStatus=0;
            }
            else{
                mediationStageForm.setIdentified(true);
                currentStatus=1;
                DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
                JSONObject tempp=JSONObject.parseObject(disputecaseAccessory.getMedicaldamageAssessment());
                if(tempp.getString("文本")!="")
                    mediationStageForm.setResultOfIdentify(tempp.getString("文本"));
                else{
                    String url=tempp.getString("文件url");
                    mediationStageForm.setResultOfIdentify(url);
                }

            }
        }else{
            mediationStageForm.setIdentified(false);
            mediationStageForm.setIdentiQualify(false);
            currentStatus=1;
        }

        /** 能否预约专家  先看金额，后看是否已预约过*/

        if(disputecase.getClaimMoney().trim()!="0" && taskService.getVariable(pi.getId(),"paramProfesor").toString().trim()=="0")
            mediationStageForm.setExpert(true);
        else
            mediationStageForm.setExpert(false);

        /** applicant`respondent数组 */
            /** 1、申请人 */
        List<String> app=new ArrayList<>();
        List<String> res=new ArrayList<>();
        for(String s:disputecase.getProposerId().trim().split(",")){
            app.add(disputecaseApplyRepository.getOne(s).getName());
        }
            /** 2、院方 */
        res=GetHospitalUtil.extract(disputecase.getMedicalProcess());

        mediationStageForm.setApplicant(app);
        mediationStageForm.setRespondent(res);

        // 直接取出来即可
        String currentStageContent=currentStage.getJSONObject("预约调解").getJSONObject("currentStageContent").toString();
        mediationStageForm.setCurrentStageContent(currentStageContent);
        /** currentStageContent */

        if(currentStage.getJSONObject("预约调解").getJSONObject("currentStageContent").getString("MediationPlace")=="") // 说明页面2没有填
            currentStatus=1;
        else
            currentStatus=2;

        mediationStageForm.setCurrentStatus(currentStatus);
        return ResultVOUtil.ReturnBack(mediationStageForm,DisputeProgressEnum.GETMEDIATIONSTAGE_SUCCESS.getCode(),DisputeProgressEnum.GETMEDIATIONSTAGE_SUCCESS.getMsg());

    }

    @Override
    public ResultVO setResultOfIndent(String caseId, String resultOfIndent) {

        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        JSONObject md=JSONObject.parseObject(disputecaseAccessory.getMedicaldamageAssessment());
        md.put("文本",resultOfIndent);
        disputecaseAccessory.setMedicaldamageAssessment(md.toString());
        disputecaseAccessory=disputecaseAccessoryRepository.save(disputecaseAccessory);

        // 防止进入子流程后无法用businesskey来查，这边用自定义的activiti表查找主流程实例
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        ProcessInstance pi=runtimeService.createProcessInstanceQuery().processInstanceId(pid).singleResult();
        taskService.setVariable(pi.getId(),"paramAuthenticate","1");

        /** 目前处于主流程:损害/医疗鉴定 */
        Task task=taskService.createTaskQuery().processInstanceId(pid).singleResult();
        taskService.complete(task.getId());  // 会进去到流程 调解前处理

        return ResultVOUtil.ReturnBack(DisputeProgressEnum.SETRESULTOFINDENT_SUCCESS.getCode(),DisputeProgressEnum.SETRESULTOFINDENT_SUCCESS.getMsg());

        /** 目前处于子流程:调解前处理的第一个任务，调解前选择 */
//        ProcessInstance subPi=runtimeService.createProcessInstanceQuery().superProcessInstanceId(pid).singleResult();
//        Map<String,Object> subvar=new HashMap<>();
//        subvar.put("subP2T1",0);
//        Task subTask1=taskService.createTaskQuery().processInstanceId(subPi.getId()).singleResult();
//        taskService.complete(subTask1.getId(),subvar);
//        Task subTask2=

    }

    @Override
    public ResultVO setAppoint(String caseId, String currentStageContent) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        JSONArray jsArr=JSONArray.parseArray(disputecaseProcess.getMediateStage());
        /** 判断当前阶段做到哪了
         * 有两种情况：
         * 1、当前阶段的预约调解还未做
         * 2、当前阶段的预约调解做了，正在调解没做
         * 3、当前阶段的正在调解已经做了
         * */
        JSONObject currentStage=jsArr.getJSONObject(jsArr.size()-1);
        JSONObject js1=currentStage.getJSONObject("预约调解");
        JSONObject csc=currentStage.getJSONObject("预约调解").getJSONObject("currentStageContent");
        Integer temp=null;
        if(csc.getString("MediationPlace")=="")
            temp=1;
        else if(currentStage.getJSONObject("正在调解").isEmpty())
            temp=2;
        else
            temp=3;

        switch(temp){
            case 1:
                List<String> app=new ArrayList<>();
                List<String> res=new ArrayList<>();
                Disputecase disputecase=disputecaseRepository.getOne(caseId);
                for(String s:disputecase.getProposerId().trim().split(",")){
                    app.add(disputecaseApplyRepository.getOne(s).getName());
                }

                res=GetHospitalUtil.extract(disputecase.getMedicalProcess());

                js1.put("applicant",app);
                js1.put("respondent",res);
                js1.put("currentStageContent",currentStageContent);
                currentStage.put("预约调解",js1);
                jsArr.set(jsArr.size()-1,currentStage);
                disputecaseProcess.setMediateStage(jsArr.toString());
                disputecaseProcessRepository.save(disputecaseProcess);
                break;
            case 2:
                /** 覆盖旧的预约调解 */
                js1.put("currentStageContent",currentStageContent);
                currentStage.put("预约调解",js1);
                jsArr.set(jsArr.size()-1,currentStage);
                disputecaseProcess.setMediateStage(jsArr.toString());
                disputecaseProcessRepository.save(disputecaseProcess);
                break;
            case 3:
                /** 说明进行到新的一轮调解 */
                JSONObject newJs=JSONObject.parseObject(InitConstant.init_mediateStage);
                JSONObject newJs_js1=newJs.getJSONObject("预约调解");
                List<String> app2=new ArrayList<>();
                List<String> res2=new ArrayList<>();
                Disputecase disputecase2=disputecaseRepository.getOne(caseId);
                for(String s:disputecase2.getProposerId().trim().split(",")){
                    app2.add(disputecaseApplyRepository.getOne(s).getName());
                }

                res2=GetHospitalUtil.extract(disputecase2.getMedicalProcess());

                newJs_js1.put("applicant",app2);
                newJs_js1.put("respondent",res2);
                newJs_js1.put("currentStageContent",currentStageContent);
                newJs.put("预约调解",newJs_js1);
                jsArr.add(newJs);
                disputecaseProcess.setMediateStage(jsArr.toString());
                disputecaseProcessRepository.save(disputecaseProcess);
                break;
        }

        /**
         * 当前步骤为 调解前预约
         * 1、设置appointResult 参数 ： 0为三方 1为专家
         * 2、流程往下走一格
         * 3、自动发送预约消息
         */
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Integer result=0;
        if(JSONObject.parseObject(currentStageContent).getJSONArray("Experts").size()!=0)
            result=1;
        Map<String ,Object> var=new HashMap<>();
        var.put("appointResult",result);
        Task currentTask=taskService.createTaskQuery().processInstanceId(pid).singleResult();
        taskService.complete(currentTask.getId(),var);
        // 3的流程看webServiceTask MediateInform

        return ResultVOUtil.ReturnBack(DisputeProgressEnum.SETAPPOINT_SUCCESS.getCode(),DisputeProgressEnum.SETAPPOINT_SUCCESS.getMsg());


    }

    @Override
    public ResultVO setCaseLitigation(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("7");
        return ResultVOUtil.ReturnBack(115,"撤销案件");
    }

    @Override
    public ResultVO reMediation(String caseId) {
        /** 修改状态为调解中、同时阶段也要增加，调解过程页面直接获取新阶段 */
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("2");
        JSONArray arr=JSONArray.parseArray(disputecaseProcess.getMediateStage());
        arr.add(JSONObject.parseObject(InitConstant.init_mediateStage));
        disputecaseProcess.setMediateStage(arr.toString());
        disputecaseProcess=disputecaseProcessRepository.save(disputecaseProcess);
        /**
         * 对流程进行更新
         *  设置流程参数paramMediateResult  0成功；1诉讼；2走撤销；3走其他
         * */
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Task currentTask=taskService.createTaskQuery().processInstanceId(pid).singleResult();  // 调解结果处理
        Map<String,Object> var=new HashMap<>();
        var.put("paramMediateResult",3);
        taskService.complete(currentTask.getId(),var);
        return getMediationStage(caseId);
    }

    @Override
    public ResultVO informIndenty(String caseId) {
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Task currentTask=taskService.createTaskQuery().processInstanceId(pid).singleResult(); // 流程：调节前处理
        Map<String,Object> var=new HashMap<>();
        var.put("paramBeforeMediate",0);
        taskService.complete(currentTask.getId(),var);
        Disputecase disputecase=disputecaseRepository.getOne(caseId);
        for(String s:disputecase.getProposerId().trim().split(",")){
            DisputecaseApply disputecaseApply=disputecaseApplyRepository.getOne(s);
            String name=disputecaseApply.getName();
            String phone=disputecaseApply.getPhone();
            AutoSendUtil.sendSms(caseId,phone,name);
            String email=normalUserRepository.findByIdCard(disputecaseApply.getIdCard()).getEmail();
            if(!(email==null || email==""))
                AutoSendUtil.sendEmail(name,email,caseId);
        }

        return ResultVOUtil.ReturnBack(DisputeProgressEnum.INFORMINDENTY_SUCCESS.getCode(),DisputeProgressEnum.INFORMINDENTY_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getExpertsList() {
        List<Experts> expertsList=expertsRepository.findAll();
        return ResultVOUtil.ReturnBack(expertsList,DisputeProgressEnum.GETEXPERTLIST_SUCCESS.getCode(),DisputeProgressEnum.GETEXPERTLIST_SUCCESS.getMsg());
    }
}
