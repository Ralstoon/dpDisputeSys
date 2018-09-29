package com.seu.domian;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName One
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/20 0020 下午 5:01
 * @Version 1.0
 **/
@Data
public class One {
    @NotNull(message = "id is neccssary")
    private String id;
    private String name;


}
