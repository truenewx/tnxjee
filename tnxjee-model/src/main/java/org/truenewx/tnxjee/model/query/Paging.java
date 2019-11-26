package org.truenewx.tnxjee.model.query;

import java.io.Serializable;

/**
 * 分页请求
 *
 * @author jianglei
 */
public class Paging implements Serializable {

    private static final long serialVersionUID = 5918877416013992553L;

    private int pageSize;
    private int pageNo = 1;
    private QuerySort sort;

    public Paging() {
    }

    public Paging(int pageSize, int pageNo) {
        setPageSize(pageSize);
        setPageNo(pageNo);
    }

    public Paging(int pageSize, int pageNo, QuerySort sort) {
        this(pageSize, pageNo);
        this.sort = sort;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * @param pageSize 页大小。最小为0，表示不分页；小于0的赋值会被强制视为0
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize < 0 ? 0 : pageSize;
    }

    public int getPageNo() {
        return this.pageNo;
    }

    /**
     * @param pageNo 页码。从1开始计数，小于1的赋值会被强制视为1
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo <= 0 ? 1 : pageNo;
    }

    public QuerySort getSort() {
        return this.sort;
    }

    public void setSort(QuerySort sort) {
        this.sort = sort;
    }

    /**
     * 如果当前页大小未设定，则设定为指定页大小默认值
     *
     * @param pageSize 页大小默认值
     */
    public void setPageSizeDefault(int pageSize) {
        if (this.pageSize <= 0) {
            this.pageSize = pageSize;
        }
    }

    public void addOrder(String fieldName, Boolean desc) {
        if (this.sort == null) {
            this.sort = new QuerySort();
        }
        this.sort.addOrder(fieldName, desc);
    }
}
