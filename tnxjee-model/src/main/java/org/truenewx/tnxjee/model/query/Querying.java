package org.truenewx.tnxjee.model.query;

import org.springframework.util.Assert;

/**
 * 分页查询条件。通过创建子类附带更多的查询条件
 *
 * @author jianglei
 */
public abstract class Querying implements QueryModel {

    private Paging paging = new Paging();
    private boolean totalable = true;
    private boolean listable = true;

    @Override
    public Paging getPaging() {
        return this.paging;
    }

    public void setPaging(Paging paging) {
        Assert.notNull(paging, "paging must not be null");
        this.paging = paging;
    }

    public void setPageSize(int pageSize) {
        this.paging.setPageSize(pageSize);
    }

    public void setPageNo(int pageNo) {
        this.paging.setPageNo(pageNo);
    }

    public void setSort(QuerySort sort) {
        this.paging.setSort(sort);
    }

    @Override
    public boolean isTotalable() {
        return this.totalable;
    }

    public void setTotalable(boolean totalable) {
        this.totalable = totalable;
    }

    @Override
    public boolean isListable() {
        return this.listable;
    }

    public void setListable(boolean listable) {
        this.listable = listable;
    }

    public void addOrder(String fieldName, Boolean desc) {
        this.paging.addOrder(fieldName, desc);
    }

}
