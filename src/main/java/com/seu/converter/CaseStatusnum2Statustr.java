package com.seu.converter;

/**
 * @ClassName CaseStatusnum2Statustr
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/31 21:59
 * @Version 1.0
 **/
public class CaseStatusnum2Statustr {
    public static String convert(String status){
        String statustr=null;
        switch (status) {
            case "0":
                statustr = "申请中";
                break;
            case "1":
                statustr = "等待分配";
                break;
            case "2":
                statustr = "调解中";
                break;
            case "3":
                statustr = "调解失败";
                break;
            case "4":
                statustr = "结案中";
                break;
            case "5":
                statustr = "已结案";
                break;
            case "6":
                statustr = "撤销申请";
                break;
            case "7":
                statustr = "撤销调解";
                break;
        }
        return statustr;
    }

}
