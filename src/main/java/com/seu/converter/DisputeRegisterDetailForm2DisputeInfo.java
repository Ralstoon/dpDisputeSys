package com.seu.converter;

import com.seu.domian.DisputeInfo;
import com.seu.form.DisputeRegisterDetailForm;

/**
 * @ClassName DisputeRegisterDetailForm2DisputeInfo
 * @Description DisputeRegisterDetailForm是从前端获取的纠纷信息表，DisputeInfo是数据库存储的格式
 * @Author 吴宇航
 * @Date 2018/7/20 21:34
 * @Version 1.0
 **/
// TODO 两个类的内容都尚未完善
public class DisputeRegisterDetailForm2DisputeInfo {
    public static DisputeInfo convert(DisputeRegisterDetailForm disputeRegisterDetailForm, DisputeInfo disputeInfo){
        try {
            if(disputeRegisterDetailForm.getContent()!=null){
                disputeInfo.setContent(disputeRegisterDetailForm.getContent());
            }

        }catch (Exception e){
            //TODO
            e.printStackTrace();
        }finally {
            return disputeInfo;
        }
    }
    public static DisputeInfo convert(DisputeRegisterDetailForm disputeRegisterDetailForm){
        DisputeInfo disputeInfo=new DisputeInfo();
        try {
            if(disputeRegisterDetailForm.getContent()!=null){
                disputeInfo.setContent(disputeRegisterDetailForm.getContent());
            }

        }catch (Exception e){
            //TODO
            e.printStackTrace();
        }finally {
            return disputeInfo;
        }
    }
}
