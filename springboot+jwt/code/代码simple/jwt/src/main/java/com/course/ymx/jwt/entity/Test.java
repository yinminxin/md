package com.course.ymx.jwt.entity;

import com.course.ymx.jwt.common.base.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author yinminxin
 * @description 监控表
 * @date 2020/1/14 20:05
 */
@Entity
@Table(name = "test")
@org.hibernate.annotations.Table(appliesTo = "test", comment = "监控表")
public class Test extends BaseEntity {

    @Column(name = "name", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '名称'")
    private String name;
    @Column(name = "title", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '标题'")
    private String title;
    @Column(name = "time", columnDefinition = "date NOT NULL COMMENT '时间'")
    private Date time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
