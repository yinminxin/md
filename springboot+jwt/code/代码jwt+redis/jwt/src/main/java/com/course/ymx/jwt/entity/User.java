package com.course.ymx.jwt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.course.ymx.jwt.common.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author yinminxin
 * @description
 * @date 2020/1/16 17:30
 */
@Entity
@Table(name = "user")
@org.hibernate.annotations.Table(appliesTo = "user", comment = "用户表")
public class User extends BaseEntity {

    @Column(name = "name", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '用户姓名'")
    private String name;
    @Column(name = "user_name", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '用户名'")
    private String userName;
    @Column(name = "pass_word", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '密码'")
    @JSONField(serialize = false)
    private String passWord;
    @Column(name = "phone", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '手机号'")
    private String phone;
    @Column(name = "back_phone", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '备用手机号'")
    private String backPhone;
    @Column(name = "card_no", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '身份证号码'")
    private String cardNo;

    @Transient
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBackPhone() {
        return backPhone;
    }

    public void setBackPhone(String backPhone) {
        this.backPhone = backPhone;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
