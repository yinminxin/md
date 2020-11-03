package com.course.ymx.jwt.repository;

import com.course.ymx.jwt.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yinminxin
 * @description
 * @date 2020/1/14 20:04
 */
@Repository
public interface TestRepository extends JpaRepository<Test,String>, JpaSpecificationExecutor<Test> {

    @Query(value = "SELECT * from test;",nativeQuery = true)
    List<Test> findList();
}
