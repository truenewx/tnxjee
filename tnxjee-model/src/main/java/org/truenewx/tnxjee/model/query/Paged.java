package org.truenewx.tnxjee.model.query;

import java.io.Serializable;

/**
 * 分页结果
 *
 * @author jianglei
 */
public class Paged implements Serializable {

    private static final long serialVersionUID = 2748051722289562458L;

    private int pageSize;
    private int pageNo;
    private QuerySort sort;
    private Long total;
    private boolean morePage;

    public Paged(int pageSize, int pageNo, long total) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.total = total;
        this.morePage = (pageSize * pageNo) < total;
    }

    public Paged(int pageSize, int pageNo, long total, QuerySort sort) {
        this(pageSize, pageNo, total);
        this.sort = sort;
    }

    public Paged(int pageSize, int pageNo, boolean morePage) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.morePage = morePage;
    }

    public Paged(int pageSize, int pageNo, boolean morePage, QuerySort sort) {
        this(pageSize, pageNo, morePage);
        this.sort = sort;
    }

    public static Paged of(Paging paging, long total) {
        return new Paged(paging.getPageSize(), paging.getPageNo(), total, paging.getSort());
    }

    public static Paged of(Paging paging, boolean morePage) {
        return new Paged(paging.getPageSize(), paging.getPageNo(), morePage, paging.getSort());
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public QuerySort getSort() {
        return this.sort;
    }

    public Long getTotal() {
        return this.total;
    }

    public boolean isMorePage() {
        return this.morePage;
    }

    public boolean isPageable() {
        return this.pageSize > 0;
    }

    public boolean isCountable() {
        return this.total != null && this.total >= 0;
    }

    public int getPageCount() {
        if (isPageable()) {
            if (isCountable()) {
                int pageCount = (int) (this.total / this.pageSize);
                if (this.total % this.pageSize != 0) {
                    pageCount++;
                }
                return pageCount;
            } else if (!this.morePage) { // 无总数但没有更多页时，当前页码即为总页数
                return this.pageNo;
            }
        }
        return 0;
    }

    public int getPreviousPage() {
        return this.pageNo <= 1 ? 1 : (this.pageNo - 1);
    }

    public int getNextPage() {
        int pageCount = getPageCount();
        return (this.total >= 0 && this.pageNo >= pageCount) ? pageCount : (this.pageNo + 1);
    }

}
