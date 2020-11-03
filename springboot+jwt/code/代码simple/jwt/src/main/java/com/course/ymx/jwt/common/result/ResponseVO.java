package com.course.ymx.jwt.common.result;

import java.io.Serializable;

public class ResponseVO implements Serializable {

    private String code;
    private boolean success;
    private String message;
    private Object data;

    public ResponseVO() {
    }

    public ResponseVO(String code) {
        this.code = code;
        if (code.equals(ResultCode.SUCCESS.getCode())) {
            this.success = true;
            this.message = ResultCode.SUCCESS.getMessage();
        } else {
            this.message = ResultCode.FAIL.getMessage();
        }
    }

    public ResponseVO(String code, String message) {
        this.code = code;
        if (code.equals(ResultCode.SUCCESS.getCode())) {
            this.success = true;
        }
        this.message = message;
    }

    public ResponseVO(boolean success, String message, String code) {
        this.message = message;
        this.code = code;
        this.success = success;
    }

    public ResponseVO(String message, String code, Object data) {
        this(code, message);
        this.data = data;
    }

    public ResponseVO(boolean success, String message, String code, Object data) {
        this(success, message, code);
        this.data = data;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}