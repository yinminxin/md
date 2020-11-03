package com.course.ymx.jwt.service;

import com.course.ymx.jwt.entity.Test;
import com.course.ymx.jwt.vo.request.PageVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author yinminxin
 * @description
 * @date 2020/1/14 20:02
 */
public interface TestService {

    List<Test> findList();

    /**
     * 分页查询
     * @param pageVo
     * @return
     */
    Page<Test> findByPage(PageVo pageVo);
}
