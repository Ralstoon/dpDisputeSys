package com.seu.service;

import com.seu.ViewObject.ResultVO;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import org.activiti.engine.task.Task;

import java.text.ParseException;
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

    // TODO 可能不需要了
    /** 根据disputeId取出数据库中的content，并转换为Form形式返还给前端 */
    DisputeRegisterDetailForm getDisputeForm(String disputeId);


    //TODO 重写,分步骤进行
    /** 存储纠纷信息到数据库中 */
//    String saveDisputeInfo(DisputeRegisterDetailForm disputeRegisterDetailForm,String starterId);


    /** 通过用户id查找该用户的纠纷案件 */
    List<DisputeCaseForm> getDisputeListByUserId(String userId);
    // TODO 重写，且前端系统尚未写该功能
//    List<DisputeCaseForm> getDisputeListByTask(String task, Integer page, Integer size);

    /** 通过disoutrId分页查询纠纷调解历史记录*/
    List<HistoricTaskForm> getHistoricTaskListByDisputeId(String disputeId);

    /** 根据taskId对流程中的任务进行评价*/
    CommentForm addCommentByTaskId(String taskId, String disputeId, String comment);


    /** 为案件进程表更新（添加）apply_status状态 */
    void updateApplyStatus(String disputeId,String ID);

    /** 为案件进程表更新（添加）avoid_status状态 */
    void updateAvoidStatus(String disputeId,String ID);

    /** 为案件进程表更新（添加）user_choose选项 */
    void updateUserChoose(String disputeId,String mediatorList);

    /** 获取调解大厅中的数据 */
    ResultVO getMediationHallData(String id);

    /** 获取我的调节中的数据 */
    ResultVO getMyMediationData(String id);

    /** 管理员获得案件列表 */
    ResultVO getManagerCaseList(String id);

    /** 管理员获取统计管理页面列表 */
    ResultVO getManagerCaseJudiciary(String id);

    /** 管理员获取调解员列表（用于给案件分配调解员） */
    ResultVO getMediatorList(String id);

    /** 管理员 获取所有调解员的授权信息 */
    ResultVO getNameofAuthorityList(String id);

    /** 管理员发送授权调解员权限改变信息 */
    ResultVO changeAuthorityNameList(String changeAuthorityFormList);

    /** 进入调解时 获取当前调解阶段、是否具备医疗鉴定资格、医疗鉴定与否、是否具备专家预约资格，当前阶段中的当前步骤（医疗鉴定中、预约中、正在调解中、调解结束）*/
    ResultVO getMediationStage(String caseId);

    /** 发送鉴定结果数据 */
    ResultVO setResultOfIndent(String caseId,String resultOfIndent);

    /** 发送预约数据 */
    ResultVO setAppoint(String caseId,String currentStageContent);

    //管理员确定调解员
    ResultVO decideMediatorDisputeCase(String mediator, String caseId);

    ResultVO setMediationSuccess(String caseId);

    ResultVO setMediationFailure(String caseId);

    ResultVO setCaseRepeal(String caseId);

    ResultVO setCaseLitigation(String caseId);
}
