package com.seu.form;


import lombok.Data;

import java.io.Serializable;

/*
用于接收从前端传输过来的纠纷登记信息
 */

@Data
public class DisputeRegisterDetailForm implements Serializable {
    private String content="这是纠纷信息内容";

}
