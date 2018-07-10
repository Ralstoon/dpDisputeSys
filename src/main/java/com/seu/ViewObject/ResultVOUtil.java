package com.seu.ViewObject;

public class ResultVOUtil {
    /**
     * 用于封装ResultVO
     * @param object 表示data属性内容
     * @param msg  传入enum中具体的msg
     * @param code  传入enum中具体的code
     * @return
     */
    public static ResultVO ReturnBack(Object object,Integer code,String msg){
        ResultVO resultVO=new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        resultVO.setData(object);
        return resultVO;
    }
    public static ResultVO ReturnBack(Integer code,String msg){
        return ReturnBack(null,code,msg);
    }
}

