package com.seu.form.VOForm;

import com.seu.form.DisputeRegisterDetailForm;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName DisputeCaseForm
 * @Description 返回前端时 用户查询能看到的单件纠纷案件信息
 * @Author 吴宇航
 * @Date 2018/7/21 13:31
 * @Version 1.0
 **/
@Data
public class DisputeCaseForm {
    /** 案件时间*/
    private Date date;
    /** 案件名称 */
    private String name;
    /** 案件状态 */
    private String status;
    /** 案件id号 */
    private String id;
    /** 用户姓名 */
    private String trueName;
    /** 用户角色*/
    private String role;

}
