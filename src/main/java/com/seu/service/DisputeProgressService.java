package com.seu.service;

import com.seu.form.DisputeRegisterDetailForm;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface DisputeProgressService {

    /** 启动工作流 */
    boolean startProcess();

    /** 查询当前任务 */
    List<Task> searchCurrentTasks(String assignee);
    List<Task> searchCurrentTasks();

    /** 完成当前任务 */
    void completeCurrentTask(String taskId);
    void completeCurrentTask(String taskId, Map<String,Object> vars);

    /** 触发信号边界事件 */
    void triggerSignalBoundary(String signal);
    void triggerSignalBoundary(String signal,Map<String,Object> vars);

    /** 取出主流程实例中的参数 */
    Object getParamFormMPI(String paramName);
}
