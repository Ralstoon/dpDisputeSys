package com.seu.converter;

import com.seu.domian.DisputeInfo;
import com.seu.form.DisputeRegisterDetailForm;

/**
 * @ClassName DisputeInfo2DisputeRegisterDetailForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/7/21 10:08
 * @Version 1.0
 **/
public class DisputeInfo2DisputeRegisterDetailForm {
    public static DisputeRegisterDetailForm convert(DisputeInfo disputeInfo,DisputeRegisterDetailForm disputeRegisterDetailForm){
        try {
            disputeRegisterDetailForm.setContent(disputeInfo.getContent());

        }catch (Exception e){
            //TODO
            e.printStackTrace();
        }finally {
            return disputeRegisterDetailForm;
        }
    }
    public static DisputeRegisterDetailForm convert(DisputeInfo disputeInfo){
        DisputeRegisterDetailForm disputeRegisterDetailForm=new DisputeRegisterDetailForm();
        try {
            disputeRegisterDetailForm.setContent(disputeInfo.getContent());

        }catch (Exception e){
            //TODO
            e.printStackTrace();
        }finally {
            return disputeRegisterDetailForm;
        }
    }
}
