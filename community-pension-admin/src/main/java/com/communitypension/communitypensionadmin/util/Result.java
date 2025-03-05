package com.communitypension.communitypensionadmin.util;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> created(T data) {
        return new Result<>(201, "创建成功", data);
    }

    public static <T> Result<T> updated(T data) {
        return new Result<>(200, "更新成功", data);
    }

    public static <T> Result<T> deleted() {
        return new Result<>(200, "删除成功", null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null);
    }

    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }
}