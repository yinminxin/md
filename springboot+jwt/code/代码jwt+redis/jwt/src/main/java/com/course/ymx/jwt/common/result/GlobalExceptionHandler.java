package com.course.ymx.jwt.common.result;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局404/500异常捕捉处理工具
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    public ResponseVO defaultErrorHandler(Exception e) {
        ResponseVO result = null;
        String message = e.getMessage();
        if (message.equals(ResultCode.UNAUTHORIZED.getCode())) {
            result = new ResponseVO(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
        } else if (e instanceof NoHandlerFoundException) {
            result = new ResponseVO(ResultCode.NOT_FOUND.getCode(), message);
        } else {
            result = new ResponseVO(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message);
        }
        return result;
    }
}