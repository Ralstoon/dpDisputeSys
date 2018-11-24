package com.seu.converter;

import com.seu.domian.User;
import com.seu.form.VOForm.UserForm;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName User2UserForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/3 21:28
 * @Version 1.0
 **/


public class User2UserForm {
    public static UserForm convert(User user){
        UserForm userForm=new UserForm();
        try {
            if(user.getID()!=null){
                userForm.setId(user.getID());
            }
            if(user.getSpecificId()!=null){
                userForm.setSpecific_id(user.getSpecificId());
            }
            if(user.getRole()!=null){
                userForm.setRole(user.getRole());

            }

        }catch (Exception e){
            //TODO
            e.printStackTrace();
        }finally {
            return userForm;
        }
    }
}
