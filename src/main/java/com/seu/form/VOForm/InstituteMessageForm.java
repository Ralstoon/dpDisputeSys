package com.seu.form.VOForm;

import com.seu.common.InitConstant;
import com.seu.utils.StrIsEmptyUtil;
import lombok.Data;

/**
 * @ClassName InstituteMessageForm
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/20 0020 下午 7:00
 * @Version 1.0
 **/
@Data
public class InstituteMessageForm {
    /** 医院名称 */
    private String name;
    /** 院方电话 */
    private String fixedTele;
    /** 院方联系人 */
    private String contactPerson;
    /** 联系人电话 */
    private String contactPhone;

    public InstituteMessageForm() {
    }

    public InstituteMessageForm(String name, String fixedTele, String contactPerson, String contactPhone) {
        this.name = name;
        if(StrIsEmptyUtil.isEmpty(fixedTele))
            this.fixedTele= InitConstant.fixed;
        else
            this.fixedTele = fixedTele;
        if(StrIsEmptyUtil.isEmpty(contactPerson))
            this.contactPerson= InitConstant.fixed;
        else
            this.contactPerson = contactPerson;
        if(StrIsEmptyUtil.isEmpty(contactPhone))
            this.contactPhone=InitConstant.fixed;
        else
            this.contactPhone = contactPhone;
    }
}
