package com.seu.service;

import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.form.CommentForm;
import com.seu.form.VOForm.DisputeCaseForm;
import com.seu.form.DisputeRegisterDetailForm;
import com.seu.form.HistoricTaskForm;
import net.sf.json.JSON;
import org.activiti.engine.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface DisputeProgressService {

    /** 启动工作流 */
    void startProcess(String disputeId);
    void startProcess(String disputeId,Map<String,Object> vars);
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
    ResultVO getMediationHallData(JSONObject map);

    /** 获取我的调节中的数据 */
    ResultVO getMyMediationData(JSONObject map) throws Exception;

    /** 管理员获得案件列表 */
    ResultVO getManagerCaseList(JSONObject map) throws Exception;

    /** 管理员获取统计管理页面列表 */
    ResultVO getManagerCaseJudiciary(String id);

    /** 管理员获取调解员列表（用于给案件分配调解员） */
    ResultVO getMediatorList(String id,Pageable pageable);

    /** 管理员 获取所有调解员的授权信息 */
    ResultVO getNameofAuthorityList(JSONObject map);

    /** 管理员发送授权调解员权限改变信息 */
    ResultVO changeAuthorityNameList(JSONObject map);

    /** 进入调解时 获取当前调解阶段、是否具备医疗鉴定资格、医疗鉴定与否、是否具备专家预约资格，当前阶段中的当前步骤（医疗鉴定中、预约中、正在调解中、调解结束）*/
    ResultVO getMediationStage(String caseId);
    ResultVO getReMediationStage(String caseId);

    /** 发送鉴定结果数据 */
    ResultVO setResultOfIndent(String caseId, String text, MultipartFile[] multipartFiles, String stage);

    /** 发送预约数据 */
    ResultVO setAppoint(String caseId,String currentStageContent);

    //管理员确定调解员
    ResultVO decideMediatorDisputeCase(String mediator, String caseId);

    ResultVO setMediationSuccess(String caseId);

    ResultVO setMediationFailure(String caseId);

    ResultVO setCaseSuccess(String caseId);

    ResultVO setCaseLitigation(String caseId);

    /** 申请再次调解 */
    ResultVO reMediation(String caseId);

    /** 通知用户进行医疗鉴定 */
    ResultVO informIndenty(String caseId);

    /** 获取专家库 */
    ResultVO getExpertsList();

    //用户中心获取案件列表
    ResultVO getUserCaseList(String userId);

    /** 获取调解员权限 */
    ResultVO getAuthority(String id);


    /** 修改案件状态 */
    void updateCaseStatus(String caseId,String status);

    /** 管理者获取该案件 的用户意向调解员 */
    ResultVO getUserChooseMediator(JSONObject map);

    /** 管理者获取该案件的 另外分配：剔除 用户意向和申请回避 */
    ResultVO getAdditionalAllocation(String caseId,Pageable pageRequest);

    /** 下拉框：管理员页面获取所有调解员(不分页) */
    ResultVO getAllMediator();

    /** 立案判断通过后为process表添加 调解开始时间和调解结束时间，初始限时30个工作日 */
    void setStartTimeAndEndTime(String caseId) throws Exception;

    /** 问询医院时获取医院信息 */
    ResultVO getHospitalMessage(String caseId);

    /** 提交问讯结果（文本），问讯医院 */
    ResultVO inqueryHospital(JSONObject map);

    /** 获取问讯列表 */
    ResultVO getInqueryHospitalList(String caseId);

    /** 设置流程挂起状态 */
    void setSuspended(String caseId,Integer isSuspended);

    /** 管理员获取专家管理界面数据 */
    ResultVO getExpertManageList(PageRequest pageRequest,Integer filterStatus);

    /** 根据caseId和video_url存入进展表的当前阶段中 */
    void saveMediateVideo(String caseId,String video_url);

    //撤销申请
    ResultVO setCaseCancelApply(String caseId, String reason);

    //撤销调解
    ResultVO setCaseCancellMediation(String caseId, String reason);

    //申请结案
    ResultVO setCaseSettle(String caseId);

    //申请再次调解
    ResultVO setCasereMediation(String caseId);

    //更换调解员
    ResultVO changeMediator(String caseId,List<String> mediatorId, String reason);

    ResultVO getcaseDetail(String caseId);

}
