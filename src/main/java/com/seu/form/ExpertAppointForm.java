package com.seu.form;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @ClassName ExpertAppointForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/8 0008 下午 10:33
 * @Version 1.0
 **/
@Data
public class ExpertAppointForm {
    private String caseId;
    private String caseName;
    private JSONObject appointExpert;

    public ExpertAppointForm() {
    }

    public ExpertAppointForm(String caseId,String caseName, JSONObject appointExpert) {
        this.caseId=caseId;
        this.caseName = caseName;
        this.appointExpert = appointExpert;
    }
}
