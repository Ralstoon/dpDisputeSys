package com.seu.form;


import lombok.Data;

import java.io.Serializable;

/*
用于接收从前端传输过来的纠纷登记信息
 */

@Data
public class DisputeRegisterDetailForm implements Serializable {
    private String name="吴宇航";
    private String sex="男";

}
