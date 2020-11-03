package com.course.ymx.jwt.service.impl;

import com.course.ymx.jwt.entity.Test;
import com.course.ymx.jwt.repository.TestRepository;
import com.course.ymx.jwt.service.TestService;
import com.course.ymx.jwt.vo.request.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yinminxin
 * @description
 * @date 2020/1/14 20:02
 */
@Service
public class TestServiceImlp implements TestService {

    @Autowired
    private TestRepository testRepository;

    @Override
    public List<Test> findList() {
        return testRepository.findList();
    }

    @Override
    public Page<Test> findByPage(PageVo pageVo) {
        //默认分页参数
        int pageNum = 1;
        int pageSize = 20;
        //有分页参数给分页参数赋值
        if(pageVo.getPageNum() != null){
            pageNum=pageVo.getPageNum();
        }
        if(pageVo.getPageSize() != null){
            pageSize=pageVo.getPageSize();
        }
        //根据更新时间默认倒叙
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC,"updateTime"));
//        return estateRepository.findAll(pageable);

        // TODO Specification
        Specification<Test> specification = (Specification<Test>) (root, query, cb) -> {
            //查询列表
            List<Predicate> predicates = new ArrayList<>();
            //添加状态正常的条件
            predicates.add(cb.equal(root.get("state").as(short.class), (short)0));
            if (!org.springframework.util.StringUtils.isEmpty(pageVo.getSearchKey())) {
                Predicate p1 = cb.like(root.get("name").as(String.class), "%" + pageVo.getSearchKey().trim() + "%");
                Predicate p2 = cb.like(root.get("title").as(String.class), "%" + pageVo.getSearchKey().trim() + "%");
                predicates.add(cb.or(p1,p2));
            }
            //封装where
            if(!CollectionUtils.isEmpty(predicates)){
                Predicate[] preArr = new Predicate[predicates.size()];
                query.where(predicates.toArray(preArr));
            }
            return query.getRestriction();
        };
        //查询
        Page<Test> all = testRepository.findAll(specification, pageable);
        return all;
    }
}
