package com.seu.ViewObject;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;


/**
 * 用于包装返回给前端的最外层json格式
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultVO<T> implements Serializable {
    private static final long serialVersionUID = 6358510185731454745L;
    /** 返回码 */
    private Integer code;
    private String msg="";
    private T data;



}
