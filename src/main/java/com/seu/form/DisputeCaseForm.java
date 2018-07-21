package com.seu.form;

import lombok.Data;

/**
 * @ClassName DisputeCaseForm
 * @Description 用户查询时能看到的单件纠纷案件信息
 * @Author 吴宇航
 * @Date 2018/7/21 13:31
 * @Version 1.0
 **/
@Data
public class DisputeCaseForm {
    /** 当事人姓名*/
    private String userName;
    /** 纠纷登记时间 */
    private String registerTime;
    /** 纠纷案件详情 */
    private DisputeRegisterDetailForm disputeRegisterDetailForm;

    /** 纠纷案件id号 */
    private String disputeId;


}
