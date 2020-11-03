package com.course.ymx.jwt.common.result;

public enum ResultCode {

    /** 成功 */
    SUCCESS("200", "successed"),
    /** 操作失败 */
    FAIL("209", "failed"),

    /** 成功 */
    OK("200", "OK"),

    /** 用户新建或修改数据成功 */
    CREATED("201", "CREATED"),

    /** 用户删除数据成功 */
    NO_CONTENT("204", "NO CONTENT"),


    /** 用户发出的请求有错误 */
    INVALID_REQUEST("400", "INVALID REQUEST"),

    /** 未经授权 用户需要进行身份验证 */
    UNAUTHORIZED("401", "UNAUTHORIZED"),

    /** 访问是被禁止 */
    FORBIDDEN("403", "FORBIDDEN"),

    /** 不存在的记录 */
    NOT_FOUND("404", "NOT FOUND"),

    /** 当创建一个对象时，发生一个验证错误 */
    UNPROCESABLE_ENTITY("411", "UNPROCESABLE ENTITY"),

    /** 资源被永久删除 */
    GONE("414", "GONE"),


    /** 服务器发生错误 */
    INTERNAL_SERVER_ERROR("500", "INTERNAL SERVER ERROR");

    private String code;
    private String message;

    private ResultCode(String code, String message){
        this.code = code;
        this.message = message != null ? message : "";
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}