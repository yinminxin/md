package com.course.ymx.jwt.common.base;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author yinminxin
 * @description
 * @date 2019/12/11 18:26
 *
 * @MappedSuperclass 不将当前父类entity映射到数据库
 *
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(name = "id", length = 32)
    private String id;

    @Basic
    @Column(name = "create_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL COMMENT '创建时间'")
    private Timestamp createTime;

    @Basic
    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL COMMENT '更新时间'")
    private Timestamp updateTime;

    @Basic
    @Column(name = "state", columnDefinition = "TINYINT DEFAULT '0' NOT NULL COMMENT '状态 0-正常 1-删除'")
    private short state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }
}
