package com.seu.converter;

import com.seu.domian.NormalUserDetail;
import com.seu.form.NormalUserDetailForm;

import java.text.SimpleDateFormat;

public class NormalUserDetailForm2NormalUserDetail {

    public static NormalUserDetail convert(NormalUserDetailForm normalUserDetailForm,NormalUserDetail normalUserDetail){
        try {
            if(normalUserDetailForm.getSex()!=null){
                normalUserDetail.setSex(normalUserDetailForm.getSex());
            }
            if(normalUserDetailForm.getAge()!=null){
                String ageStr=normalUserDetailForm.getAge();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                normalUserDetail.setAge(sdf.parse(ageStr));
            }
            if(normalUserDetailForm.getEducation()!=null){
                normalUserDetail.setEducation(normalUserDetailForm.getEducation());
            }

        }catch (Exception e){
            //TODO
            e.printStackTrace();
        }finally {
            return normalUserDetail;
        }
    }
}
