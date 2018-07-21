package com.seu.service;

import com.seu.ViewObject.ResultVO;
import com.seu.form.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import org.activiti.engine.task.Task;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;

public interface DisputeProgressService {

    /** 启动工作流 */
    ResultVO startProcess(String disputeId);

    /** 查询当前任务 */
    List<Task> searchCurrentTasks(String disputeId);
//    List<Task> searchCurrentTasks();

    /** 完成当前任务 */
    void completeCurrentTask(String taskId);
    void completeCurrentTask(String taskId, Map<String,Object> vars);

    /** 触发信号边界事件 */
    void triggerSignalBoundary(String signal);
    void triggerSignalBoundary(String signal,Map<String,Object> vars);

    /** 根据disputeId取出数据库中的content，并转换为Form形式返还给前端 */
    DisputeRegisterDetailForm getDisputeForm(String disputeId);

    /** 存储纠纷信息到数据库中 */
    String saveDisputeInfo(DisputeRegisterDetailForm disputeRegisterDetailForm,String starterId);

    /** 通过userId分页查找该用户尚未完成的纠纷案件 */
    List<DisputeCaseForm> getDisputeListByUserId(String userId, Integer page, Integer size);
}
