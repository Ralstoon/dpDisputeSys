package com.seu.domian;

import com.seu.common.InitConstant;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName DisputecaseProcess
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/30 16:01
 * @Version 1.0
 **/

@Data
@Entity
public class DisputecaseProcess {
    @Id
    private String id;
    private String disputecaseId;
    private String status="0";
    private String param="{\"paramProfesor\": \"0\",\"paramAuthen\": \"0\"}";
    private String avoidStatus="";
    private String applyStatus="";
    private String userChoose="";
    private String mediateStage=InitConstant.init_mediateStage;
    private Boolean isSuspended = Boolean.FALSE;
    private Date endtimeDisputecase;
    private Date startimeDisputecase;

}
