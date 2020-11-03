package com.course.ymx.jwt.vo.request;

/**
 * @author yinminxin
 * @description 分页统用参数Vo
 * @date 2019/10/28 16:10
 */
public class PageVo {

    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页条数
     */
    private Integer pageSize;
    /**
     * 关键字
     */
    private String searchKey;

    public PageVo() {
    }

    public PageVo(Integer pageNum, Integer pageSize, String searchKey) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.searchKey = searchKey;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}
