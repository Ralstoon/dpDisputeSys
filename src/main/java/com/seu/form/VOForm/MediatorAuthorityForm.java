package com.seu.form.VOForm;

import lombok.Data;

import javax.sound.midi.Track;

/**
 * @ClassName MediatorAuthorityForm
 * @Description 调解员的授权信息 VOForm
 * @Author 吴宇航
 * @Date 2018/9/1 10:24
 * @Version 1.0
 **/
@Data
public class MediatorAuthorityForm {
    /** 调解员姓名 */
    private String name;
    /** 调解员id */
    private String id;
    /** 是否具有立案审批权 */
    private Boolean authority1;
    /** 是否具有上送司法厅权利 */
    private Boolean authority2;

    public MediatorAuthorityForm(String name, String id, String authority1, String authority2) {
        this.name = name;
        this.id = id;
        this.authority1 = (authority1=="1")?true:false;
        this.authority2 = (authority1=="1")?true:false;
    }
}
