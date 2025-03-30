package com.communitypension.communitypensionadmin.utils;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;

    // 添加 ok 方法
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    //success,接收message
    public static <T> Result<T> success(String message){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(null);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    //Result.success("查询成功", elder);
    public static <T> Result<T> success(String message,T data){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    //Result.success(roleService.getRolesByPage(page, size));
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    //Result.created(elder);
    public static <T> Result<T> created(T data) {
        Result<T> result = new Result<>();
        result.setCode(201);
        result.setMessage("创建成功");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    //Result.deleted();
    public static <T> Result<T> deleted(){
        Result<T> result = new Result<>();
        result.setCode(204);
        result.setMessage("删除成功");
        result.setData(null);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    //Result.error("查询失败");
    public  static <T> Result<T> error(String message){
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setData(null);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    //Result.error(500, "创建失败");
    public static <T> Result<T> error(int code,String message){
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    //Result.success(200, "查询成功", elderList);
    public static <T> Result<T> success(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    // 省略 getter 和 setter 方法...
}