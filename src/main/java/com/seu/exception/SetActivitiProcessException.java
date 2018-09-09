package com.seu.exception;

import lombok.Getter;

/**
 * @ClassName SetActivitiProcessException
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/7 20:10
 * @Version 1.0
 **/

@Getter
public class SetActivitiProcessException extends RuntimeException{
    public Integer code;

    public SetActivitiProcessException(Integer code,String msg){
        super(msg);
        this.code=code;
    }

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
}
