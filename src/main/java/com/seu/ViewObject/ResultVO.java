package com.seu.ViewObject;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


/**
 * 用于包装返回给前端的最外层json格式
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultVO<T> {
    /** 返回码 */
    private Integer code;
    private String msg="";
    private T data;
}
