package com.seu.service;

import com.seu.ViewObject.ResultVO;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface DisputeProgressService {

    /** 启动工作流 */
    ResultVO startProcess(String disputeId);
    ResultVO startProcess(String disputeId,Map<String,Object> vars);
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

    List<DisputeCaseForm> getDisputeListByTask(String task, Integer page, Integer size);

    /** 通过disoutrId分页查询纠纷调解历史记录*/
    List<HistoricTaskForm> getHistoricTaskListByDisputeId(String disputeId);

    /** 根据taskId对流程中的任务进行评价*/
    CommentForm addCommentByTaskId(String taskId, String disputeId, String comment);
}
