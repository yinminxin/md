package com.course.ymx.jwt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.course.ymx.jwt.common.base.BaseController;
import com.course.ymx.jwt.common.result.ResponseVO;
import com.course.ymx.jwt.config.properties.JwtProperties;
import com.course.ymx.jwt.entity.User;
import com.course.ymx.jwt.utils.JwtUtils;
import com.course.ymx.jwt.utils.RedisUtils;
import com.course.ymx.jwt.vo.entity.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author yinminxin
 * @description 登陆相关控制层
 * @date 2020/1/3 15:57
 */
@Controller
@RequestMapping("/")
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwt;
    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/")
    public String loginView(){
        return "admin/login";
    }

    @PostMapping("/login")
    @ResponseBody
    private ResponseVO testLogin(@RequestBody String str){
        //用户Id
        String userName = null;
        String passWord = null;
        if (StringUtils.isNotBlank(str)) {
            //解析json字符串
            JSONObject jsonObject = JSON.parseObject(str);
            try {
                if(StringUtils.isNotBlank(jsonObject.getString("userName")) && StringUtils.isNotBlank(jsonObject.getString("passWord"))){
                    userName = jsonObject.getString("userName");
                    passWord = jsonObject.getString("passWord");
                }else{
                    return getFailure("参数错误");
                }
            } catch (NumberFormatException e) {
                return getFailure();
            }
        }else {
            return getFailure("参数错误");
        }
        //根据用户名和密码查找用户
        User user = userService.findByUserNameAndPassWord(userName,passWord);
        if (user == null) {
            return getFailure("用户名或密码错误!");
        }
        String token = null;
        try {
            //用户存在生成token
            token = JwtUtils.generateToken(new UserInfo(user.getId(),user.getUserName()), jwt.getPrivateKey(), jwt.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(token)) {
            return getFailure();
        }
        //将用户信息存入redis
        redisUtils.set(token,user,60L*30);
        //将token存入cookie
//        CookieUtils.setCookie(request,response,jwt.getCookieName(),token,jwt.getCookieMaxAge(),null,true);
        user.setToken(token);
        return getFromData(user);
    }
}
