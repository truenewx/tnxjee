package org.truenewx.tnxjee.model.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * 分页查询结果
 *
 * @param <T> 结果记录类型
 * @author jianglei
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

    public static <T> QueryResult<T> of(List<T> records, int pageSize, int pageNo, Long total) {
        if (pageSize <= 0) {
            pageSize = records.size();
            pageNo = 1;
        }
        Paged paged;
        if (total != null) {
            paged = new Paged(pageSize, pageNo, total);
        } else {
            boolean morePage = records.size() > pageSize;
            while (records.size() > pageSize) { // 确保结果数据数目不大于页大小
                records.remove(records.size() - 1);
            }
            paged = new Paged(pageSize, pageNo, morePage);
        }
        return new QueryResult<>(records, paged);
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

    public <R> QueryResult<R> transfer(Function<T, R> function) {
        List<R> list = new ArrayList<>();
        this.records.forEach(record -> {
            list.add(function.apply(record));
        });
        return new QueryResult<>(list, this.paged);
    }

}