package com.seu.converter;

import com.seu.domian.NormalUser;
import com.seu.form.NormalUserForm;

import java.text.SimpleDateFormat;

public class NormalUserForm2NormalUser {

    public static NormalUser convert(NormalUserForm normalUserForm, NormalUser normalUser){
        try {
            if(normalUserForm.getIdCard()!=null){
                normalUser.setIdCard(normalUserForm.getIdCard());
            }
            if(normalUserForm.getSex()!=null){
                normalUser.setSex(normalUserForm.getSex());
            }
            if(normalUserForm.getAge()!=null){
                String ageStr=normalUserForm.getAge();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                normalUser.setAge(sdf.parse(ageStr));
            }
            if(normalUserForm.getEducation()!=null){
                normalUser.setEducation(normalUserForm.getEducation());
            }
            if(normalUserForm.getName()!=null){
                normalUser.setName(normalUserForm.getName());
            }
            if(normalUserForm.getEmail()!=null){
                normalUser.setEmail(normalUserForm.getEmail());
            }
            if(normalUserForm.getUserName()!=null){
                normalUser.setUserName(normalUserForm.getUserName());
            }
        }catch (Exception e){
            //TODO
            e.printStackTrace();
        }finally {
            return normalUser;
        }
    }
}
