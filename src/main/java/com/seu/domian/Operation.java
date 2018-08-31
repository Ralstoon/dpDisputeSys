package com.seu.domian;

import lombok.Data;

/**
 * @ClassName Operation
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/28 14:43
 * @Version 1.0
 **/
@Data
public class Operation {
    private String keyword;
    private String operations;

    public Operation(String keyword,String operations){
        this.keyword=keyword;
        this.operations=operations;
    }
}
