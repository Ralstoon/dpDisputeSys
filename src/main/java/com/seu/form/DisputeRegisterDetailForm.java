package com.seu.form;


import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

/*
用于接收从前端传输过来的纠纷登记信息
 */

@Data
public class DisputeRegisterDetailForm implements Serializable {
    @NotEmpty(message = "主ID号必须提供")
    private String ID;
    private String content="这是纠纷信息内容";

}
