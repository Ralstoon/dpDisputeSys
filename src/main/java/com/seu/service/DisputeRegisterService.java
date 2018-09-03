package com.seu.service;

import com.seu.ViewObject.ResultVO;

/**
 * @ClassName DisputeRegisterService
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/20 20:42
 * @Version 1.0
 **/
public interface DisputeRegisterService {

    ResultVO getDieaseList();

    ResultVO getMedicalBehaviorList();

    ResultVO getRoomList();

    ResultVO getOperations(String keywords,String room) throws Exception;

    /** 进入纠纷登记时，获取案件ID，防止不从第一页开始填 */
    ResultVO getCaseId();

    /** 发送涉事人员信息 */
    ResultVO sendInvolvedPeopleInfo(String caseId,String involvedPeople);

    /** 发送医疗数据 */
    ResultVO getBasicDivideInfo(String stageContent,String caseId,Integer mainRecStage,String require,Integer claimAmount);
}
