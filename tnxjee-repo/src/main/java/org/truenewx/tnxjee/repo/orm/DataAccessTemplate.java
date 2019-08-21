package org.truenewx.tnxjee.repo.orm;

import java.util.List;
import java.util.Map;

/**
 * 数据访问模板，除查询语句外，还能执行更新语句
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class DataAccessTemplate extends DataQueryTemplate {

    public abstract int update(CharSequence ul, String paramName, Object paramValue);

    public abstract int update(CharSequence ul, Map<String, ?> params);

    public abstract int update(CharSequence ul, List<?> params);

    public final int update(CharSequence ul) {
        return update(ul, (Map<String, ?>) null);
    }

}
