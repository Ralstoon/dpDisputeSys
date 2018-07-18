package com.seu.service.impl;

import com.seu.service.DisputeProgressService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private ProcessDefinition pd;
    private ProcessInstance pi;

    /*
    仅仅启动了工作流，未对用户组管理做任何处理
     */
    @Override
    public boolean startProcess() {
        Deployment deployment=repositoryService.createDeployment().addClasspathResource("processes/test.bpmn").deploy();
        pd=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        pi=runtimeService.startProcessInstanceById(pd.getId());
        if(pi==null)
            return false;
        else
            return true;
    }


    /*
    查询当前任务
     */
    @Override
    public List<Task> searchCurrentTasks(String assignee) {
        //TODO
        return taskService.createTaskQuery().processInstanceId(pi.getId()).list();
    }
    @Override
    public List<Task> searchCurrentTasks() {
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

    @Override
    public Object getParamFormMPI(String paramName) {
        return runtimeService.getVariable(pi.getId(),paramName);
    }
}
