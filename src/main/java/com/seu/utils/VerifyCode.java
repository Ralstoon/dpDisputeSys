package com.seu.utils;

import java.util.Date;

public class VerifyCode {
    private String code;
    private Date time;

    public VerifyCode(String code, Date time) {
        this.code = code;
        this.time = time;
    }

    public VerifyCode() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


}
