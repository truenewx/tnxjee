package org.truenewx.tnxjee.repo.jpa.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.mapping.Column;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.model.definition.support.ModelPropertyLimitValueManager;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.Queried;
import org.truenewx.tnxjee.model.query.QuerySort;
import org.truenewx.tnxjee.model.query.Querying;
import org.truenewx.tnxjee.repo.jpa.JpaRepo;
import org.truenewx.tnxjee.repo.jpa.util.OqlUtil;
import org.truenewx.tnxjee.repo.support.RepoSupport;

/**
 * JPA的数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class JpaRepoSupport<T extends Entity> extends RepoSupport<T>
        implements JpaRepo<T> {

    @Autowired
    private ModelPropertyLimitValueManager propertyLimitValueManager;

    @Override
    protected JpaSchemaTemplate getSchemaTemplate() {
        return (JpaSchemaTemplate) super.getSchemaTemplate();
    }

    protected String getEntityName() {
        return getEntityClass().getName();
    }

    @Override
    public T first() {
        return getSchemaTemplate().first("from " + getEntityName(), (Map<String, Object>) null);
    }

    @Override
    public void flush() {
        ((JpaRepository<T, ?>) getRepository()).flush();
    }

    @Override
    public void refresh(T entity) {
        getSchemaTemplate().refresh(entity);
    }

    private Queried<T> query(CharSequence ql, Map<String, Object> params, int pageSize, int pageNo,
            QuerySort sort, boolean totalable, boolean listable) {
        Long total = null;
        if ((pageSize > 0 || !listable) && totalable) { // 需分页查询且需要获取总数时，才获取总数
            total = getSchemaTemplate().count("select count(*) " + ql, params);
        }

        List<T> dataList;
        // 已知总数为0或无需查询记录清单，则不查询记录清单
        if ((total != null && total == 0) || !listable) {
            dataList = new ArrayList<>();
        } else {
            String orderString = OqlUtil.buildOrderString(sort);
            if (StringUtils.isNotBlank(orderString)) {
                if (ql instanceof StringBuffer) {
                    ((StringBuffer) ql).append(orderString);
                } else {
                    ql = ql.toString() + orderString;
                }
            }
            dataList = getSchemaTemplate().list(ql, params, pageSize, pageNo);
            if (pageSize <= 0) { // 非分页查询，总数为结果记录条数
                total = (long) dataList.size();
            }
        }
        return Queried.of(dataList, pageSize, pageNo, total);
    }

    protected Queried<T> query(CharSequence ql, Map<String, Object> params, Querying querying) {
        Paging paging = querying.getPaging();
        return query(ql, params, paging.getPageSize(), paging.getPageNo(), paging.getSort(),
                querying.isTotalable(), querying.isListable());
    }

    protected Queried<T> query(CharSequence ql, Map<String, Object> params, Paging paging) {
        return query(ql, params, paging.getPageSize(), paging.getPageNo(), paging.getSort());
    }

    protected Queried<T> query(CharSequence ql, Map<String, Object> params, int pageSize,
            int pageNo, QuerySort sort) {
        return query(ql, params, pageSize, pageNo, sort, true, true);
    }

    protected final Column getColumn(String propertyName) {
        return (Column) ((MetamodelImplementor) getSchemaTemplate().getEntityManager()
                .getMetamodel()).getTypeConfiguration().getMetadataBuildingContext()
                        .getMetadataCollector().getEntityBinding(getEntityName())
                        .getProperty(propertyName).getColumnIterator().next();
    }

    private Number getNumberPropertyMinValue(String propertyName) {
        Class<T> entityClass = getEntityClass();
        if (this.propertyLimitValueManager.isNonNumber(entityClass, propertyName)) {
            // 已明确知晓不是数字类型的属性，直接返回null
            return null;
        }
        Number minValue = this.propertyLimitValueManager.getMinValue(entityClass, propertyName);
        if (minValue == null) {
            Class<?> propertyClass = getPropertyClass(propertyName);
            minValue = MathUtil.minValue(propertyClass);
            if (minValue != null) { // 可从类型取得最小值，说明是数值类型
                Min min = ClassUtil.findAnnotation(getEntityClass(), propertyName, Min.class);
                if (min != null) {
                    minValue = min.value();
                } else {
                    DecimalMin decimalMin = ClassUtil.findAnnotation(getEntityClass(), propertyName,
                            DecimalMin.class);
                    if (decimalMin != null) {
                        minValue = MathUtil.parseDecimal(decimalMin.value(), null);
                    }
                }

                Metamodel metamodel = getSchemaTemplate().getEntityManager().getMetamodel();
                metamodel.getManagedTypes().forEach(type -> {

                });
                @SuppressWarnings("unchecked")
                Class<? extends Number> type = (Class<? extends Number>) propertyClass;
                Column column = getColumn(propertyName);
                int precision = column.getPrecision();
                int scale = column.getScale();
                Number minValue2 = MathUtil.minValue(type, precision, scale);
                // 两个最小值中的较大者，才是实际允许的最小值
                if (minValue2.doubleValue() > minValue.doubleValue()) {
                    minValue = minValue2;
                }
            }
            this.propertyLimitValueManager.putMinValue(entityClass, propertyName, minValue);
        }
        return minValue;
    }

    private Number getNumberPropertyMaxValue(String propertyName) {
        Class<T> entityClass = getEntityClass();
        if (this.propertyLimitValueManager.isNonNumber(entityClass, propertyName)) {
            // 已明确知晓不是数字类型的属性，直接返回null
            return null;
        }
        Number maxValue = this.propertyLimitValueManager.getMaxValue(entityClass, propertyName);
        if (maxValue == null) {
            Class<?> propertyClass = getPropertyClass(propertyName);
            maxValue = MathUtil.maxValue(propertyClass);
            if (maxValue != null) { // 可从类型取得最大值，说明是数值类型
                Max max = ClassUtil.findAnnotation(getEntityClass(), propertyName, Max.class);
                if (max != null) {
                    maxValue = max.value();
                } else {
                    DecimalMax decimalMax = ClassUtil.findAnnotation(getEntityClass(), propertyName,
                            DecimalMax.class);
                    if (decimalMax != null) {
                        maxValue = MathUtil.parseDecimal(decimalMax.value(), null);
                    }
                }

                @SuppressWarnings("unchecked")
                Class<? extends Number> type = (Class<? extends Number>) propertyClass;
                Column column = getColumn(propertyName);
                int precision = column.getPrecision();
                int scale = column.getScale();
                Number maxValue2 = MathUtil.maxValue(type, precision, scale);
                // 两个最大值中的较小者，才是实际允许的最大值
                if (maxValue2.doubleValue() < maxValue.doubleValue()) {
                    maxValue = maxValue2;
                }
            }
            this.propertyLimitValueManager.putMaxValue(entityClass, propertyName, maxValue);
        }
        return maxValue;
    }

    protected final boolean doIncreaseNumber(StringBuffer ql, Map<String, Object> params,
            String propertyName, double stepValue) {
        if (stepValue < 0) { // 增量为负时需限定最小值
            Number minValue = getNumberPropertyMinValue(propertyName);
            if (minValue == null) { // 无法取得属性类型最小值，说明属性不是数值类型
                return false;
            }
            ql.append(" and ").append(propertyName).append("+:step>=:minValue");
            params.put("minValue", minValue);
        } else { // 增量为正时需限定最大值
            Number maxValue = getNumberPropertyMaxValue(propertyName);
            if (maxValue == null) { // 无法取得属性类型最大值，说明属性不是数值类型
                return false;
            }
            ql.append(" and ").append(propertyName).append("+:step<=:maxValue");
            params.put("maxValue", maxValue);
        }
        return getSchemaTemplate().update(ql, params) > 0;
    }
}
