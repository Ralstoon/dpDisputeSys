package com.seu.domian;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @ClassName ContactList
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/9 0009 下午 5:16
 * @Version 1.0
 **/
@Entity
@Data
public class ContactList implements Serializable {

    private static final long serialVersionUID = -6782119864242250298L;
    @Id
    @GeneratedValue
    private Integer id;
    /** 医院名 */
    private String name;
    /** 座机号 */
    private String tele;
    /** 负责人 */
    private String contactPerson;
    /** 手机号 */
    private String contactPhone;
    /** 角色 */
    private String role;
    /** 位置 */
    private String location;
    /** 省 */
    private String province;
    /** 区 */
    private String zone;
    /** 市 */
    private String city;

    public ContactList() {
    }
}
