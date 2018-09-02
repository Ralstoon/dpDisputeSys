package com.seu.domian;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName DisputecaseActiviti
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/2 13:58
 * @Version 1.0
 **/

@Data
@Entity
public class DisputecaseActiviti {
    @Id
    private String disputecaseId;
    private String processId;
}
