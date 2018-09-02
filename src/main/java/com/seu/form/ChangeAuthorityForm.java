package com.seu.form;

import lombok.Data;

/**
 * @ClassName ChangeAuthorityForm
 * @Description 管理员发送给后台 改变调解员信息 列表中的一个对象
 * @Author 吴宇航
 * @Date 2018/9/1 10:51
 * @Version 1.0
 **/

@Data
public class ChangeAuthorityForm {
    /** 调解员id */
    private String nameid;
    /** 立案判断权限 */
    private Boolean authority1;
    /** 上送司法厅权限 */
    private Boolean authority2;
}
