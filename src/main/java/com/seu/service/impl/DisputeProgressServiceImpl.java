package com.seu.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.EndDate;
import com.seu.common.InitConstant;
import com.seu.converter.Disputecase2DisputeRegisterDetailForm;
import com.seu.domian.*;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.*;
import com.seu.form.VOForm.*;
import com.seu.repository.*;
import com.seu.service.DisputeProgressService;
import com.seu.service.DisputecaseAccessoryService;
import com.seu.service.webServiceTask.MediateInform;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


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
    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;
    @Autowired
    private ContactListRepository contactListRepository;

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
        disputecaseProcess.setAvoidStatus(avoidStatus); //todo:  ???
        disputecaseProcessRepository.save(disputecaseProcess);
    }

    @Override
    public void updateUserChoose(String disputeId, String mediatorList) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(disputeId);
        String oldUserChoose=disputecaseProcess.getUserChoose();
        if(!oldUserChoose.isEmpty()){
            oldUserChoose = oldUserChoose + ",";
        }
        oldUserChoose+=mediatorList;
        disputecaseProcess.setUserChoose(oldUserChoose);
        disputecaseProcessRepository.save(disputecaseProcess);
    }


    @Override
    public ResultVO getMediationHallData(JSONObject map) {
        /** 根据是否发送案件状态、是否发送调解员id、是否发送起止时间来执行不同的sql方法 */

        //id todo
        String id = map.getString("id");

        Integer size=map.getInteger("size");
        Integer page=map.getInteger("page")-1;
        String filterStatus=map.getString("filterStatus").trim();
//        String filterMediator=map.getString("filterMediator").trim();
        Date startTime=map.getDate("startTime");
        Date endTime=map.getDate("endTime");

        String authorityConfirm = map.getString("authorityConfirm");
        String province=map.getString("province");
        String city=map.getString("city");
        String mediateCenter=map.getString("mediate_center");
        if(province==null)
            province="%";
        if(city==null)
            city="%";
        if(mediateCenter==null)
            mediateCenter="%";



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
                if(authorityConfirm.equals("1"))
                    disputecasePage=disputecaseRepository.findAll_HallData(province,city,mediateCenter,pageRequest);
                if(authorityConfirm.equals("0"))
                    disputecasePage= disputecaseRepository.findAll_HallData_ByLow(province,city,mediateCenter,pageRequest);
                break;
            case 1:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    disputecasePage=disputecaseRepository.findWithProcessStatus(filterStatus,province,city,mediateCenter,pageRequest);
                else if(startTime!=null)
                    disputecasePage=disputecaseRepository.findAfterTime_HallData(startTime,province,city,mediateCenter,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findBeforeTime_HallData(endTime,province,city,mediateCenter,pageRequest);
                break;
            case 2:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    if(startTime!=null)
                        disputecasePage=disputecaseRepository.findWithStatusAndAfterTime(filterStatus,startTime,province,city,mediateCenter,pageRequest);
                    else
                        disputecasePage=disputecaseRepository.findWithStatusAndBeforeTime(filterStatus,endTime,province,city,mediateCenter,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findBetweenTime_HallData(startTime,endTime,province,city,mediateCenter,pageRequest);
                break;
            case 3:
                disputecasePage=disputecaseRepository.findWithStatusAndTime(filterStatus,startTime,endTime,province,city,mediateCenter,pageRequest);
                break;
        }
        Integer totalPages=disputecasePage.getTotalPages();
        List<MediationHallDataForm> mediationHallDataFormList=new ArrayList<>();
        ArrayList<Disputecase> templist=new ArrayList<>();
        for(Disputecase dp:disputecasePage.getContent())
            templist.add(dp);
        Collections.sort(templist, new Comparator<Disputecase>() {
            @Override
            public int compare(Disputecase o1, Disputecase o2) {
                try{
                    Date dt1 = o1.getApplyTime();
                    Date dt2 = o2.getApplyTime();
                    if (dt1.getTime() > dt2.getTime()) {
                        return -1;
                    } else if (dt1.getTime() < dt2.getTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return 0;
            }
        });
        for(Disputecase disputecase:templist){
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
                JSONObject jsStr_arr=jsStr.getJSONObject("InvolvedInstitute");//涉事医院修改 10/13 wj
                respondentList.add(jsStr_arr.getString("Hospital"));
            }
            mediationHallDataForm.setRespondent(new ArrayList<>(respondentList));

            DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputecase.getId());

            mediationHallDataForm.setUserUpload(JSONArray.parseArray(disputecaseAccessory.getUserUpload()));

            mediationHallDataForm.setCaseLevel(disputecase.getLevel());

            mediationHallDataFormList.add(mediationHallDataForm);

            //当前人是否回避words=str.trim().split(",");todo:test

            List<String> avoidId= Arrays.asList(disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getAvoidStatus().trim().split(","));
            mediationHallDataForm.setFordebarb("0");
            for (int i = 0; i<avoidId.size(); i++){
                if (avoidId.get(i).equals(id)){
                    mediationHallDataForm.setFordebarb("1");
                }
            }


        }
        Map<String,Object> var=new HashMap<>();
        var.put("mediationHallDataFormList",mediationHallDataFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }


    @Override
    public ResultVO getMyMediationData(JSONObject map) throws Exception{
        Integer size=map.getInteger("size");
        Integer page=map.getInteger("page")-1;
        String filterStatus=map.getString("filterStatus").trim();
        String filterMediator=map.getString("id").trim();
        Date startTime=map.getDate("startTime");
        Date endTime=map.getDate("endTime");
        String province=map.getString("province");
        String city=map.getString("city");
        String mediateCenter=map.getString("mediate_center");
        if(province==null)
            province="%";
        if(city==null)
            city="%";
        if(mediateCenter==null)
            mediateCenter="%";

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
                disputecasePage=disputecaseRepository.findByMediatorId(filterMediator,province,city,mediateCenter,pageRequest);
                break;
            case 1:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    disputecasePage=disputecaseRepository.findWithStatusAndMediator(filterStatus,filterMediator,province,city,mediateCenter,pageRequest);
                else if(startTime!=null)
                    disputecasePage=disputecaseRepository.findWithMediatorAndAfterTime(filterMediator,startTime,province,city,mediateCenter,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findWithMediatorAndBeforeTime(filterMediator,endTime,province,city,mediateCenter,pageRequest);
                break;
            case 2:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    if(startTime!=null)
                        disputecasePage=disputecaseRepository.findWithStatusAndMediatorAndAfterTime(filterStatus,filterMediator,startTime,province,city,mediateCenter,pageRequest);
                    else
                        disputecasePage=disputecaseRepository.findWithStatusAndMediatorAndBeforeTime(filterStatus,filterMediator,endTime,province,city,mediateCenter,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findWithMediatorAndTime(filterMediator,startTime,endTime,province,city,mediateCenter,pageRequest);
                break;
            case 3:
                disputecasePage=disputecaseRepository.findWith4Conditions(filterStatus,filterMediator,startTime,endTime,province,city,mediateCenter,pageRequest);
                break;
        }

        Integer totalPages=disputecasePage.getTotalPages();
        List<MediationHallDataForm> mediationHallDataFormList=new ArrayList<>();
        List<Disputecase> templist=new ArrayList<>();
        for(Disputecase dp:disputecasePage.getContent())
            templist.add(dp);
        Collections.sort(templist, new Comparator<Disputecase>() {
            @Override
            public int compare(Disputecase o1, Disputecase o2) {
                try{
                    Date dt1 = o1.getApplyTime();
                    Date dt2 = o2.getApplyTime();
                    if (dt1.getTime() > dt2.getTime()) {
                        return -1;
                    } else if (dt1.getTime() < dt2.getTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return 0;
            }
        });

        for(Disputecase disputecase:templist){
            MediationHallDataForm mediationHallDataForm=new MediationHallDataForm();
            mediationHallDataForm.setDate(disputecase.getApplyTime());
            mediationHallDataForm.setName(disputecase.getCaseName());
            mediationHallDataForm.setCaseId(disputecase.getId());

            mediationHallDataForm.setCountdown(getWorkingTimeUtil.calRemainTime(disputecase.getId()));
            //todo:倒计时
            //mediationHallDataForm.setCountdown(null);


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
                JSONObject jsStr_arr=jsStr.getJSONObject("InvolvedInstitute");//  18/10/15 wj 涉事医院 todo:test

                    respondentList.add(jsStr_arr.getString("Hospital"));

            }
            mediationHallDataForm.setRespondent(new ArrayList<>(respondentList));


            DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputecase.getId());
            if(disputecaseAccessory.getProtocal()!=null)
                mediationHallDataForm.setProtocal(true);

            mediationHallDataForm.setUserUpload(JSONArray.parseArray(disputecaseAccessory.getUserUpload()));

            mediationHallDataFormList.add(mediationHallDataForm);
        }
        Map<String,Object> var=new HashMap<>();
        var.put("mediationHallDataFormList",mediationHallDataFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getCode(),DisputeProgressEnum.GETMYMEDIATIONDATA_SUCCESS.getMsg());
    }

    @Override
    @Transactional
    public ResultVO getManagerCaseList(JSONObject map) throws Exception{ //todo: 添加过滤
        /** 根据是否发送案件状态、是否发送调解员id、是否发送起止时间来执行不同的sql方法 */
        Integer size=map.getInteger("size");
        Integer page=map.getInteger("page")-1;
        String filterStatus=map.getString("filterStatus").trim();
        String filterMediator=map.getString("filterMediator").trim();
        Date startTime=map.getDate("startTime");
        Date endTime=map.getDate("endTime");
        String province=map.getString("province");
        String city=map.getString("city");
        String mediateCenter=map.getString("mediate_center");
        if(province==null || province.equals(""))
            province="%";
        if(city==null || city.equals(""))
            city="%";
        if(mediateCenter==null || mediateCenter.equals(""))
            mediateCenter="%";


        if(endTime!=null){
            // endTime+1
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            endTime=calendar.getTime();
        }
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        PageRequest pageRequest1=new PageRequest(page,size,sort);
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
                disputecasePage=disputecaseRepository.findAllWithDesc(province,city,mediateCenter,pageRequest);
                break;
            case 1:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    disputecasePage=disputecaseRepository.findWithProcessStatus(filterStatus,province,city,mediateCenter,pageRequest);
                else if(!StrIsEmptyUtil.isEmpty(filterMediator))
                    disputecasePage=disputecaseRepository.findByMediatorId(filterMediator,province,city,mediateCenter,pageRequest);
                else if(startTime!=null)
                    disputecasePage=disputecaseRepository.findAfterTime(startTime,province,city,mediateCenter,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findBeforeTime(endTime,province,city,mediateCenter,pageRequest);
                break;
            case 2:
                if(!StrIsEmptyUtil.isEmpty(filterStatus))
                    if(!StrIsEmptyUtil.isEmpty(filterMediator))
                        disputecasePage=disputecaseRepository.findWithStatusAndMediator(filterStatus,filterMediator,province,city,mediateCenter,pageRequest);
                    else if(startTime!=null)
                        disputecasePage=disputecaseRepository.findWithStatusAndAfterTime(filterStatus,startTime,province,city,mediateCenter,pageRequest);
                    else
                        disputecasePage=disputecaseRepository.findWithStatusAndBeforeTime(filterStatus,endTime,province,city,mediateCenter,pageRequest);
                else
                    if(!StrIsEmptyUtil.isEmpty(filterMediator))
                        if(startTime!=null)
                            disputecasePage=disputecaseRepository.findWithMediatorAndAfterTime(filterMediator,startTime,province,city,mediateCenter,pageRequest);
                        else
                            disputecasePage=disputecaseRepository.findWithMediatorAndBeforeTime(filterMediator,endTime,province,city,mediateCenter,pageRequest);
                    else
                        disputecasePage=disputecaseRepository.findBetweenTime(startTime,endTime,province,city,mediateCenter,pageRequest);
                break;
            case 3:
                if(endTime==null)
                    disputecasePage=disputecaseRepository.findWithStatusAndMediatorAndAfterTime(filterStatus,filterMediator,startTime,province,city,mediateCenter,pageRequest);
                else if(startTime==null)
                    disputecasePage=disputecaseRepository.findWithStatusAndMediatorAndBeforeTime(filterStatus,filterMediator,endTime,province,city,mediateCenter,pageRequest);
                else if(StrIsEmptyUtil.isEmpty(filterMediator))
                    disputecasePage=disputecaseRepository.findWithStatusAndTime(filterStatus,startTime,endTime,province,city,mediateCenter,pageRequest);
                else
                    disputecasePage=disputecaseRepository.findWithMediatorAndTime(filterMediator,startTime,endTime,province,city,mediateCenter,pageRequest);
                break;
            case 4:
                disputecasePage=disputecaseRepository.findWith4Conditions(filterStatus,filterMediator,startTime,endTime,province,city,mediateCenter,pageRequest);
                break;
        }

        Integer totalPages=disputecasePage.getTotalPages();
        List<ManagerCaseForm> managerCaseFormList=new ArrayList<>();
        for(Disputecase disputecase:disputecasePage.getContent()) {
            ManagerCaseForm managerCaseForm = new ManagerCaseForm();
            managerCaseForm.setDate(disputecase.getApplyTime());
            managerCaseForm.setName(disputecase.getCaseName());
            managerCaseForm.setCaseId(disputecase.getId());


            managerCaseForm.setCountdown(getWorkingTimeUtil.calRemainTime(disputecase.getId()));
            //todo:倒计时
            //managerCaseForm.setCountdown(null);


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
                JSONObject jsStr_arr=jsStr.getJSONObject("InvolvedInstitute");

                respondentList.add(jsStr_arr.getString("Hospital"));

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
            managerCaseForm.setCaseLevel(disputecase.getLevel());
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

            String avoidStatus = disputecaseProcess.getAvoidStatus();
            if(!(avoidStatus.trim()=="" || avoidStatus.trim().equals(""))){
                for(String s:avoidStatus.trim().split(",")){

                    Mediator mediator=mediatorRepository.findByFatherId(s);
                    managerCaseForm.addUserIntention(mediator.getMediatorName(),mediator.getFatherId(),"no");
                }
            }

            DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(disputecase.getId());

            managerCaseForm.setUserUpload(JSONArray.parseArray(disputecaseAccessory.getUserUpload()));
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
        disputecaseProcessRepository.save(disputecaseProcess);
        return ResultVOUtil.ReturnBack(113,"调解失败");
    }

    @Override
    public ResultVO setCaseSuccess(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("4");
        disputecaseProcessRepository.save(disputecaseProcess);
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
                JSONObject jsStr_arr=jsStr.getJSONObject("InvolvedInstitute"); //涉事医院 todo:test
                respondentList.add(jsStr_arr.getString("Hospital"));

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
    public ResultVO getMediatorList(String province, String city, String mediate_center,String id,Pageable pageable) {
        if(province == null)
            province="";
        if(city == null)
            city = "";
        if(mediate_center == null)
            mediate_center = "";

        Page<Mediator> mediatorList=mediatorRepository.findAllByMediatorCenter(province, city, mediate_center, pageable);
        mediatorList.getTotalPages();
        Integer totalPages=mediatorList.getTotalPages();
        List<OneMediatorForm> mediatorFormList=new ArrayList<>();
        for(Mediator mediator:mediatorList.getContent()){
            String name=mediator.getMediatorName();
            String mediatorId=mediator.getFatherId();
            mediatorFormList.add(new OneMediatorForm(name,mediatorId,mediator.getMediateCenter(),mediator.getBasicInformation(),"http://"+mediator.getAvatar()));
        }
        Map<String,Object> var=new HashMap<>();
        var.put("mediatorFormList",mediatorFormList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETMEDIATORLIST_SUCCESS.getCode(),DisputeProgressEnum.GETMEDIATORLIST_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getNameofAuthorityList(JSONObject map) {
        String id = map.getString("id");
        Integer page=map.getInteger("page")-1;  // 前端从一开始，后台从0开始
        Integer size=map.getInteger("size");
        String filterType=map.getString("filterType");
        String filterType2=map.getString("filterType2");
        String province = map.getString("province");
        String city = map.getString("city");
        String mediateCenter = map.getString("mediate_center");

        if (province==null)
            province="";
        if (city==null)
            city="";
        if (mediateCenter==null)
            mediateCenter="";

        PageRequest pageRequest=new PageRequest(page,size);
        Page<Mediator> mediatorList=null;
        if(!StrIsEmptyUtil.isEmpty(filterType) && !StrIsEmptyUtil.isEmpty(filterType2))
            mediatorList=mediatorRepository.findAllByAuthorityConfirmAndAuthorityJudiciaryByMediatorCenter(filterType,filterType2,province,city,mediateCenter,pageRequest);
        else if(!StrIsEmptyUtil.isEmpty(filterType))
            mediatorList=mediatorRepository.findAllByAuthorityConfirmByMediatorCenter(filterType,province,city,mediateCenter,pageRequest);
        else if(!StrIsEmptyUtil.isEmpty(filterType2))
            mediatorList=mediatorRepository.findAllByAuthorityJudiciaryByMediatorCenter(filterType2,province,city,mediateCenter,pageRequest);
        else
            mediatorList=mediatorRepository.findAllByMediatorCenter(province,city,mediateCenter,pageRequest);

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
    public ResultVO changeAuthorityNameList(JSONObject map) {
        log.info("进入了修改调解员权限界面1");
        Boolean authority1=map.getBoolean("authority1");
        Boolean authority2=map.getBoolean("authority2");
        String mediatorId=map.getString("mediatorId");
        log.info("进入了修改调解员权限界面2");
        Mediator mediator=mediatorRepository.findByFatherId(mediatorId);
        if(authority1==true)
            mediator.setAuthorityConfirm("1");
        else if(authority2==false)
            mediator.setAuthorityConfirm("0");
        if(authority2==true)
            mediator.setAuthorityJudiciary("1");
        else if(authority2==false)
            mediator.setAuthorityJudiciary("0");
        mediatorRepository.save(mediator);
//        JSONArray jsArr=JSONArray.parseArray(changeAuthorityFormList);
//        int len=jsArr.size();
//        for(int i=0;i<len;++i){
//            JSONObject jsobj=jsArr.getJSONObject(i);
//            String id=jsobj.getString("nameid");
//            String authority1="0";
//            if(jsobj.get("authority1").toString().trim().equals("true"))
//                authority1="1";
//            String authority2="0";
//            if(jsobj.get("authority2").toString().trim().equals("true"))
//                authority2="1";
//            Mediator mediator=mediatorRepository.findByFatherId(id);
//            mediator.setAuthorityConfirm(authority1);
//            mediator.setAuthorityJudiciary(authority2);
//            mediatorRepository.save(mediator);
//        }
        return  ResultVOUtil.ReturnBack(DisputeProgressEnum.CHANGEMEDIATORAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.CHANGEMEDIATORAUTHORITY_SUCCESS.getMsg());
    }

    @Override
    @Transactional
    public ResultVO getMediationStage(String caseId) {
        caseId=caseId.trim();
        MediationStageForm mediationStageForm=new MediationStageForm();
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        /** 防止该json字段初始化的时候没有值 */

        if(StrIsEmptyUtil.isEmpty(currentProcess.getMediateStage())) {
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
        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        String cm=disputecase.getClaimMoney().trim();
        if(cm=="2" || cm.equals("2") || cm=="1" || cm.equals("1")) {  // 10w以上
            mediationStageForm.setIdentiQualify(true);
            Integer temp= Integer.parseInt(runtimeService.getVariable(pid,"paramAuthenticate").toString());
            if(temp==0) {  //尚未做过鉴定
                mediationStageForm.setIdentified(false);
                currentStatus=0;
            }
            else{
                mediationStageForm.setIdentified(true);
                currentStatus=1;
//                mediationStageForm.setResultOfIdentify(disputecaseAccessory.getMedicaldamageAssessment());
                //JSONObject tempp=JSONObject.parseObject(disputecaseAccessory.getMedicaldamageAssessment());
            }
        }else{
            mediationStageForm.setIdentified(false);
            mediationStageForm.setIdentiQualify(false);
            currentStatus=1;
        }
        /** 能否预约专家,  先看金额，后看是否已预约过*/
        if(runtimeService.getVariable(pid,"paramProfesor")==null || (runtimeService.getVariable(pid,"paramProfesor")).equals("0"))
            runtimeService.setVariable(pid,"paramProfesor",0);
        Integer pP= Integer.parseInt(runtimeService.getVariable(pid,"paramProfesor").toString());
        if(pP==0)
            mediationStageForm.setExpertAppoint("0");
        if(pP==1)
            mediationStageForm.setExpertAppoint("1");
        if(pP==2)
            mediationStageForm.setExpertAppoint("2");
        //if((cm!="0"|| !cm.equals("0")))
        if((!cm.equals("0")))
            mediationStageForm.setExpert(true);
        else
            mediationStageForm.setExpert(false);

        /** applicant`respondent数组 */
            /** 1、申请人.如果 mediateStage中数据为空则读表该数据*/
        JSONArray applicants=mediateStage.getJSONArray("applicants");
        if(applicants.isEmpty()){
            JSONArray apps=JSONArray.parseArray("[]");
            for(String s:disputecase.getProposerId().trim().split(",")){
                // name , phone ,email
                DisputecaseApply disputecaseApply=disputecaseApplyRepository.getOne(s);
                String name=disputecaseApply.getName();
                String phone=disputecaseApply.getPhone();
                String specificId=userRepository.findByPhone(phone).getSpecificId();
                String email=normalUserRepository.getOne(specificId).getEmail();
                if(email==null)
                    email="";
                JSONObject obj=JSONObject.parseObject("{}");
                obj.put("name",name);
                obj.put("phone",phone);
                obj.put("email",email);
                apps.add(obj);
            }
            mediationStageForm.setApplicants(apps);
        }else
            mediationStageForm.setApplicants(applicants);
            /** 2、院方,如果 mediateStage中数据为空则读表该数据 */
        JSONArray respondents=mediateStage.getJSONArray("respondents");
        if(respondents.isEmpty()){
            List<String> res=new ArrayList<>();
            res=GetHospitalUtil.extract(disputecase.getMedicalProcess());//todo:涉事医院
//            JSONObject hosJS=JSONObject.parseObject(constantDataRepository.findByName("hospital_list").getData());//todo:市 区



            JSONArray respo=JSONArray.parseArray("[]");
            for(String hos:res){
                List<ContactList> contactList = contactListRepository.findByName(hos);
                if(contactList.size()==0 || contactList==null){
                    JSONObject obj=JSONObject.parseObject("{}");
                    obj.put("name","联系人");
                    obj.put("phone","1258012580");
                    respo.add(obj);
                }else{
                    ContactList contact = contactList.get(0);
                    String phone=contact.getTele();
                    if(phone==null)
                        phone="暂缺";
                    JSONObject obj=JSONObject.parseObject("{}");
                    obj.put("name",hos);
                    obj.put("phone",phone);
                    respo.add(obj);
                }
                if(contactList.size()==0 || contactList==null){
                    JSONObject obj=JSONObject.parseObject("{}");
                    obj.put("name","暂缺");
                    obj.put("phone","暂缺");
                    respo.add(obj);
                }else{
                    ContactList contact = contactList.get(0);
                    String phone=contact.getTele();
                    if(phone==null)
                        phone="";
                    JSONObject obj=JSONObject.parseObject("{}");
                    obj.put("name",hos);
                    obj.put("phone",phone);
                    respo.add(obj);
                }
            }
            mediationStageForm.setRespondents(respo);
        }else
            mediationStageForm.setRespondents(respondents);
        /**  stageContent直接取出来 */
//        String currentStageContent=currentStage.toString();
        JSONObject currentStageContent=JSONObject.parseObject(currentStage.toString());


        //温要求修改
        if(StrIsEmptyUtil.isEmpty(disputecaseAccessory.getMedicaldamageAssessment())){

            JSONObject jsonObject = JSONObject.parseObject("{\n" +
                    "\t\"identified\": \"0\"\n" +
                    "}");
            currentStageContent.put("resultOfIdentify", jsonObject);

        }else{

            JSONArray jsonArray = JSONArray.parseArray(disputecaseAccessory.getMedicaldamageAssessment());
            JSONObject temp = JSONObject.parseObject("{}");
            temp.put("identified","0");
            for(int i = 0; i< jsonArray.size(); i++){
                if(Integer.parseInt(jsonArray.getJSONObject(i).getString("stage")) == stage){
                    temp = jsonArray.getJSONObject(i);
                    temp.put("identified","1");
                }
            }


            currentStageContent.put("resultOfIdentify",temp);
        }
        //currentStageContent.put("resultOfIdentify",InitConstant.init_identify);





        if( disputecaseAccessoryRepository.findByDisputecaseId(caseId).getNormaluserUpload() != null)
            currentStageContent.getJSONObject("particopateContact").put("currentFiles", JSONArray.parseArray(disputecaseAccessoryRepository.findByDisputecaseId(caseId).getNormaluserUpload()));
        mediationStageForm.setCurrentStageContent(currentStageContent);
        /** currentStageContent */
        String tmp=currentStage.getJSONObject("particopateContact").getString("mediationPlace").trim();
        if(tmp==null||tmp=="" || tmp.equals("")) // 说明页面2没有填
            currentStatus=1;
        else
            currentStatus=2;

        mediationStageForm.setCurrentStatus(currentStatus);
        /** 挂起状态输入 */

        mediationStageForm.setIsSuspended(currentProcess.getIsSuspended());




        //是否可预约
        mediationStageForm.setAppoint(true);
        //String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Task tasks=taskService.createTaskQuery().processInstanceId(pid).singleResult();
        if (tasks.getName().equals("专家预约审核") || tasks.getName().equals("三方调解") || tasks.getName().equals("专家调解") || tasks.getName().equals("调解结果处理")){
            mediationStageForm.setAppoint(false);
        }


        return ResultVOUtil.ReturnBack(mediationStageForm,DisputeProgressEnum.GETMEDIATIONSTAGE_SUCCESS.getCode(),DisputeProgressEnum.GETMEDIATIONSTAGE_SUCCESS.getMsg());

    }

    @Override
    @Transactional
    public ResultVO getReMediationStage(String caseId) {
        caseId=caseId.trim();
        MediationStageForm mediationStageForm=new MediationStageForm();
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        /** 防止该json字段初始化的时候没有值 */

        if(StrIsEmptyUtil.isEmpty(currentProcess.getMediateStage())) {
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
        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        String cm=disputecase.getClaimMoney().trim();
        if(cm=="2" || cm.equals("2") || cm=="1" || cm.equals("1")) {  // 10w以上
            mediationStageForm.setIdentiQualify(true);
            Integer temp= Integer.parseInt(runtimeService.getVariable(pid,"paramAuthenticate").toString());
            if(temp==0) {  //尚未做过鉴定
                mediationStageForm.setIdentified(false);
                currentStatus=0;
            }
            else{
                mediationStageForm.setIdentified(true);
                currentStatus=1;
//                mediationStageForm.setResultOfIdentify(disputecaseAccessory.getMedicaldamageAssessment());
                //JSONObject tempp=JSONObject.parseObject(disputecaseAccessory.getMedicaldamageAssessment());
            }
        }else{
            mediationStageForm.setIdentified(false);
            mediationStageForm.setIdentiQualify(false);
            currentStatus=1;
        }
        /** 能否预约专家,  先看金额，后看是否已预约过*/
        if(runtimeService.getVariable(pid,"paramProfesor")==null || (runtimeService.getVariable(pid,"paramProfesor")).equals("0"))
            runtimeService.setVariable(pid,"paramProfesor",0);
        Integer pP= Integer.parseInt(runtimeService.getVariable(pid,"paramProfesor").toString());
        if(pP==0)
            mediationStageForm.setExpertAppoint("0");
        else
            mediationStageForm.setExpertAppoint("1");
        //if((cm!="0"|| !cm.equals("0")))
        if((!cm.equals("0")))
            mediationStageForm.setExpert(true);
        else
            mediationStageForm.setExpert(false);

        /** applicant`respondent数组 */
        /** 1、申请人.如果 mediateStage中数据为空则读表该数据*/
        JSONArray applicants=mediateStage.getJSONArray("applicants");
        if(applicants.isEmpty()){
            JSONArray apps=JSONArray.parseArray("[]");
            for(String s:disputecase.getProposerId().trim().split(",")){
                // name , phone ,email
                DisputecaseApply disputecaseApply=disputecaseApplyRepository.getOne(s);
                String name=disputecaseApply.getName();
                String phone=disputecaseApply.getPhone();
                String specificId=userRepository.findByPhone(phone).getSpecificId();
                String email=normalUserRepository.getOne(specificId).getEmail();
                if(email==null)
                    email="";
                JSONObject obj=JSONObject.parseObject("{}");
                obj.put("name",name);
                obj.put("phone",phone);
                obj.put("email",email);
                apps.add(obj);
            }
            mediationStageForm.setApplicants(apps);
        }else
            mediationStageForm.setApplicants(applicants);
        /** 2、院方,如果 mediateStage中数据为空则读表该数据 */
        JSONArray respondents=mediateStage.getJSONArray("respondents");
        if(respondents.isEmpty()){
            List<String> res=new ArrayList<>();
            res=GetHospitalUtil.extract(disputecase.getMedicalProcess());//todo:涉事医院
//            JSONObject hosJS=JSONObject.parseObject(constantDataRepository.findByName("hospital_list").getData());//todo:市 区



            JSONArray respo=JSONArray.parseArray("[]");
            for(String hos:res){

                List<ContactList> contactList = contactListRepository.findByName(hos);
                ContactList contact = contactList.get(0);

                String phone=contact.getTele();
                if(phone==null)
                    phone="";
                JSONObject obj=JSONObject.parseObject("{}");
                obj.put("name",hos);
                obj.put("phone",phone);
                respo.add(obj);
            }
            mediationStageForm.setRespondents(respo);
        }else
            mediationStageForm.setRespondents(respondents);
        /**  stageContent直接取出来 */
//        String currentStageContent=currentStage.toString();
        JSONObject currentStageContent=JSONObject.parseObject(currentStage.toString());

        //温要求修改
//        if(StrIsEmptyUtil.isEmpty(disputecaseAccessory.getMedicaldamageAssessment())){
//            //currentStageContent.put("resultOfIdentify",InitConstant.init_identify);
//            currentStageContent.put("resultOfIdentify","[]");
//        }else{
//            JSONArray jsonArray = JSONArray.parseArray(disputecaseAccessory.getMedicaldamageAssessment());
//            JSONObject temp = JSONObject.parseObject("{}");
//            for(int i = 0; i< jsonArray.size(); i++){
//                if(Integer.parseInt(jsonArray.getJSONObject(i).getString("num")) == jsonArray.size() - 1){
//                    temp = jsonArray.getJSONObject(i);
//                }
//            }
//
//            currentStageContent.put("resultOfIdentify",temp.toJSONString());
//        }
        currentStageContent.put("resultOfIdentify",JSONObject.parseObject("{\n" +
                "\t\"num\": \"\",\n" +
                "\t\"text\": \"\",\n" +
                "\t\"files\": [{\n" +
                "\t\t\"url\": \"\",\n" +
                "\t\t\"name\": \"\"\n" +
                "\t}]\n" +
                "}"));

        if( disputecaseAccessoryRepository.findByDisputecaseId(caseId).getNormaluserUpload() != null)
            currentStageContent.getJSONObject("particopateContact").put("currentFiles", JSONArray.parseArray(disputecaseAccessoryRepository.findByDisputecaseId(caseId).getNormaluserUpload()));
        mediationStageForm.setCurrentStageContent(currentStageContent);
        /** currentStageContent */
        String tmp=currentStage.getJSONObject("particopateContact").getString("mediationPlace").trim();
        if(tmp==null||tmp=="" || tmp.equals("")) // 说明页面2没有填
            currentStatus=1;
        else
            currentStatus=2;

        mediationStageForm.setCurrentStatus(currentStatus);
        /** 挂起状态输入 */

        mediationStageForm.setIsSuspended(currentProcess.getIsSuspended());



        return ResultVOUtil.ReturnBack(mediationStageForm,DisputeProgressEnum.GETMEDIATIONSTAGE_SUCCESS.getCode(),DisputeProgressEnum.GETMEDIATIONSTAGE_SUCCESS.getMsg());

    }

    @Override
    @Transactional
    public ResultVO setResultOfIndent(String caseId, String text, MultipartFile[] multipartFiles, String stage) {
        /** 目前处于主流程:损害/医疗鉴定 */
        Task task=null;
        List<Task> tasks=verifyProcessUtil.verifyTask(caseId,"损害/医疗鉴定");
        for(Task one:tasks)
            if(one.getName().equals("损害/医疗鉴定")){
                task=one;
                break;
            }

        DisputecaseAccessory disputecaseAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        if(StrIsEmptyUtil.isEmpty(disputecaseAccessory.getMedicaldamageAssessment()))
            disputecaseAccessory.setMedicaldamageAssessment("[]");
        JSONArray md=JSONArray.parseArray(disputecaseAccessory.getMedicaldamageAssessment());

        JSONObject each = JSONObject.parseObject("{}");
        if(!StrIsEmptyUtil.isEmpty(text))
            each.put("text",text);
        String title=disputecaseRepository.getOne(caseId).getCaseName();
        JSONArray files= JSONArray.parseArray("[]");
        for (MultipartFile multipartFile: multipartFiles){
            JSONObject obj= JSONObject.parseObject("{}");
            try{
                FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
                String url = disputecaseAccessoryService.uploadFile(inputStream, title+ multipartFile.getOriginalFilename());
                obj.put("url","http://"+url);
                obj.put("name",multipartFile.getOriginalFilename());
                files.add(obj);
            }catch (IOException ioe){
                obj.put("url","");
                obj.put("name","");
            }
        }
        each.put("files",files);


        each.put("stage", stage);
        md.add(each);

        disputecaseAccessory.setMedicaldamageAssessment(md.toString());
        disputecaseAccessoryRepository.save(disputecaseAccessory);
        /** 将挂起的流程返回正常状态 */
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        currentProcess.setIsSuspended(0);
        disputecaseProcessRepository.save(currentProcess);
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        runtimeService.setVariable(pid,"paramAuthenticate","1");
        /** 完成当前流程 */
        taskService.complete(task.getId());  // 会进去到流程 调解前处理

        return ResultVOUtil.ReturnBack(DisputeProgressEnum.SETRESULTOFINDENT_SUCCESS.getCode(),DisputeProgressEnum.SETRESULTOFINDENT_SUCCESS.getMsg());

    }

    @Autowired
    private MediateInform mediateInform;
    @Override
    @Transactional
    public ResultVO setAppoint(String caseId, String currentStageContent) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        JSONObject mediateStage=JSONObject.parseObject(disputecaseProcess.getMediateStage());

        Integer stage=mediateStage.getInteger("stage");
        Integer currentStatus=mediateStage.getInteger("currentStatus");
        JSONArray stageContent=mediateStage.getJSONArray("stageContent");
        if(stage==null)
            stage=1;
        stageContent.remove(stage-1);
        stageContent.add(JSONObject.parseObject(currentStageContent));
        mediateStage.put("stageContent",stageContent);
        disputecaseProcess.setMediateStage(mediateStage.toString());
        disputecaseProcessRepository.save(disputecaseProcess);

        /**
         * 当前步骤为 调解前处理
         * 1、设置paramBeforeMediate=1
         *         appointResult 参数 ： 0为三方 1为专家
         * 2、流程往下走一格到 调解通知
         * 3、自动结束调解通知
         */
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        List<Task> tasks=verifyProcessUtil.verifyTask(caseId,"调解前处理");
        Task currentTask=null;
        for(Task one :tasks)
            if(one.getName().equals("调解前处理")){
                currentTask=one;
                break;
            }
        taskService.complete(currentTask.getId());
        currentTask=taskService.createTaskQuery().processInstanceId(pid).singleResult();
        taskService.complete(currentTask.getId());// Wj
        /** 假如当前是在调解通知，则再跳一下 */
        currentTask=taskService.createTaskQuery().processInstanceId(pid).singleResult();
        if(currentTask.getName().equals("调解通知"))
            taskService.complete(currentTask.getId());


        /** 发送预约通知 */
        try {
            mediateInform.execute(caseId);
        }catch (Exception e){
            log.error("[预约通知流程出现问题！]");
            e.printStackTrace();
        }finally {
            return ResultVOUtil.ReturnBack(DisputeProgressEnum.SETAPPOINT_SUCCESS.getCode(),DisputeProgressEnum.SETAPPOINT_SUCCESS.getMsg());

        }
    }

    @Override
    public ResultVO setCaseLitigation(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("7");
        return ResultVOUtil.ReturnBack(115,"撤销案件");
    }

    @Autowired
    public DisputeProgressService disputeProgressService;

    @Override
    public ResultVO reMediation(String caseId) {
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        List<Task> tasks=verifyProcessUtil.verifyTask(caseId,"三方调解,专家调解"); //todo 10/16 19:23 ??????????????
        Task currentTask=null;
        for(Task taskOne:tasks)
            if(taskOne.getName().equals("三方调解")){
                currentTask=taskOne;
                break;
            }else if(taskOne.getName().equals("专家调解")){
                currentTask=taskOne;
                break;
            }
//        List<Task> tasks=verifyProcessUtil.verifyTask(caseId,"调解结果处理");//todo 10/16 19:23 ?????????????????????
//        Task currentTask = tasks.get(0);


        /** 修改状态为调解中、同时阶段也要增加，调解过程页面直接获取新阶段 */
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("2");
        JSONObject mediationStage=JSONObject.parseObject(disputecaseProcess.getMediateStage());
        JSONArray arr=mediationStage.getJSONArray("stageContent");
        arr.add(JSONObject.parseObject(InitConstant.currentStageContent));//todo:?????????????????????????????
        mediationStage.put("stageContent",arr);
        Integer stage=mediationStage.getInteger("stage");
        stage+=1;
        mediationStage.put("stage",stage);
        disputecaseProcess.setMediateStage(mediationStage.toString());
        disputecaseProcess=disputecaseProcessRepository.save(disputecaseProcess);
        /**
         * 对流程进行更新
         *  设置流程参数paramMediateResult  0成功；1诉讼；2走撤销；3走其他
         * */
//        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        currentTask=taskService.createTaskQuery().processInstanceId(pid).singleResult();  // 调解结果处理
        Map<String,Object> var=new HashMap<>();
        var.put("paramMediateResult",3);
        //disputeProgressService.completeCurrentTask(currentTask.getId(), var);
        taskService.complete(currentTask.getId(),var);
        //二跳
        currentTask=taskService.createTaskQuery().processInstanceId(pid).singleResult();
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

            userCaseListForm.setCaseId(disputecase.getId());


            com.alibaba.fastjson.JSONArray arr= com.alibaba.fastjson.JSONArray.parseArray(disputecase.getMedicalProcess()); //todo test 设施医院

            List<String> hospitalList = new ArrayList<>();
            String hospitals = "";

            for (Object stage:arr){
                Object involvedInstitute = ((com.alibaba.fastjson.JSONObject) stage).get("InvolvedInstitute");
                hospitalList.add((String)(((com.alibaba.fastjson.JSONObject)involvedInstitute).get("Hospital")));
            }

            for(String hospital: hospitalList){
                hospitals = hospitals + hospital + "、";
            }
            hospitals = hospitals.substring(0,hospitals.length() - 1);

            userCaseListForm.setRespondent(hospitals);
            userCaseListForm.setStatus(disputecaseProcessRepository.findByDisputecaseId(disputecase.getId()).getStatus());
            userCaseListFormList.add(userCaseListForm);
        }
        Collections.sort(userCaseListFormList, new Comparator<UserCaseListForm>() {
            @Override
            public int compare(UserCaseListForm o1, UserCaseListForm o2) {
                try{
                    Date dt1 = o1.getDate();
                    Date dt2 = o2.getDate();
                    if (dt1.getTime() > dt2.getTime()) {
                        return -1;
                    } else if (dt1.getTime() < dt2.getTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return 0;
            }
        });

        return ResultVOUtil.ReturnBack(userCaseListFormList, 111,"用户中心获取用户案件列表成功。");
    }

    @Override
    public ResultVO getAuthority(String id) {
        try {
            User user=userRepository.getOne(id);
            if(user==null)
                return  ResultVOUtil.ReturnBack(501,"该id不存在");
            Map<String,Object> var=new HashMap<>();
            switch (Integer.parseInt(user.getRole())){
                case 2:
                    var.put("authorityConfirm","1");
                    var.put("authorityJudiciary","1");
                    return  ResultVOUtil.ReturnBack(var,DisputeProgressEnum.MEDIATORGETAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.MEDIATORGETAUTHORITY_SUCCESS.getMsg());
                case 1:
                    Mediator mediator=mediatorRepository.findByFatherId(id);
                    String authorityConfirm=mediator.getAuthorityConfirm();
                    String authorityJudiciary=mediator.getAuthorityJudiciary();
                    var.put("authorityConfirm",authorityConfirm);
                    var.put("authorityJudiciary",authorityJudiciary);
                    return  ResultVOUtil.ReturnBack(var,DisputeProgressEnum.MEDIATORGETAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.MEDIATORGETAUTHORITY_SUCCESS.getMsg());
                default:
                    return ResultVOUtil.ReturnBack(501,"该id不属于管理者或调解员");
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
            return ResultVOUtil.ReturnBack(501,"空指针错误：该id不属于管理者或调解员,id="+id);
        }
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
    public ResultVO getAdditionalAllocation(String province ,String city,String mediateCenter ,String caseId, Pageable pageRequest) {
        if (province==null)
            province="";
        if(city==null)
            city="";
        if(mediateCenter==null)
            mediateCenter="";
        /** 先获取用户意向 */
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        String userChoose=disputecaseProcess.getUserChoose();
        if(StrIsEmptyUtil.isEmpty(userChoose))
            userChoose="";
        /** 获取回避信息 */
        String avoidStatus=disputecaseProcess.getAvoidStatus();
        if(StrIsEmptyUtil.isEmpty(avoidStatus))
            avoidStatus="";
        Page<Mediator> mediatorPage;
        mediatorPage=mediatorRepository.findAllWithoutUserChooseAndAvoidByMediatorCenter(userChoose,avoidStatus,province,city,mediateCenter,pageRequest);
        System.out.println(mediatorPage.getTotalPages());
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
    public ResultVO getAllMediator(String province, String city, String mediateCenter) {
        if (province==null)
            province="";
        if(city==null)
            city="";
        if(mediateCenter==null)
            mediateCenter="";
        List<Mediator> mediatorList=mediatorRepository.findByMediateCenter(province, city, mediateCenter);
        List<OneMediatorForm> mediatorFormList=new ArrayList<>();
        for(Mediator mediator:mediatorList){
            String name=mediator.getMediatorName();
            String mediatorId=mediator.getFatherId();
            mediatorFormList.add(new OneMediatorForm(name,mediatorId));
        }
        return ResultVOUtil.ReturnBack(mediatorFormList,DisputeProgressEnum.GETMEDIATORLIST_SUCCESS.getCode(),DisputeProgressEnum.GETMEDIATORLIST_SUCCESS.getMsg());
    }

    @Override
//    @Async
    public void setStartTimeAndEndTime(String caseId) throws Exception{
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        Date currentDate=new Date();
        currentProcess.setStartimeDisputecase(currentDate);
//      Date endDate = getWorkingTimeUtil.calWorkingTime(currentDate,30);//todo 未测试 18-09-29 16:36
        if (EndDate.endDate == null)
            EndDate.endDate= getWorkingTimeUtil.calWorkingTime(currentDate,30);
        Date endDate= EndDate.endDate;
        currentProcess.setEndtimeDisputecase(endDate);
        disputecaseProcessRepository.save(currentProcess);
    }

    @Override
    public ResultVO getHospitalMessage(String caseId) {
        // 先获取医疗行为数据，解析出医院信息,每个医院信息包括 市 区 名
        // TODO 解析方式随前端发送数据变化而变化 10/12 wj已修改
        JSONArray jsArr=JSONArray.parseArray(disputecaseRepository.getOne(caseId).getMedicalProcess());
        Map<String,InstituteForm> instituteFormMap=new HashMap<>();
//        for(int i=0;i<jsArr.size();++i){
//            JSONObject obj=jsArr.getJSONObject(i);
//            JSONArray InvolvedInstitutes=obj.getJSONArray("InvolvedInstitute");
//            for(int j=0;j<InvolvedInstitutes.size();++j){
//                JSONObject jsonObject=InvolvedInstitutes.getJSONObject(j);
//                InstituteForm instituteForm=new InstituteForm(jsonObject.getString("City"),jsonObject.getString("Zone"),jsonObject.getString("Hospital"));
//                instituteFormMap.put(instituteForm.toString(),instituteForm);
//            }
//        }

        JSONArray result = JSONArray.parseArray("[]");

        for(int i = 0; i < jsArr.size(); ++i){
            JSONObject obj=jsArr.getJSONObject(i);
            JSONObject involvedInstitute=obj.getJSONObject("InvolvedInstitute");
            InstituteForm instituteForm=new InstituteForm(involvedInstitute.getString("City"),involvedInstitute.getString("Zone"),involvedInstitute.getString("Hospital"));
            instituteFormMap.put(instituteForm.toString(),instituteForm);
        }


        for(InstituteForm form:instituteFormMap.values()){
            List<ContactList> contactLists=new ArrayList<>();
            JSONObject jsonObject = JSONObject.parseObject("{}");
            List<ContactList> lists=contactListRepository.findAllByCityAndZoneAndName(form.getCity(),form.getZone(),form.getHospital());
            for(ContactList c:lists)
                contactLists.add(c);

            jsonObject.put("name", form.getHospital());
            jsonObject.put("contact", contactLists);
            result.add(jsonObject);
        }




//原版
//        for(int i = 0; i < jsArr.size(); ++i){
//            JSONObject obj=jsArr.getJSONObject(i);
//            JSONObject involvedInstitute=obj.getJSONObject("InvolvedInstitute");
//            InstituteForm instituteForm=new InstituteForm(involvedInstitute.getString("City"),involvedInstitute.getString("Zone"),involvedInstitute.getString("Hospital"));
//            instituteFormMap.put(instituteForm.toString(),instituteForm);
//        }
//
//        List<ContactList> contactLists=new ArrayList<>();
//        for(InstituteForm form:instituteFormMap.values()){
//            List<ContactList> lists=contactListRepository.findAllByCityAndZoneAndName(form.getCity(),form.getZone(),form.getHospital());
//            for(ContactList c:lists)
//                contactLists.add(c);
//        }
//        List<InstituteMessageForm> instituteMessage=new ArrayList<>();
//        for(ContactList con:contactLists){
//            InstituteMessageForm imForm=new InstituteMessageForm(con.getName(),con.getTele(),con.getContactPerson(),con.getContactPhone());
//            instituteMessage.add(imForm);
//        }



        return ResultVOUtil.ReturnBack(result,DisputeProgressEnum.GETINFORMATION_SUCCESS.getCode(),DisputeProgressEnum.GETINFORMATION_SUCCESS.getMsg());
    }

    @Override
    @Transactional
    public ResultVO inqueryHospital(Integer isFinished, String caseId, String mediatorId, String inquireMessages, MultipartFile[] file) throws IOException {



        JSONArray inquireMessage=JSONObject.parseArray(inquireMessages);

        /**  先判断当前流程位置*/
        List<Task> tasks=verifyProcessUtil.verifyTask(caseId,"问询医院");
        Task currentTask=null;
        for(Task one :tasks)
            if(one.getName().equals("问询医院")){
                currentTask=one;
                break;
            }


        DisputecaseAccessory currentAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        String ih=currentAccessory.getInquireHospital();
        if(StrIsEmptyUtil.isEmpty(ih)){
            // 初始化
            JSONObject ihJS=JSONObject.parseObject("{}");
            /** 获取医院列表 */
            Set<String> hospitalSet=new HashSet<>();
            for(int i=0;i<inquireMessage.size();++i){
                JSONObject obj=inquireMessage.getJSONObject(i);

                hospitalSet.add(obj.getString("hospital"));
            }
            // 写入数据
            String hospitalList=String.join(",",hospitalSet);
            ihJS.put("hospitalList",hospitalList);
            for(String hospitalOne:hospitalSet){
                JSONArray hArr=JSONArray.parseArray("[]");
                ihJS.put(hospitalOne,hArr);
            }

            currentAccessory.setInquireHospital(ihJS.toString());
            currentAccessory=disputecaseAccessoryRepository.save(currentAccessory);
        }
        JSONObject ihJS=JSONObject.parseObject(currentAccessory.getInquireHospital());
        ihJS.put("isFinished",isFinished);
        ihJS.put("caseId",caseId);
        for(int i=0;i<inquireMessage.size();++i){
            JSONObject obj=inquireMessage.getJSONObject(i);
            if(StrIsEmptyUtil.isEmpty(obj.getString("inquireResult")))
                continue;
            String currentHos=obj.getString("hospital");
            JSONArray tmp=ihJS.getJSONArray(currentHos);
            obj.put("mediatorId",mediatorId);

            JSONArray urls = JSONArray.parseArray("[]");
            for (int j = 0; j < file.length; j++){
                FileInputStream inputStream = (FileInputStream) file[j].getInputStream();
                String url = disputecaseAccessoryService.uploadFile(inputStream, caseId+"/"+ file[j].getOriginalFilename());
                urls.add("http://"+url);
            }

            obj.put("files",urls);
            tmp.add(obj);
            ihJS.put(currentHos,tmp);
        }

        currentAccessory.setInquireHospital(ihJS.toString());
        disputecaseAccessoryRepository.save(currentAccessory);


        /** 根据当前的isFInished值走流程 */
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Integer paramInquireHospital=0;
        String status="0";
        if(isFinished==2){
            paramInquireHospital=2;
            status="8";

        } else if(isFinished==1){
            paramInquireHospital=1;
            status="9";
        }
//        runtimeService.setVariable(pid,"paramInquireHospital",paramInquireHospital);
        updateCaseStatus(caseId,status);
        Map<String,Object> var=new HashMap<>();
        var.put("paramInquireHospital",paramInquireHospital);
        log.info("\n准备完成当前任务:[问询医院]\n");

        completeCurrentTask(currentTask.getId(),var);
        log.info("\n完成当前任务:[问询医院] 成功\n");
        Disputecase disputecase = disputecaseRepository.getOne(caseId);
        Map<String,String> map = new HashMap<>();
        map.put("caseLevel", disputecase.getLevel());
        return ResultVOUtil.ReturnBack(map,112,"提交问询成功");
    }

    @Autowired
    private AdminRepository adminRepository;
    @Override
    public ResultVO getInqueryHospitalList(String caseId) {
        DisputecaseAccessory currentAccessory=disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        if(StrIsEmptyUtil.isEmpty(currentAccessory.getInquireHospital())){
            return ResultVOUtil.ReturnBack(null,112,"获取问询列表成功");
        }
        String ih=currentAccessory.getInquireHospital();
        JSONObject ihArr=JSONObject.parseObject(ih);
        String[] temp=ihArr.getString("hospitalList").trim().split(",");
        JSONArray data=JSONArray.parseArray("[]");

        for(String s:temp){
            JSONArray jsonArray=ihArr.getJSONArray(s);
            for(int i=0;i<jsonArray.size();++i){
                JSONObject obj=jsonArray.getJSONObject(i);
                String mediatorId=obj.getString("mediatorId");
                User user=userRepository.getOne(mediatorId);
                String specificId=user.getSpecificId();
                String mediatorName=null;
                Mediator mediator=mediatorRepository.findOne(specificId);
                Admin admin=adminRepository.findOne(specificId);
                if(mediator!=null)
                    mediatorName=mediator.getMediatorName();
                else
                    mediatorName=admin.getAdminName();
                obj.put("mediatorName",mediatorName);
                data.add(obj);

            }
        }
        return ResultVOUtil.ReturnBack(data,112,"获取问询列表成功");
    }


    @Override
    public void setSuspended(String caseId, Integer isSuspended) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setIsSuspended(isSuspended);
        disputecaseProcessRepository.save(disputecaseProcess);
    }

    @Override
    public ResultVO getExpertManageList(PageRequest pageRequest,Integer filterStatus,String province,String city,String mediateCenter) {
        /**
          filterstatus 0 1 2 待审批 通过 不通过
         待审批找参数is_suspended
         其他找参数paramProfessor
         */
        Page<Object[]> pages=null;
        List<ExpertAppointForm> list=new ArrayList<>();
        if(filterStatus!=3){
            if(filterStatus==0)
                pages=disputecaseAccessoryRepository.findBySuspended(2,province,city,mediateCenter,pageRequest);
            else if(filterStatus==1 || filterStatus==2)
                pages=disputecaseAccessoryRepository.findByParamProfessor(String.valueOf(filterStatus),province,city,mediateCenter, pageRequest);

            for(Object[] obj:pages.getContent()){
                ExpertAppointForm form=new ExpertAppointForm(obj[0].toString(),obj[1].toString(), JSONObject.parseObject(obj[2].toString()).getString("application"), String.valueOf(filterStatus));
                list.add(form);
            }
        }else{
            pages=disputecaseAccessoryRepository.findBySuspendedAndParamProfessor(province,city,mediateCenter,pageRequest);
            for(Object[] obj:pages.getContent()){
                String result="";
                if(obj[3].toString().equals("0"))
                    result="0";
                else
                    result=obj[3].toString();
                ExpertAppointForm form=new ExpertAppointForm(obj[0].toString(),obj[1].toString(), JSONObject.parseObject(obj[2].toString()).getString("application"), result);
                list.add(form);
            }
        }


        return ResultVOUtil.ReturnBack(list,200,"管理员获取专家管理界面数据成功");
    }

    @Override
    public void saveMediateVideo(String caseId, String video_url) {
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId(caseId);
        JSONObject mediateStage=JSONObject.parseObject(currentProcess.getMediateStage());
        JSONArray stageContent=mediateStage.getJSONArray("stageContent");
        JSONObject currentStageContent=stageContent.getJSONObject(mediateStage.getInteger("stage")-1);
        JSONObject particopateContact=currentStageContent.getJSONObject("particopateContact");
        JSONArray currentFiles=particopateContact.getJSONArray("currentFiles");
        if(currentFiles.isEmpty())
            currentFiles=JSONArray.parseArray("[]");
        currentFiles.add(video_url);
        particopateContact.put("currentFiles",currentFiles);
        currentStageContent.put("particopateContact",particopateContact);
        stageContent.set(mediateStage.getInteger("stage")-1,currentStageContent);
        mediateStage.put("stageContent",stageContent);
        currentProcess.setMediateStage(mediateStage.toString());
        disputecaseProcessRepository.save(currentProcess);
    }

    @Override
    public ResultVO setCaseCancelApply(String caseId, String reason) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("10");

        JSONObject reasonJSON= JSONObject.parseObject(disputecaseProcess.getReason());
        JSONArray changeMediator = reasonJSON.getJSONArray("caseCancelApply ");
        JSONObject each = JSONObject.parseObject("{}");
        each.put("reason", reason);
        each.put("num", changeMediator.size());
        changeMediator.add(each);
        disputecaseProcess.setReason(reasonJSON.toJSONString());

        disputecaseProcessRepository.save(disputecaseProcess);
        return ResultVOUtil.ReturnBack(114,"撤销申请");
    }

    @Override
    public ResultVO setCaseCancellMediation(String caseId, String reason) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("11");

        JSONObject reasonJSON= JSONObject.parseObject(disputecaseProcess.getReason());
        JSONArray changeMediator = reasonJSON.getJSONArray("caseCancelMediation ");
        JSONObject each = JSONObject.parseObject("{}");
        each.put("reason", reason);
        each.put("num", changeMediator.size());
        changeMediator.add(each);
        disputecaseProcess.setReason(reasonJSON.toJSONString());

        disputecaseProcessRepository.save(disputecaseProcess);
        return ResultVOUtil.ReturnBack(114,"撤销调解");
    }

    @Override
    public ResultVO setCaseSettle(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("4");
        disputecaseProcessRepository.save(disputecaseProcess);
        return ResultVOUtil.ReturnBack(114,"申请结案");
    }

    @Override
    public ResultVO setCasereMediation(String caseId) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("2");
        disputecaseProcessRepository.save(disputecaseProcess);
        return ResultVOUtil.ReturnBack(114,"申请再次调解");
    }

    @Override
    public ResultVO changeMediator(String caseId, List<String> mediatorId, String reason) {
        DisputecaseProcess disputecaseProcess = disputecaseProcessRepository.findByDisputecaseId(caseId);
        disputecaseProcess.setStatus("12");

        JSONObject reasonJSON= JSONObject.parseObject(disputecaseProcess.getReason());
        JSONArray changeMediator = reasonJSON.getJSONArray("changeMediator ");
        JSONObject each = JSONObject.parseObject("{}");
        each.put("reason", reason);
        each.put("num", changeMediator.size());
        changeMediator.add(each);
        disputecaseProcess.setReason(reasonJSON.toJSONString());

//        disputecaseProcess.setUserChoose(String.join(",", mediatorId));
//        disputecaseProcessRepository.save(disputecaseProcess);
        for(int i = 0; i < mediatorId.size(); i++){
            disputeProgressService.updateAvoidStatus(caseId,mediatorId.get(i));
        }
        return ResultVOUtil.ReturnBack(114,"更换调解员");
    }

    @Override
    public ResultVO getcaseDetail(String caseId, String role) {

        Disputecase disputecase = disputecaseRepository.findOne(caseId);
        String[] temp=disputecase.getProposerId().trim().split(",");
        List<String> names=new ArrayList<>();
        for(String s:temp){
            names.add(disputecaseApplyRepository.findOne(s).getName());
        }

        JSONObject result = JSONObject.parseObject("{}");
        result.put("involvedPeople", names);

        result.put("require", disputecase.getAppeal());

        String cA = disputecase.getClaimMoney();
        String claimAmount = "";
        if(cA.equals("0")){
            claimAmount = "2万以下";
        }
        if(cA.equals("1")){
            claimAmount = "2万至10万";
        }
        if(cA.equals("2")){
            claimAmount = "10万以上";
        }
        result.put("claimAmount", claimAmount);


        JSONObject jO = JSONObject.parseObject(disputecase.getRecommendedPaper());
        if(role.equals("0")){
            jO.remove("dissension_dx");
            jO.remove("dissension");
        }

        result.put("relatedCases", jO);


        String mainRecSatgeName="";

        JSONArray medicalProcess = JSONArray.parseArray(disputecase.getMedicalProcess());
        JSONArray stage = JSONArray.parseArray("[]");

        for(int i = 0; i < medicalProcess.size(); i++){

            if(disputecase.getMainRecStage().equals(String.valueOf(i+1))){
                mainRecSatgeName = ((JSONObject)medicalProcess.get(i)).getString("name");
            }

            JSONObject jsonObject = JSONObject.parseObject("{}");

            jsonObject.put("name",((JSONObject)medicalProcess.get(i)).getString("name"));

            JSONObject involvedInstituteJson = ((JSONObject)medicalProcess.get(i)).getJSONObject("InvolvedInstitute");

            String involvedInstitute = involvedInstituteJson.getString("City")+involvedInstituteJson.getString("Zone")+involvedInstituteJson.getString("Hospital");


            JSONArray resultOfRegConflict = ((JSONObject)medicalProcess.get(i)).getJSONArray("resultOfRegConflict");
            List<String> behaviorListTemp = new ArrayList<>();



            String diseaseBefore="";
            String diseaseAfter="";

            String diseasesymptomBefore = "";
            String diseasesymptomAfter = "";
            String details = "";
            for(int j = 0; j < resultOfRegConflict.size(); j++){
                String resultOfRegConflictName = ((JSONObject)resultOfRegConflict.get(j)).getString("name");
                List<String> dB = (List<String>) ((JSONObject)resultOfRegConflict.get(j)).get("defaultBehavior");
                List<String> diseaseAfterList = (List<String>) ((JSONObject)resultOfRegConflict.get(j)).get("diseaseAfter");
                List<String> diseaseBeforeList = (List<String>) ((JSONObject)resultOfRegConflict.get(j)).get("diseaseBefore");
                List<String> diseasesymptomAfterList = (List<String>) ((JSONObject)resultOfRegConflict.get(j)).get("diseasesymptomAfter");
                List<String> diseasesymptomBeforeList = (List<String>) ((JSONObject)resultOfRegConflict.get(j)).get("diseasesymptomBefore");

                diseasesymptomBefore = diseasesymptomBefore + String.join(",", diseasesymptomBeforeList);
                diseasesymptomAfter = diseasesymptomAfter + String.join(",", diseasesymptomAfterList);

                diseaseAfter = diseaseAfter + String.join(",", diseaseAfterList);
                diseaseBefore = diseaseBefore + String.join(",", diseaseBeforeList);

                if (dB.size()!=0){
                    for (int k=0; k< dB.size();k++){
                        details = details + dB.get(k)+"、";
                    }
                    behaviorListTemp.add(resultOfRegConflictName+":"+details.substring(0, details.length()-1));
                }


            }
            jsonObject.put("behaviorList", behaviorListTemp);

            jsonObject.put("resultOfDamage",((JSONObject)medicalProcess.get(i)).getString("ResultOfDamageDetail"));
            if(diseasesymptomBefore.equals(""))
                diseasesymptomBefore="未填写";
            if(diseaseBefore.equals(""))
                diseaseBefore="未填写";
            if(diseasesymptomAfter.equals(""))
                diseasesymptomAfter="未填写";
            if(diseaseAfter.equals(""))
                diseaseAfter="未填写";
            jsonObject.put("diseasesBefore","症状："+diseasesymptomBefore+ "。疾病："+diseaseBefore+"。");
            jsonObject.put("diseaseAfter","症状："+diseasesymptomAfter+ "。疾病："+diseaseAfter+"。");

            jsonObject.put("involvedInstitute",involvedInstitute);
            jsonObject.put("resultOfDamage",((JSONObject)medicalProcess.get(i)).getString("ResultOfDamage"));
            stage.add(jsonObject);
        }

        result.put("mainRecSatge",mainRecSatgeName);
        result.put("stage", stage);
        Map<String, JSONObject> registerData = new HashMap<>();
        registerData.put("registerData", result);
        return ResultVOUtil.ReturnBack(result,123, "详情中查看用户登记信息 以及 相关案例推荐");
    }
}
