package org.truenewx.tnxjee.model.query;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 分页查询条件。通过创建子类附带更多的查询条件
 *
 * @author jianglei
 */
public abstract class Querying extends Pagination implements QueryModel, Paging {

    private static final long serialVersionUID = -3979291396866569456L;
    private static final String DESC = "desc";

    private QueryIgnoring ignoring;

    public Querying() {
    }

    public Querying(int pageSize, int pageNo) {
        super(pageSize, pageNo);
    }

    public Querying(int pageSize, int pageNo, List<FieldOrder> orders) {
        super(pageSize, pageNo, orders);
    }

    @Override
    public QueryIgnoring getIgnoring() {
        return this.ignoring;
    }

    public void setIgnoring(QueryIgnoring ignoring) {
        this.ignoring = ignoring;
    }

    //////

    @Override
    @JsonIgnore // 避免JSON序列化复杂的集合属性，通过下面的动态orderBy属性实现字段排序传递
    public List<FieldOrder> getOrders() {
        return super.getOrders();
    }

    public void setOrderBy(String orderBy) {
        if (StringUtils.isNotBlank(orderBy)) {
            String[] orders = orderBy.split(Strings.COMMA);
            for (String order : orders) {
                order = order.trim();
                if (order.length() > 0) {
                    String fieldName;
                    boolean desc = false;
                    int index = order.indexOf(Strings.SPACE);
                    if (index > 0) {
                        fieldName = order.substring(0, index);
                        desc = DESC.equalsIgnoreCase(order.substring(index + 1));
                    } else {
                        fieldName = order;
                    }
                    addOrder(fieldName, desc);
                }
            }
        }
    }

    public String getOrderBy() {
        return toOrderBy(getOrders());
    }

    /**
     * 将指定查询排序序列转换排序语句，不含order by<br/>
     * 如果无排序设置，则返回空字符串
     *
     * @param orders 查询排序序列
     * @return 排序语句
     */
    public static String toOrderBy(Collection<FieldOrder> orders) {
        StringBuilder orderString = new StringBuilder();
        for (FieldOrder order : orders) {
            orderString.append(Strings.COMMA).append(order.getName());
            if (order.isDesc()) {
                orderString.append(Strings.SPACE).append(DESC);
            }
        }
        if (orderString.length() > 0) {
            orderString.deleteCharAt(0); // 去掉首位的多余逗号
        }
        return orderString.toString();
    }

}
