package org.truenewx.tnxjee.model.query;

import java.io.Serializable;

/**
 * 查询参数。通过创建子类附带更多的查询条件
 *
 * @author jianglei
 */
public abstract class QueryParam implements Serializable {

    private static final long serialVersionUID = 1766849422293534482L;

    private Paging paging;
    private boolean totalable = true;
    private boolean listable = true;

    public Paging getPaging() {
        return this.paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
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

}
