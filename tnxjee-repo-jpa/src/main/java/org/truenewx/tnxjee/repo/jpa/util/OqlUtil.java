package org.truenewx.tnxjee.repo.jpa.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.model.query.FieldOrder;
import org.truenewx.tnxjee.model.query.QuerySort;

/**
 * 对象查询语言(OQL)工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class OqlUtil {

    private OqlUtil() {
    }

    /**
     * 根据指定查询排序序列构建order by子句<br/>
     * 如果无排序设置，则返回""，否则返回以空格开头的形如：" order by ..."的order by子句
     *
     * @param orders 查询排序序列
     * @return order by子句
     */
    public static String buildOrderString(Iterable<Entry<String, Boolean>> orders) {
        StringBuffer orderBy = new StringBuffer();
        if (orders != null) {
            for (Entry<String, Boolean> entry : orders) {
                orderBy.append(Strings.COMMA).append(entry.getKey());
                if (entry.getValue() == Boolean.TRUE) {
                    orderBy.append(" desc");
                }
            }
        }
        if (orderBy.length() > 0) {
            orderBy.replace(0, Strings.COMMA.length(), Strings.SPACE); // 用空格替代第一个逗号
            orderBy.insert(0, " order by"); // 前面加order by
        }
        return orderBy.toString();
    }

    public static String buildOrderString(QuerySort sort) {
        StringBuffer orderBy = new StringBuffer();
        if (sort != null) {
            List<FieldOrder> orders = sort.getOrders();
            if (orders != null) {
                orders.forEach(order -> {
                    orderBy.append(Strings.COMMA).append(order.getName());
                    if (order.isDesc()) {
                        orderBy.append(" desc");
                    }
                });
            }
        }
        if (orderBy.length() > 0) {
            orderBy.replace(0, Strings.COMMA.length(), Strings.SPACE); // 用空格替代第一个逗号
            orderBy.insert(0, " order by"); // 前面加order by
        }
        return orderBy.toString();
    }

    /**
     * 构建OR条件子句
     *
     * @param params           查询参数映射集，相关查询参数会写入该映射集中
     * @param fieldName        字段名
     * @param fieldParamValues 字段参数值
     * @param comparison       条件比较符
     * @return OR条件子句
     *
     * @author jianglei
     */
    public static String buildOrConditionString(Map<String, Object> params, String fieldName,
            Collection<?> fieldParamValues, Comparison comparison) {
        StringBuffer condition = new StringBuffer();
        if (fieldParamValues != null && fieldParamValues.size() > 0) {
            if (comparison == null) { // 默认为等于比较符
                comparison = Comparison.EQUAL;
            }
            // 等于和不等于在参数个数大于5后使用IN/NOT IN代替
            if ((comparison == Comparison.EQUAL || comparison == Comparison.NOT_EQUAL)
                    && fieldParamValues.size() > 5) {
                condition.append(fieldName);
                if (comparison == Comparison.EQUAL) {
                    condition.append(Comparison.IN.toQlString());
                } else {
                    condition.append(Comparison.NOT_IN.toQlString());
                }
                String paramName = fieldName.replaceAll("\\.", Strings.UNDERLINE);
                condition.append(Strings.LEFT_BRACKET).append(Strings.COLON).append(paramName)
                        .append(Strings.RIGHT_BRACKET);
                params.put(paramName, fieldParamValues);
            } else {
                String junction = comparison.isNot() ? " and " : " or ";
                int i = 0;
                for (Object fieldParamValue : fieldParamValues) {
                    condition.append(junction).append(fieldName);
                    if (fieldParamValue != null) { // 忽略为null的参数值
                        String paramName = fieldName.replaceAll("\\.", Strings.UNDERLINE) + (i++);
                        condition.append(comparison.toQlString()).append(Strings.COLON)
                                .append(paramName);
                        if (comparison == Comparison.LIKE || comparison == Comparison.NOT_LIKE) {
                            params.put(paramName, StringUtils.join(Strings.PERCENT,
                                    fieldParamValue.toString(), Strings.PERCENT));
                        } else {
                            params.put(paramName, fieldParamValue);
                        }
                    }
                }
                if (fieldParamValues.size() == 1) { // 一个字段参数不需要添加括号
                    condition.delete(0, junction.length());
                } else {
                    condition.replace(0, junction.length(), Strings.LEFT_BRACKET)
                            .append(Strings.RIGHT_BRACKET); // 去掉多余的or后添加括号
                }
            }
        }
        return condition.toString();
    }

    /**
     * 构建OR条件子句
     *
     * @param params           查询参数映射集，相关查询参数会写入该映射集中
     * @param fieldName        字段名
     * @param fieldParamValues 字段参数值
     * @param comparison       条件比较符
     * @return OR条件子句
     *
     * @author jianglei
     */
    public static String buildOrConditionString(Map<String, Object> params, String fieldName,
            Object[] fieldParamValues, Comparison comparison) {
        StringBuffer condition = new StringBuffer();
        if (fieldParamValues != null && fieldParamValues.length > 0) {
            if (comparison == null) { // 默认为等于比较符
                comparison = Comparison.EQUAL;
            }
            // 等于和不等于在参数个数大于5后使用IN/NOT IN代替
            if ((comparison == Comparison.EQUAL || comparison == Comparison.NOT_EQUAL)
                    && fieldParamValues.length > 5) {
                condition.append(fieldName);
                if (comparison == Comparison.EQUAL) {
                    condition.append(Comparison.IN.toQlString());
                } else {
                    condition.append(Comparison.NOT_IN.toQlString());
                }
                String paramName = fieldName.replaceAll("\\.", Strings.UNDERLINE);
                condition.append(Strings.LEFT_BRACKET).append(Strings.COLON).append(paramName)
                        .append(Strings.RIGHT_BRACKET);
                params.put(paramName, fieldParamValues);
            } else {
                String junction = comparison.isNot() ? " and " : " or ";
                int i = 0;
                for (Object fieldParamValue : fieldParamValues) {
                    condition.append(junction).append(fieldName);
                    if (fieldParamValue != null) { // 忽略为null的参数值
                        String paramName = fieldName.replaceAll("\\.", Strings.UNDERLINE) + (i++);
                        condition.append(comparison.toQlString()).append(Strings.COLON)
                                .append(paramName);
                        if (comparison == Comparison.LIKE || comparison == Comparison.NOT_LIKE) {
                            params.put(paramName, StringUtils.join(Strings.PERCENT,
                                    fieldParamValue.toString(), Strings.PERCENT));
                        } else {
                            params.put(paramName, fieldParamValue);
                        }
                    }
                }
                if (fieldParamValues.length == 1) { // 一个字段参数不需要添加括号
                    condition.delete(0, junction.length());
                } else {
                    condition.replace(0, junction.length(), Strings.LEFT_BRACKET)
                            .append(Strings.RIGHT_BRACKET); // 去掉多余的or后添加括号
                }
            }
        }
        return condition.toString();
    }

    public static String buildOrConditionString(Map<String, Object> params, String fieldName,
            int[] fieldParamValues, Comparison comparison) {
        return buildOrConditionString(params, fieldName, ArrayUtil.toIntegerArray(fieldParamValues),
                comparison);
    }

    public static String buildOrConditionString(Map<String, Object> params, String fieldName,
            long[] fieldParamValues, Comparison comparison) {
        return buildOrConditionString(params, fieldName, ArrayUtil.toLongArray(fieldParamValues),
                comparison);
    }

    /**
     * 构建指定字段的为null条件子句
     *
     * @param fieldName 字段名
     * @param ifNull    是否为null，其值本身为null表示忽略该字段条件
     * @return 条件子句
     */
    public static String buildNullConditionString(String fieldName, Boolean ifNull) {
        StringBuffer condition = new StringBuffer();
        if (ifNull != null) {
            condition.append(fieldName).append(" is");
            if (!ifNull) {
                condition.append(" not");
            }
            condition.append(" null");
        }
        return condition.toString();
    }
}
