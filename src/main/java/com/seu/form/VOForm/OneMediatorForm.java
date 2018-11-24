package com.seu.form.VOForm;

import lombok.Data;
import org.python.antlr.ast.Str;

/**
 * @ClassName OneMediatorForm
 * @Description 管理员获取调解员列表（用于给案件分配调解员） 时发送给前端的form
 * @Author 吴宇航
 * @Date 2018/9/1 10:13
 * @Version 1.0
 **/

@Data
public class OneMediatorForm {
    /** 调解员姓名 */
    private String mediatorName;
    /** 调解员id */
    private String mediatorId;
    /** 调解中心 */
    private String mediatorCenter;
    /** 基本信息 */
    private String basciInfo;

    public OneMediatorForm(String name, String id) {
        this.mediatorName = name;
        this.mediatorId = id;
    }

    public OneMediatorForm(String mediatorName, String mediatorId, String mediatorCenter, String basciInfo) {
        this.mediatorName = mediatorName;
        this.mediatorId = mediatorId;
        this.mediatorCenter = mediatorCenter;
        this.basciInfo = basciInfo;
    }
}
