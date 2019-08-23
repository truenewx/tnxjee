package org.truenewx.tnxjee.repo.support;

/**
 * 数据查询模板工厂
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public interface DataQueryTemplateFactory {
    /**
     * 获取指定模式的数据查询模板
     * 
     * @param schema
     *            模式
     * @return 数据查询模板
     */
    DataQueryTemplate getDataQueryTemplate(String schema);
}
