package com.seu.handle;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.exception.NormalUserException;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName ExceptionHandle
 * @Description 处理并包装异常
 * @Author 吴宇航
 * @Date 2018/7/24 15:16
 * @Version 1.0
 **/
@ControllerAdvice
public class ExceptionHandle {
    private final static Logger logger=LoggerFactory.getLogger(Exception.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVO handle(Exception e){
        if(e instanceof NormalUserException){
            NormalUserException exce=(NormalUserException)e;
            return ResultVOUtil.ReturnBack(exce.getCode(),exce.getMessage());
        }else{
            // TODO 写其他的异常,这里先写了一个泛用错误
            return ResultVOUtil.ReturnBack(-1,"未知错误");
        }
    }

}

