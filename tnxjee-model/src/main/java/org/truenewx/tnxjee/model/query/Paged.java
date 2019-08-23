package org.truenewx.tnxjee.model.query;

import java.io.Serializable;

/**
 * 分页结果
 *
 * @author jianglei
 */
public class Paged implements Serializable {

    private static final long serialVersionUID = 2748051722289562458L;

    private Paging paging;
    private Long total;
    private boolean morePage;

    public Paged(Paging paging, long total) {
        this.paging = paging;
        this.total = total;
    }

    public Paged(Paging paging, boolean morePage) {
        this.paging = paging;
        this.morePage = morePage;
    }

    public static Paged of(int pageSize, int pageNo, long total) {
        return new Paged(new Paging(pageSize, pageNo), total);
    }

    public int getPageSize() {
        return this.paging.getPageSize();
    }

    public int getPageNo() {
        return this.paging.getPageNo();
    }

    public QuerySort getSort() {
        return this.paging.getSort();
    }

    public Long getTotal() {
        return this.total;
    }

    public boolean isMorePage() {
        return this.morePage;
    }

}
