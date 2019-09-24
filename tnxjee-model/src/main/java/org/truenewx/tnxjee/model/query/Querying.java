package org.truenewx.tnxjee.model.query;

/**
 * 分页查询条件。通过创建子类附带更多的查询条件
 *
 * @author jianglei
 */
public abstract class Querying {

    private Paging paging = new Paging();
    private boolean totalable = true;
    private boolean listable = true;

    public Paging getPaging() {
        return this.paging;
    }

    public void setPaging(Paging paging) {
        if (paging == null) { // 确保paging不为null
            paging = new Paging();
        }
        this.paging = paging;
    }

    public void setPageSize(int pageSize) {
        this.paging.setPageSize(pageSize);
    }

    public void setPageNo(int pageNo) {
        this.paging.setPageNo(pageNo);
    }

    public boolean isTotalable() {
        return this.totalable;
    }

    public void setTotalable(boolean totalable) {
        this.totalable = totalable;
    }

    public boolean isListable() {
        return this.listable;
    }

    public void setListable(boolean listable) {
        this.listable = listable;
    }

    public void addOrder(String fieldName, Boolean desc) {
        QuerySort sort = this.paging.getSort();
        if (sort == null) {
            sort = new QuerySort();
            this.paging.setSort(sort);
        }
        sort.addOrder(fieldName, desc);
    }

}
