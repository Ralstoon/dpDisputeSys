package com.seu.handle;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.exception.NormalUserException;
import com.seu.exception.SetActivitiProcessException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * @ClassName ExceptionHandle
 * @Description 处理并包装异常
 * @Author 吴宇航
 * @Date 2018/7/24 15:16
 * @Version 1.0
 **/
@ControllerAdvice
@Slf4j
public class ExceptionHandle {
    private final static Logger logger=LoggerFactory.getLogger(Exception.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVO handle(Exception e){
        if(e instanceof NormalUserException){
            e.printStackTrace();
            NormalUserException exce=(NormalUserException)e;
            return ResultVOUtil.ReturnBack(exce.getCode(),exce.getMessage());
        }else if(e instanceof HttpMessageNotReadableException){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(-100,"JSON格式错误");
        }else if(e instanceof RedisConnectionFailureException){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(-200,"Redis缓存服务器连接错误");
        }else if(e instanceof HttpRequestMethodNotSupportedException){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(-300,"请求方法不支持");
        }else if(e instanceof SetActivitiProcessException) {
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(((SetActivitiProcessException) e).getCode(), e.getMessage());
        }else if(e instanceof IOException){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(-400,"文件存取出错");
        }else if (e instanceof NullPointerException){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(-500,"空指针异常");
        }

        else{
            // TODO 写其他的异常,这里先写了一个泛用错误
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(-1,"未知错误");
        }
    }

}

