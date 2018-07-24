package com.seu.exception;

/**
 * @ClassName NormalUserException
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/24 15:20
 * @Version 1.0
 **/
public class NormalUserException extends  RuntimeException{

    public Integer code;

    public NormalUserException(Integer code,String msg){
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
