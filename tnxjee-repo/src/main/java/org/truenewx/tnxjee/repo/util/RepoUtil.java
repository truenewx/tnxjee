package org.truenewx.tnxjee.repo.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.truenewx.tnxjee.model.query.FieldOrder;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.QuerySort;

/**
 * Repo工具类
 *
 * @author jianglei
 */
public class RepoUtil {

    private RepoUtil() {
    }

    /**
     * 默认模式名
     */
    public static final String DEFAULT_SCHEMA_NAME = "default";

    public static Order toOrder(FieldOrder fieldOrder) {
        if (fieldOrder == null) {
            return null;
        }
        if (fieldOrder.isDesc()) {
            return Order.desc(fieldOrder.getName());
        } else {
            return Order.asc(fieldOrder.getName());
        }
    }

    public static Sort toSort(QuerySort querySort) {
        if (querySort == null) {
            return Sort.unsorted();
        }
        List<Order> orders = new ArrayList<>();
        List<FieldOrder> fieldOrders = querySort.getOrders();
        if (fieldOrders != null) {
            fieldOrders.forEach(fieldOrder -> {
                orders.add(toOrder(fieldOrder));
            });
        }
        return Sort.by(orders);
    }

    public static Pageable toPageable(Paging paging) {
        if (paging == null || paging.getPageSize() <= 0) {
            return Pageable.unpaged();
        }
        Sort sort = toSort(paging.getSort());
        int pageNo = paging.getPageNo() - 1; // Pageable的页码从0开始计数
        if (pageNo < 0) {
            pageNo = 0;
        }
        return PageRequest.of(pageNo, paging.getPageSize(), sort);
    }

    public static FieldOrder toFieldOrder(Order order) {
        if (order == null) {
            return null;
        }
        return new FieldOrder(order.getProperty(), order.isDescending());
    }

    public static QuerySort toQuerySort(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return null;
        }
        List<FieldOrder> fieldOrders = new ArrayList<>();
        sort.forEach(order -> {
            fieldOrders.add(toFieldOrder(order));
        });
        return new QuerySort(fieldOrders);
    }

    public static Paging toPaging(Pageable pageable) {
        QuerySort querySort = toQuerySort(pageable.getSort());
        int pageNo = pageable.getPageNumber() + 1; // Paging的页码从1开始计数
        if (pageNo < 1) {
            pageNo = 1;
        }
        return new Paging(pageable.getPageSize(), pageNo, querySort);
    }

}
