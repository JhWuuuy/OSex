package com.os.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Boolean success;
    private String msg; // 消息
    private Object data; // 返回的数据

    public static Result ok(){
        return new Result(true, null, null);
    }
    public static Result ok(Object data){
        return new Result(true, null, data);
    }
    public static Result ok(String msg, Object data){
        return new Result(true, msg, data);
    }
    public static Result ok(String msg){
        return new Result(true, msg, null);
    }
    public static Result fail(String errorMsg){
        return new Result(false, errorMsg, null);
    }
}
