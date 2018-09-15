package com.seu.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.InitConstant;
import com.seu.converter.Disputecase2DisputeRegisterDetailForm;
import com.seu.domian.*;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.*;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.*;
import com.seu.service.DisputeProgressService;
import com.seu.utils.*;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
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
    @Autowired
    private ConstantDataRepository constantDataRepository;
    @Autowired
    private GetWorkingTimeUtil getWorkingTimeUtil;
    @Autowired
    private VerifyProcessUtil verifyProcessUtil;

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
    public void startProcess(String disputeId) {
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
            return ;
        else{
            disputecaseActivitiRepository.save(disputecaseActiviti);
        }
    }

//    @Transactional
    public void startProcess(String disputeId,Map<String,Object> vars){
        // TODO 判断用户是否有未完成的纠纷流程实例
//        if(runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(starterId).list().size()!=0){
//            return ResultVOUtil.ReturnBack(DisputeProgressEnum.PROCESSINSTANCE_HASEXIST.getCode(),DisputeProgressEnum.PROCESSINSTANCE_HASEXIST.getMsg());
//        }
        //该用户尚未创建流程实例，可以创建
        DisputecaseActiviti disputecaseActiviti=new DisputecaseActiviti();
        if(deployment==null)
            deployment=repositoryService.createDeployment().addClasspathResource("processes/disputeProgress.bpmn")
//                                                       .addClasspathResource("processes/SubProcess1.bpmn")
//                                                       .addClasspathResource("processes/SubProcess2.bpmn")
//                                                       .addClasspathResource("processes/SubProcess3.bpmn")
//                                                       .addClasspathResource("processes/SubProcess4.bpmn")
                                                       .addClasspathResource("processes/SubProcess5.bpmn")
                                                       .addClasspathResource("processes/SubProcess6.bpmn").deploy();

        if(pd==null){
            List<ProcessDefinition> temp=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
            pd=temp.get(0);

        }
        ProcessInstance pi=runtimeService.startProcessInstanceById(pd.getId(),disputeId,vars);
        disputecaseActiviti.setDisputecaseId(disputeId);
        disputecaseActiviti.setProcessId(pi.getId());
        disputecaseActivitiRepository.save(disputecaseActiviti);

    }



    /*
    查询当前任务
     */
    @Override
    public List<Task> searchCurrentTasks(String disputeId) {
        String pid=disputecaseActivitiRepository.getOne(disputeId).getProcessId();
        return taskService.createTaskQuery().processInstanceId(pid).list();
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
            disputeCaseForm.setStatus(disputecaseProcess.getStatus());

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
    public ResultVO getMediationHallData(JSONObject map) {
        /** 根据是否发送案件状态、是否发送调解员id、是否发送起止时间来执行不同的sql方法 */
        Integer size=map.getInteger("size");
        Integer page=map.getInteger("page")-1;
        String filterStatus=map.getString("filterStatus").trim();
//        String filterMediator=map.getString("filterMediator").trim();
        Date startTime=map.getDate("startTime");
        Date endTime=map.getDate("endTime");
        if(endTime!=null){
            // endTime+1
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            endTime=calendar.getTime();
        }
        PageRequest pageRequest=new PageRequest(page,size);
        Page<Disputecase> disputecasePage=null;



        Integer count=0;
        /** 判断组合 */
        if(!StrIsEmptyUtil.isEmpty(filterStatus))
            count++;

        if(startTime!=null)
            count++;
        if(endTime!=null)
            count++;
        switch (count){
            case 0:
                disputecasePage=disputecaseRepository.findAll_HallData(pageRequest);
                break;
            case 1:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    disputecasePage=disputecaseRepository.findWithProcessStatus(filterStatus,pageRequest);
                else if(startTime!=null)
                    disputecasePage=disputecaseRepository.findAfterTime_HallData(startTime,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findBeforeTime_HallData(endTime,pageRequest);
                break;
            case 2:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    if(startTime!=null)
                        disputecasePage=disputecaseRepository.findWithStatusAndAfterTime(filterStatus,startTime,pageRequest);
                    else
                        disputecasePage=disputecaseRepository.findWithStatusAndBeforeTime(filterStatus,endTime,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findBetweenTime_HallData(startTime,endTime,pageRequest);
                break;
            case 3:
                disputecasePage=disputecaseRepository.findWithStatusAndTime(filterStatus,startTime,endTime,pageRequest);
                break;
        }
        Integer totalPages=disputecasePage.getTotalPages();
        List<MediationHallDataForm> mediationHallDataFormList=new ArrayList<>();
        for(Disputecase disputecase:disputecasePage.getContent()){
            String status=disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus();

            MediationHallDataForm mediationHallDataForm=new MediationHallDataForm();
            mediationHallDataForm.setDate(disputecase.getApplyTime());
            mediationHallDataForm.setName(disputecase.getCaseName());
            mediationHallDataForm.setCaseId(disputecase.getId());
            /** 案件进程到process表中查询 */

            mediationHallDataForm.setStatus(status);
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
            if(processList==null)
                continue;
            for (int i = 0; i < processList.size(); ++i) {
                JSONObject jsStr = processList.getJSONObject(i);
                JSONArray jsStr_arr=jsStr.getJSONArray("InvolvedInstitute");
                for(int j=0;j<jsStr_arr.size();++j){
                    JSONObject temppp=jsStr_arr.getJSONObject(j);
                    respondentList.add(temppp.getString("Hospital"));
                }
            }
            mediationHallDataForm.setRespondent(new ArrayList<>(respondentList));


            mediationHallDataFormList.add(mediationHallDataForm);
        }
        Map<String,Object> var=new HashMap<>();
        var.put("mediationHallDataFormList",mediationHallDataFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }


    @Override
    public ResultVO getMyMediationData(JSONObject map) {
        Integer size=map.getInteger("size");
        Integer page=map.getInteger("page")-1;
        String filterStatus=map.getString("filterStatus").trim();
        String filterMediator=map.getString("id").trim();
        Date startTime=map.getDate("startTime");
        Date endTime=map.getDate("endTime");
        if(endTime!=null){
            // endTime+1
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            endTime=calendar.getTime();
        }
        PageRequest pageRequest=new PageRequest(page,size);
        Page<Disputecase> disputecasePage=null;



        /** 根据调解员id号查找所有与他相关的案件 */
        Integer count=0;
        /** 判断组合 */
        if(!StrIsEmptyUtil.isEmpty(filterStatus))
            count++;
        if(startTime!=null)
            count++;
        if(endTime!=null)
            count++;
        switch (count){
            case 0:
                disputecasePage=disputecaseRepository.findByMediatorId(filterMediator,pageRequest);
                break;
            case 1:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    disputecasePage=disputecaseRepository.findWithStatusAndMediator(filterStatus,filterMediator,pageRequest);
                else if(startTime!=null)
                    disputecasePage=disputecaseRepository.findWithMediatorAndAfterTime(filterMediator,startTime,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findWithMediatorAndBeforeTime(filterMediator,endTime,pageRequest);
                break;
            case 2:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    if(startTime!=null)
                        disputecasePage=disputecaseRepository.findWithStatusAndMediatorAndAfterTime(filterStatus,filterMediator,startTime,pageRequest);
                    else
                        disputecasePage=disputecaseRepository.findWithStatusAndMediatorAndBeforeTime(filterStatus,filterMediator,endTime,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findWithMediatorAndTime(filterMediator,startTime,endTime,pageRequest);
                break;
            case 3:
                disputecasePage=disputecaseRepository.findWith4Conditions(filterStatus,filterMediator,startTime,endTime,pageRequest);
                break;
        }

        Integer totalPages=disputecasePage.getTotalPages();
        List<MediationHallDataForm> mediationHallDataFormList=new ArrayList<>();
        for(Disputecase disputecase:disputecasePage.getContent()){
            MediationHallDataForm mediationHallDataForm=new MediationHallDataForm();
            mediationHallDataForm.setDate(disputecase.getApplyTime());
            mediationHallDataForm.setName(disputecase.getCaseName());
            mediationHallDataForm.setCaseId(disputecase.getId());
            /** 案件进程到process表中查询 */
            String status=disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus();
            mediationHallDataForm.setStatus(status);
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
            if(processList==null)
                continue;
            for (int i = 0; i < processList.size(); ++i) {
                JSONObject jsStr = processList.getJSONObject(i);
                JSONArray jsStr_arr=jsStr.getJSONArray("InvolvedInstitute");
                for(int j=0;j<jsStr_arr.size();++j){
                    JSONObject temppp=jsStr_arr.getJSONObject(j);
                    respondentList.add(temppp.getString("Hospital"));
                }
            }
            mediationHallDataForm.setRespondent(new ArrayList<>(respondentList));


            mediationHallDataFormList.add(mediationHallDataForm);
        }
        Map<String,Object> var=new HashMap<>();
        var.put("mediationHallDataFormList",mediationHallDataFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }

    @Override
    @Transactional
    public ResultVO getManagerCaseList(JSONObject map) throws Exception{
        /** 根据是否发送案件状态、是否发送调解员id、是否发送起止时间来执行不同的sql方法 */
        Integer size=map.getInteger("size");
        Integer page=map.getInteger("page")-1;
        String filterStatus=map.getString("filterStatus").trim();
        String filterMediator=map.getString("filterMediator").trim();
        Date startTime=map.getDate("startTime");
        Date endTime=map.getDate("endTime");
        if(endTime!=null){
            // endTime+1
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            endTime=calendar.getTime();
        }
        PageRequest pageRequest=new PageRequest(page,size);
        Page<Disputecase> disputecasePage=null;



        Integer count=0;
        /** 判断组合 */
        if(!StrIsEmptyUtil.isEmpty(filterStatus))
            count++;
        if(!StrIsEmptyUtil.isEmpty(filterMediator))
            count++;
        if(startTime!=null)
            count++;
        if(endTime!=null)
            count++;
        switch (count){
            case 0:
                disputecasePage=disputecaseRepository.findAll(pageRequest);
                break;
            case 1:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    disputecasePage=disputecaseRepository.findWithProcessStatus(filterStatus,pageRequest);
                else if(!StrIsEmptyUtil.isEmpty(filterMediator))
                    disputecasePage=disputecaseRepository.findByMediatorId(filterMediator,pageRequest);
                else if(startTime!=null)
                    disputecasePage=disputecaseRepository.findAfterTime(startTime,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findBeforeTime(endTime,pageRequest);
                break;
            case 2:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    if(!StrIsEmptyUtil.isEmpty(filterMediator))
                        disputecasePage=disputecaseRepository.findWithStatusAndMediator(filterStatus,filterMediator,pageRequest);
                    else if(startTime!=null)
                        disputecasePage=disputecaseRepository.findWithStatusAndAfterTime(filterStatus,startTime,pageRequest);
                    else
                        disputecasePage=disputecaseRepository.findWithStatusAndBeforeTime(filterStatus,endTime,pageRequest);
                else
                    if(!StrIsEmptyUtil.isEmpty(filterMediator))
                        if(startTime!=null)
                            disputecasePage=disputecaseRepository.findWithMediatorAndAfterTime(filterMediator,startTime,pageRequest);
                        else
                            disputecasePage=disputecaseRepository.findWithMediatorAndBeforeTime(filterMediator,endTime,pageRequest);
                    else
                        disputecasePage=disputecaseRepository.findBetweenTime(startTime,endTime,pageRequest);
                break;
            case 3:
                if(endTime==null)
                    disputecasePage=disputecaseRepository.findWithStatusAndMediatorAndAfterTime(filterStatus,filterMediator,startTime,pageRequest);
                else if(startTime==null)
                    disputecasePage=disputecaseRepository.findWithStatusAndMediatorAndBeforeTime(filterStatus,filterMediator,endTime,pageRequest);
                else if(StrIsEmptyUtil.isEmpty(filterMediator))
                    disputecasePage=disputecaseRepository.findWithStatusAndTime(filterStatus,startTime,endTime,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findWithMediatorAndTime(filterMediator,startTime,endTime,pageRequest);
                break;
            case 4:
                disputecasePage=disputecaseRepository.findWith4Conditions(filterStatus,filterMediator,startTime,endTime,pageRequest);
                break;
        }

        Integer totalPages=disputecasePage.getTotalPages();
        List<ManagerCaseForm> managerCaseFormList=new ArrayList<>();
        for(Disputecase disputecase:disputecasePage.getContent()) {
            ManagerCaseForm managerCaseForm = new ManagerCaseForm();
            managerCaseForm.setDate(disputecase.getApplyTime());
            managerCaseForm.setName(disputecase.getCaseName());
            managerCaseForm.setCaseId(disputecase.getId());
            /** 案件进程到process表中查询 */
            String status = disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus();
            managerCaseForm.setStatus(status);
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
            if(processList==null)
                continue;
            for (int i = 0; i < processList.size(); ++i) {
                JSONObject jsStr = processList.getJSONObject(i);
                JSONArray jsStr_arr=jsStr.getJSONArray("InvolvedInstitute");
                for(int j=0;j<jsStr_arr.size();++j){
                    JSONObject temppp=jsStr_arr.getJSONObject(j);
                    respondentList.add(temppp.getString("Hospital"));
                }
//                respondentList.add(jsStr.get("InvolvedInstitute").toString());
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
            String mediatorApply = disputecaseProcess.getApplyStatus();
            String mediatorIntention;
            if(!(userChoose.trim()=="" || userChoose.trim().equals(""))){
                for(String s:userChoose.trim().split(",")){
                    //s是每个用户选择的调解员id号
                    mediatorIntention = "no";
                    for(String m:mediatorApply.trim().split(",")){
                        //m是每个调解员id号
                        if(m == s){
                            mediatorIntention = "yes";
                        }
                    }
                    Mediator mediator=mediatorRepository.findByFatherId(s);
                    managerCaseForm.addUserIntention(mediator.getMediatorName(),mediator.getFatherId(),mediatorIntention);
                }
            }
            managerCaseFormList.add(managerCaseForm);
        }
        Map<String,Object> var=new HashMap<>();
        var.put("CaseList",managerCaseFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }

    @Override
    public ResultVO decideMediatorDisputeCase(String mediator, String caseId) {
        Disputecase disputecase = disputecaseRepository.findOne(caseId);
        disputecase.setMediatorId(mediator);
        Map<String,String> map=new HashMap<>();
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
    public ResultVO setCaseSuccess(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("4");
        return ResultVOUtil.ReturnBack(114,"调解成功");
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
            managerCaseForm.setStatus(status);
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
            if(processList==null)
                continue;
            for (int i = 0; i < processList.size(); ++i) {
                JSONObject jsStr = processList.getJSONObject(i);
                JSONArray jsStr_arr=jsStr.getJSONArray("InvolvedInstitute");
                for(int j=0;j<jsStr_arr.size();++j){
                    JSONObject temppp=jsStr_arr.getJSONObject(j);
                    respondentList.add(temppp.getString("Hospital"));
                }
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
    public ResultVO getMediatorList(String id,Pageable pageable) {
        Page<Mediator> mediatorList=mediatorRepository.findAll(pageable);
        mediatorList.getTotalPages();
        Integer totalPages=mediatorList.getTotalPages();
        List<OneMediatorForm> mediatorFormList=new ArrayList<>();
        for(Mediator mediator:mediatorList.getContent()){
            String name=mediator.getMediatorName();
            String mediatorId=mediator.getFatherId();
            mediatorFormList.add(new OneMediatorForm(name,mediatorId));
        }
        Map<String,Object> var=new HashMap<>();
        var.put("mediatorFormList",mediatorFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETMEDIATORLIST_SUCCESS.getCode(),DisputeProgressEnum.GETMEDIATORLIST_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getNameofAuthorityList(String id, Pageable pageable) {
        Page<Mediator> mediatorList=mediatorRepository.findAll(pageable);
        Integer totalPages=mediatorList.getTotalPages();
        List<MediatorAuthorityForm> mediatorAuthorityFormList=new ArrayList<>();
        for(Mediator mediator:mediatorList.getContent()){
            String name=mediator.getMediatorName();
            String mediatorId=mediator.getFatherId();
            String authority_confirm=mediator.getAuthorityConfirm();
            String authority_judiciary=mediator.getAuthorityJudiciary();
            mediatorAuthorityFormList.add(new MediatorAuthorityForm(name,mediatorId,authority_confirm,authority_judiciary));
        }
        Map<String,Object> var=new HashMap<>();
        var.put("mediatorAuthorityFormList",mediatorAuthorityFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETNAMEOFAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.GETNAMEOFAUTHORITY_SUCCESS.getMsg());
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
    @Transactional
    public ResultVO getMediationStage(String caseId) {
        MediationStageForm mediationStageForm=new MediationStageForm();
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        /** 防止该json字段初始化的时候没有值 */

        if(currentProcess.getMediateStage()==null ||currentProcess.getMediateStage()=="") {
            currentProcess.setMediateStage(InitConstant.init_mediateStage);
            currentProcess=disputecaseProcessRepository.save(currentProcess);
        }


        /** 获取当前调解阶段 */
        JSONObject mediateStage=JSONObject.parseObject(currentProcess.getMediateStage());
        JSONArray stageContent=mediateStage.getJSONArray("stageContent");
        Integer stage=stageContent.size();
        mediationStageForm.setStage(stage);
        JSONObject currentStage=stageContent.getJSONObject(stage-1);
        /** 获取当前步骤0 鉴定中  1 预约中  2调解中，并决定下面那些操作不要做 */
        Integer currentStatus=null;
        /** accessory表查询鉴定结果 */


        /** 是否具备鉴定资格以及是否做过鉴定,先从案件表的金额判断，后从流程图的参数判断 */
        Disputecase disputecase=disputecaseRepository.getOne(caseId);
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        String cm=disputecase.getClaimMoney().trim();
//        ProcessInstance pi=runtimeService.createProcessInstanceQuery().processInstanceId(pid).singleResult();
        if(cm=="2" || cm.equals("2")) {  // 10w以上
            mediationStageForm.setIdentiQualify(true);
//            String temp=pi.getProcessVariables().get("paramAuthenticate").toString().trim();
            String temp=runtimeService.getVariable(pid,"paramAuthenticate").toString().trim();
            if(temp=="0" || temp.equals("0")) {  //尚未做过鉴定
                mediationStageForm.setIdentified(false);
                currentStatus=0;
            }
            else{
                mediationStageForm.setIdentified(true);
                currentStatus=1;
                DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
                JSONObject tempp=JSONObject.parseObject(disputecaseAccessory.getMedicaldamageAssessment());
                if(tempp.get("文本")==null){
                    mediationStageForm.setResultOfIdentify("");
                }else{
                    if(!(tempp.getString("文本").trim()=="" || tempp.getString("文本").trim().equals("")))
                        mediationStageForm.setResultOfIdentify(tempp.getString("文本"));
                    else
                        mediationStageForm.setResultOfIdentify(tempp.getString("文件url"));

                }

            }
        }else{
            mediationStageForm.setIdentified(false);
            mediationStageForm.setIdentiQualify(false);
            currentStatus=1;
        }

        /** 能否预约专家  先看金额，后看是否已预约过*/

        String pP=runtimeService.getVariable(pid,"paramProfesor").toString().trim();
        if((cm!="0"|| !cm.equals("0")) && (pP=="0"||pP.equals("0")))
            mediationStageForm.setExpert(true);
        else
            mediationStageForm.setExpert(false);

        /** applicant`respondent数组 */
            /** 1、申请人 */
//        List<String> app=new ArrayList<>();
        List<String> res=new ArrayList<>();
        JSONArray apps=JSONArray.parseArray("[]");
        for(String s:disputecase.getProposerId().trim().split(",")){
            // name , phone ,email
            DisputecaseApply disputecaseApply=disputecaseApplyRepository.getOne(s);
            String name=disputecaseApply.getName();
            String phone=disputecaseApply.getPhone();
            String specificId=userRepository.findByPhone(phone).getSpecificId();
            String email=normalUserRepository.getOne(specificId).getEmail();
//            String email=normalUserRepository.findByIdCard(disputecaseApply.getIdCard()).get(0).getEmail();
            if(email==null)
                email="";
            JSONObject obj=JSONObject.parseObject("{}");
            obj.put("name",name);
            obj.put("phone",phone);
            obj.put("email",email);
            apps.add(obj);
//            mediationStageForm.addApplicants(name,phone,email);
        }
        mediationStageForm.setApplicants(apps.toString());
            /** 2、院方 */
        res=GetHospitalUtil.extract(disputecase.getMedicalProcess());
        JSONObject hosJS=JSONObject.parseObject(constantDataRepository.findByName("hospital_list").getData());
        JSONArray respo=JSONArray.parseArray("[]");
        for(String hos:res){
            JSONObject hosOne=hosJS.getJSONObject(hos.trim());
            String phone=hosOne.getString("phone");
            String email=hosOne.getString("email");
            if(email==null)
                email="";
            JSONObject obj=JSONObject.parseObject("{}");
            obj.put("name",hos);
            obj.put("phone",phone);
            obj.put("email",email);
            respo.add(obj);
        }
        mediationStageForm.setRespondents(respo.toString());
        // 直接取出来即可
        String currentStageContent=currentStage.toString();
        mediationStageForm.setCurrentStageContent(currentStageContent);
        /** currentStageContent */
        String tmp=currentStage.getJSONObject("particopateContact").getString("mediationPlace").trim();
        if(tmp=="" || tmp.equals("")) // 说明页面2没有填
            currentStatus=1;
        else
            currentStatus=2;

        mediationStageForm.setCurrentStatus(currentStatus);
        return ResultVOUtil.ReturnBack(mediationStageForm,DisputeProgressEnum.GETMEDIATIONSTAGE_SUCCESS.getCode(),DisputeProgressEnum.GETMEDIATIONSTAGE_SUCCESS.getMsg());

    }

    @Override
    @Transactional
    public ResultVO setResultOfIndent(String caseId, String resultOfIndent) {

        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        if(StrIsEmptyUtil.isEmpty(disputecaseAccessory.getMedicaldamageAssessment()))
            disputecaseAccessory.setMedicaldamageAssessment("{}");
        JSONObject md=JSONObject.parseObject(disputecaseAccessory.getMedicaldamageAssessment());
        md.put("文本",resultOfIndent);
        // TODO 发送文件情况
        disputecaseAccessory.setMedicaldamageAssessment(md.toString());
        disputecaseAccessory=disputecaseAccessoryRepository.save(disputecaseAccessory);

        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        runtimeService.setVariable(pid,"paramAuthenticate","1");

        /** 目前处于主流程:损害/医疗鉴定 */
        Task task=null;
        List<Task> tasks=verifyProcessUtil.verifyTask(caseId,"损害/医疗鉴定");
        for(Task one:tasks)
            if(one.getName().equals("损害/医疗鉴定")){
                task=one;
                break;
            }
        taskService.complete(task.getId());  // 会进去到流程 调解前处理

        return ResultVOUtil.ReturnBack(DisputeProgressEnum.SETRESULTOFINDENT_SUCCESS.getCode(),DisputeProgressEnum.SETRESULTOFINDENT_SUCCESS.getMsg());

    }

    @Override
    @Transactional
    public ResultVO setAppoint(String caseId, String currentStageContent) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        JSONObject mediateStage=JSONObject.parseObject(disputecaseProcess.getMediateStage());
        /** 判断当前阶段做到哪了
         * 有3种情况：
         * 1、医疗鉴定
         * 2、预约调解
         * 3、正在调解
         * */
        Integer stage=mediateStage.getInteger("stage");
        Integer currentStatus=mediateStage.getInteger("currentStatus");
        JSONArray stageContent=mediateStage.getJSONArray("stageContent");
        if(stage>0)
            stageContent.remove(stage-1);
        stageContent.add(JSONObject.parseObject(currentStageContent));
        mediateStage.put("stageContent",stageContent);
        disputecaseProcess.setMediateStage(mediateStage.toString());
        disputecaseProcessRepository.save(disputecaseProcess);

        /**
         * 当前步骤为 调解前处理
         * 1、设置paramBeforeMediate=1
         *         appointResult 参数 ： 0为三方 1为专家
         * 2、流程往下走一格
         * 3、自动发送预约消息
         */
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Integer result=0;
        String particopate=JSONObject.parseObject(currentStageContent).getString("particopate");
        Pattern pattern=Pattern.compile("专家");
        Matcher matcher=pattern.matcher(particopate);
        if(matcher.find())
            result=1;
        Map<String ,Object> var=new HashMap<>();
        var.put("paramBeforeMediate",1);
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
    @Transactional
    public ResultVO informIndenty(String caseId) {
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        List<Task> tasks=verifyProcessUtil.verifyTask(caseId,"调解前处理");
        Task currentTask=null;
        for(Task taskOne:tasks)
            if(taskOne.getName().equals("调解前处理")){
                currentTask=taskOne;
                break;
            }
//        Task currentTask=taskService.createTaskQuery().processInstanceId(pid).singleResult(); // 流程：调节前处理
        Map<String,Object> var=new HashMap<>();
        var.put("paramBeforeMediate",0);
        taskService.complete(currentTask.getId(),var);
        Disputecase disputecase=disputecaseRepository.getOne(caseId);
        for(String s:disputecase.getProposerId().trim().split(",")){
            DisputecaseApply disputecaseApply=disputecaseApplyRepository.getOne(s);
            String name=disputecaseApply.getName();
            String phone=disputecaseApply.getPhone();
            String specificId=userRepository.findByPhone(phone).getSpecificId();
            AutoSendUtil.sendSms(caseId,phone,name);
            String email=normalUserRepository.getOne(specificId).getEmail();
            if(!(email==null || email==""))
                AutoSendUtil.sendEmail(name,email,caseId);
        }
        /** 挂起流程，自动累加结束时间 */
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        currentProcess.setIsSuspended(1);
        disputecaseProcessRepository.save(currentProcess);

        return ResultVOUtil.ReturnBack(DisputeProgressEnum.INFORMINDENTY_SUCCESS.getCode(),DisputeProgressEnum.INFORMINDENTY_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getExpertsList() {
        List<Experts> expertsList=expertsRepository.findAll();
        return ResultVOUtil.ReturnBack(expertsList,DisputeProgressEnum.GETEXPERTLIST_SUCCESS.getCode(),DisputeProgressEnum.GETEXPERTLIST_SUCCESS.getMsg());
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResultVO getUserCaseList(String userId) {
        NormalUser normalUser = normalUserRepository.findByFatherId(userId);
        User user=userRepository.getOne(userId);
        List<DisputecaseApply> disputecaseApplyList = disputecaseApplyRepository.findAllByPhone(user.getPhone());
//        List<DisputecaseApply> disputecaseApplyList = disputecaseApplyRepository.findAllByIdCard(normalUser.getIdCard());

        List<UserCaseListForm> userCaseListFormList = new ArrayList<>();

        for (DisputecaseApply disputecaseApply: disputecaseApplyList){
            Disputecase disputecase = disputecaseRepository.findOne(disputecaseApply.getDisputecaseId());
            UserCaseListForm userCaseListForm = new UserCaseListForm();

            String[] temp=disputecase.getProposerId().trim().split(",");
            String applicant="";
            for(String s:temp){
                applicant = (disputecaseApplyRepository.getOne(s).getName())+"、";
            }

            applicant = applicant.substring(0, applicant.length() - 1);
            userCaseListForm.setApplicant(applicant);
            if(disputecase.getMediatorId()!=null)
            {
                userCaseListForm.setCurrentMedator(mediatorRepository.findByFatherId(disputecase.getMediatorId()).getMediatorName());
                userCaseListForm.setMediatorId(disputecase.getMediatorId());
            }

            else{
                userCaseListForm.setCurrentMedator("未选择");
                userCaseListForm.setMediatorId("");
            }

            userCaseListForm.setName(disputecase.getCaseName());
            userCaseListForm.setDate(disputecase.getApplyTime());

            userCaseListForm.setNameId(disputecase.getId());


            com.alibaba.fastjson.JSONArray arr= com.alibaba.fastjson.JSONArray.parseArray(disputecase.getMedicalProcess());

            List<String> hospitalList = new ArrayList<>();
            String hospitals = "";

            for (Object stage:arr){
                Object involvedInstitute = ((com.alibaba.fastjson.JSONObject) stage).get("InvolvedInstitute");

                for(Object hospital: (com.alibaba.fastjson.JSONArray)involvedInstitute){

                    hospitalList.add((String)(((com.alibaba.fastjson.JSONObject)hospital).get("Hospital")));
                }
            }

            for(String hospital: hospitalList){
                hospitals = hospitals + hospital + "、";
            }
            hospitals = hospitals.substring(0,hospitals.length() - 1);

            userCaseListForm.setRespondent(hospitals);
            userCaseListForm.setStatus(disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus());
            userCaseListFormList.add(userCaseListForm);
        }


        return ResultVOUtil.ReturnBack(userCaseListFormList, 111,"用户中心获取用户案件列表成功。");
    }

    @Override
    public ResultVO getAuthority(String id) {
        Mediator mediator=mediatorRepository.findByFatherId(id);
        String authorityConfirm=mediator.getAuthorityConfirm();
        String authorityJudiciary=mediator.getAuthorityJudiciary();
        Map<String,Object> var=new HashMap<>();
        var.put("authorityConfirm",authorityConfirm);
        var.put("authorityJudiciary",authorityJudiciary);
        return  ResultVOUtil.ReturnBack(var,DisputeProgressEnum.MEDIATORGETAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.MEDIATORGETAUTHORITY_SUCCESS.getMsg());

    }

    @Override
    public void updateCaseStatus(String caseId, String status) {
        DisputecaseProcess dp=disputecaseProcessRepository.findByDisputecaseId(caseId);
        dp.setStatus(status);
        disputecaseProcessRepository.save(dp);
    }

    @Override
    public ResultVO getUserChooseMediator(JSONObject map) {
        String caseId=map.getString("caseId");
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        String userChoose=disputecaseProcess.getUserChoose();
        if(userChoose==null || userChoose.trim()=="" || userChoose.trim().equals("")){
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.GETUSERCHOOSE_NONE.getCode(),DisputeProgressEnum.GETUSERCHOOSE_NONE.getMsg());
        }else {
            String[] chooseList=userChoose.trim().split(",");
            Set<String> result=new HashSet<>();
            for(String s:chooseList)
                result.add(s.trim());
            /** 剔除申请回避的调解员id */
            String avoidStatus=disputecaseProcess.getAvoidStatus().trim();
            if(!(avoidStatus==null||avoidStatus==""||avoidStatus.equals(""))){
                for(String s:avoidStatus.split(","))
                    result.remove(s);
            }
            List<OneMediatorForm> mediatorFormList=new ArrayList<>();
            for(String s:result){
                Mediator mediator=mediatorRepository.findByFatherId(s.trim());
                String name=mediator.getMediatorName();
                String mediatorId=mediator.getFatherId();
                mediatorFormList.add(new OneMediatorForm(name,mediatorId));
            }
            return ResultVOUtil.ReturnBack(mediatorFormList,DisputeProgressEnum.GETUSERCHOOSE_SUCCESS.getCode(),DisputeProgressEnum.GETUSERCHOOSE_SUCCESS.getMsg());
        }
    }

    @Override
    public ResultVO getAdditionalAllocation(String caseId, Pageable pageRequest) {
        /** 先获取用户意向 */
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        String userChoose=disputecaseProcess.getUserChoose().trim();
        if(userChoose==null)
            userChoose="";
        /** 获取回避信息 */
        String avoidStatus=disputecaseProcess.getAvoidStatus().trim();
        if(avoidStatus==null)
            avoidStatus="";
        Page<Mediator> mediatorPage=mediatorRepository.findAllWithoutUserChooseAndAvoid(userChoose,avoidStatus,pageRequest);
        Integer totalPages=mediatorPage.getTotalPages();
        List<OneMediatorForm> mediatorFormList=new ArrayList<>();
        for(Mediator mediator:mediatorPage.getContent()){
            String name=mediator.getMediatorName();
            String mediatorId=mediator.getFatherId();
            mediatorFormList.add(new OneMediatorForm(name,mediatorId));
        }
        Map<String,Object> var=new HashMap<>();
        var.put("mediatorFormList",mediatorFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETADDITIONALALLOCATION_SUCCESS.getCode(),DisputeProgressEnum.GETADDITIONALALLOCATION_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getAllMediator() {
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
    public void setStartTimeAndEndTime(String caseId) throws Exception{
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        Date currentDate=new Date();
        currentProcess.setStartimeDisputecase(currentDate);
        Date endDate= getWorkingTimeUtil.calWorkingTime(currentDate,30);
        currentProcess.setEndtimeDisputecase(endDate);
        disputecaseProcessRepository.save(currentProcess);
    }
}
