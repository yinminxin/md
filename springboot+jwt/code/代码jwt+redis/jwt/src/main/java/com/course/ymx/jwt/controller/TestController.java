package com.course.ymx.jwt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.course.ymx.jwt.common.base.BaseController;
import com.course.ymx.jwt.common.result.ResponseVO;
import com.course.ymx.jwt.entity.Test;
import com.course.ymx.jwt.service.TestService;
import com.course.ymx.jwt.vo.request.PageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yinminxin
 * @description
 * @date 2020/1/16 12:20
 */
@RestController
@RequestMapping("test")
public class TestController extends BaseController {

    @Autowired
    private TestService testService;

    /**
     * @apiDefine sendSuccessList
     * @apiSuccess {Number} code 响应码;200(成功),402(未知错误)
     * @apiSuccess {String[]} data 数据
     * @apiSuccess {String} message 响应信息;接口请求success或failed返回相关信息
     * @apiSuccess {Boolean} success 是否成功;通过该字段可以判断请求是否到达.
     */

    /**
     * @apiDefine sendSuccess
     * @apiSuccess {Number} code 响应码;200(成功),402(未知错误)
     * @apiSuccess {Object} data 数据
     * @apiSuccess {String} message 响应信息;接口请求success或failed返回相关信息
     * @apiSuccess {Boolean} success 是否成功;通过该字段可以判断请求是否到达.
     */

    /**
     * @api {get} /test/getList get请求获取数据
     * @apiName getList
     * @apiDescription 测试api详情
     * @apiGroup test
     * @apiParam {Number} id 标题ID
     * @apiContentType application/json
     * @apiParamExample {json} Request-Example:
    {
    "id":"1"
    }
     * @apiUse sendSuccess
     * @apiSuccess {Object} data 数据
     * @apiSuccess {Integer} data.id 标签ID
     * @apiSuccess {String} data.name 标签名称
     * @apiSuccess {String} data.updatedTime 更新时间
     * @apiSuccessExample {json} Success-Response:
    {
    "code": "200",
    "data": "1",
    "message": "successed",
    "success": true
    }
     */
    @GetMapping("/getList")
    @ResponseBody
    private ResponseVO getList(@RequestBody String str){
        if (StringUtils.isNotBlank(str)) {
            String id = null; //ID
            JSONObject jsonObject = JSON.parseObject(str);
            if (StringUtils.isNotBlank(jsonObject.getString("id"))) {
                id = jsonObject.getString("id");
            } else {
                return getFailure();
            }
            if (StringUtils.isNotBlank(id)) {
                return getFromData(id);
            }
        }
        List<Test> list = testService.findList();
        return getFromData(list);
    }


    /**
     * @api {post} /test/postList post请求获取数据
     * @apiName postList
     * @apiDescription 测试api详情
     * @apiGroup test
     * @apiParam {Number} id 标题ID
     * @apiContentType application/json
     * @apiParamExample {json} Request-Example:
    {
    "id":"1"
    }
     * @apiUse sendSuccess
     * @apiSuccess {Object} data 数据
     * @apiSuccess {String} data.id ID
     * @apiSuccess {String} data.name 名称
     * @apiSuccess {String} data.title 标题
     * @apiSuccess {date} data.time 时间
     * @apiSuccessExample {json} Success-Response:
    {
    "code": "200",
    "data": [
    {
    "createTime": 1579156726000,
    "id": "1",
    "name": "测试1",
    "state": 0,
    "time": 1579104000000,
    "title": "测试标题1",
    "updateTime": 1579156726000
    }
    ],
    "message": "successed",
    "success": true
    }
     */
    @PostMapping("/postList")
    @ResponseBody
    private ResponseVO postList(@RequestBody String str){
        if (StringUtils.isNotBlank(str)) {
            String id = null; //ID
            JSONObject jsonObject = JSON.parseObject(str);
            if (StringUtils.isNotBlank(jsonObject.getString("id"))) {
                id = jsonObject.getString("id");
            } else {
                return getFailure();
            }
            if (StringUtils.isNotBlank(id)) {
                List<Test> list = testService.findList();
                return getFromData(list);
            }
            return getFailure("id为空");
        }else{
            return getFailure("id为空");
        }
    }

    /**
     * @api {post} /test/findByPage 分页请求数据
     * @apiName findByPage
     * @apiDescription 测试api详情
     * @apiGroup test
     * @apiParam {Number} id 标题ID
     * @apiContentType application/json
     * @apiParamExample {json} Request-Example:
    {
    "pageNum":1,
    "pageSize":4,
    "searchKey":"5"
    }
     * @apiUse sendSuccess
     * @apiSuccess {Object} data 数据
     * @apiSuccess {String} data.id ID
     * @apiSuccess {String} data.name 名称
     * @apiSuccess {String} data.title 标题
     * @apiSuccess {date} data.time 时间
     * @apiSuccessExample {json} Success-Response:
    {
    "code": "200",
    "data": {
    "content": [
    {
    "createTime": 1579156726000,
    "id": "1",
    "name": "测试1",
    "state": 0,
    "time": 1579104000000,
    "title": "测试标题1",
    "updateTime": 1579156726000
    }
    ],
    "empty": false,
    "first": true,
    "last": true,
    "number": 0,
    "numberOfElements": 4,
    "pageable": {
    "offset": "0",
    "pageNumber": 0,
    "pageSize": 20,
    "paged": true,
    "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
    },
    "unpaged": false
    },
    "size": 20,
    "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
    },
    "totalElements": "4",
    "totalPages": 1
    },
    "message": "successed",
    "success": true
    }
     */
    @PostMapping("findByPage")
    public ResponseVO findByPage(@RequestBody PageVo pageVo){
        Page<Test> test = testService.findByPage(pageVo);
        return getFromData(test);
    }
}
