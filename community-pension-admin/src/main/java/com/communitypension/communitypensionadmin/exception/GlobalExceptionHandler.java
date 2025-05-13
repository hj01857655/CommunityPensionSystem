package com.communitypension.communitypensionadmin.exception;

import com.communitypension.communitypensionadmin.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex) {
        // 409 代表冲突
        return Result.error(409, ex.getMessage());
    }

    // 你可以根据需要添加更多异常处理方法
} 