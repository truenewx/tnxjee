package org.truenewx.tnxjee.model.query;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 查询结果实现
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T> 结果记录类型
 */
public class QueryResult<T> implements Iterable<T> {

    private List<T> records;
    private Paged paged;

    public QueryResult(List<T> records, Paged paged) {
        if (records == null) {
            this.records = Collections.emptyList();
        } else {
            this.records = records;
        }
        this.paged = paged;
    }

    public static <T> QueryResult<T> of(List<T> records, int pageSize, int pageNo, long total) {
        Paged paged = Paged.of(pageSize, pageNo, total);
        return new QueryResult<T>(records, paged);
    }

    /**
     * 构建未知总数的查询结果
     *
     * @param records  结果记录清单
     * @param pageSize 页大小
     * @param pageNo   页码
     */
    public static <T> QueryResult<T> of(List<T> records, int pageSize, int pageNo) {
        if (pageSize <= 0) {
            pageSize = records.size();
            pageNo = 1;
        }
        Paging paging = new Paging(pageSize, pageNo);
        boolean morePage = records.size() > pageSize;
        Paged paged = new Paged(paging, morePage);
        while (morePage) { // 确保结果数据数目不大于页大小
            records.remove(records.size() - 1);
        }
        return new QueryResult<T>(records, paged);
    }

    public List<T> getRecords() {
        return this.records;
    }

    public Paged getPaged() {
        return this.paged;
    }

    @Override
    public Iterator<T> iterator() {
        return this.records.iterator();
    }

}
