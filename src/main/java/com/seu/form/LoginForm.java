package com.seu.form;


import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
public class LoginForm {
    private String phone;
    private String password;

    public LoginForm(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
