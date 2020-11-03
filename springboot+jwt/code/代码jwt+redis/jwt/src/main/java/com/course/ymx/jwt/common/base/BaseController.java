package com.course.ymx.jwt.common.base;

import com.course.ymx.jwt.common.result.ResponseVO;
import com.course.ymx.jwt.common.result.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author yinminxin
 * @description Controller公共类
 * @date 2019/12/11 16:59
 */
public class BaseController {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;

    public String getToken(){
        return request.getHeader("Authorization");
    }

    public void setSessionAttr(String s,Object o){ request.getSession().setAttribute(s,o); }

    public Object getSessionAttr(String s){ Object o=request.getSession().getAttribute(s);return o; }

    protected ResponseVO getSuccess(){
        return new ResponseVO(ResultCode.SUCCESS.getCode());
    }

    protected ResponseVO getFromData(Object data){
        ResponseVO responseVO = getSuccess();
        responseVO.setData(data);
        return responseVO;
    }

    protected ResponseVO getFailure(){
        return new ResponseVO(ResultCode.FAIL.getCode());
    }

    protected ResponseVO getFailure(String msg){
        return new ResponseVO(ResultCode.FAIL.getCode(),msg);
    }

    protected ResponseVO getFailure(String errorCode, String msg){
        return new ResponseVO(errorCode, msg);
    }

    protected ResponseVO getFailureWithMap(String errorCode, Map<String, String> errMsgMap){
        StringBuilder stb = new StringBuilder();
        for (String errMsg : errMsgMap.values()){
            stb.append(errMsg);
        }
        return new ResponseVO(errorCode, stb.toString());
    }

    protected ResponseVO getResponse(Object data){
        ResponseVO responseVO =  getSuccess();
        responseVO.setData(data);
        return responseVO;
    }
}
