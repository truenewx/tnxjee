package org.truenewx.tnxjee.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.CaptionUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.core.Entity;
import org.truenewx.tnxjee.model.exception.BusinessException;
import org.truenewx.tnxjee.repo.Repo;
import org.truenewx.tnxjee.service.api.Service;

/**
 * 抽象服务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T> 关联类型
 */
public abstract class AbstractService<T extends Entity> extends ServiceSupport implements Service {

    protected Class<T> getEntityClass() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

    protected Repo<T> getRepo() {
        return getRepo(getEntityClass());
    }

    /**
     * 确保指定实体非null
     *
     * @param entity 实体
     *
     * @return 非null的实体
     */
    protected T ensureNotNull(T entity) {
        if (entity == null) { // 如果此时实体为null，则使用实体类的构造函数创建一个新的实体
            Class<T> entityClass = getEntityClass();
            entity = ClassUtil.newInstance(entityClass);
        }
        return entity;
    }

    /**
     * 断言指定实体不为null
     *
     * @param entity 实体
     * @throws 如果实体为null
     */
    protected void assertNotNull(T entity) {
        if (entity == null) {
            String code = getNonexistentErorrCode();
            if (code != null) {
                throw new BusinessException(code);
            }
            // 子类未指定实体不存在的异常错误码，则使用默认的异常消息
            Class<?> entityClass = getEntityClass();
            String entityCaption = CaptionUtil.getCaption(entityClass, null);
            if (StringUtils.isBlank(entityCaption)) {
                entityCaption = entityClass.getSimpleName();
            }
            throw new BusinessException(ServiceExceptionCodes.NONEXISTENT_ENTITY, entityCaption);
        }
    }

    /**
     * 获取实体不存在的错误码，在load()方法中无法取得实体时抛出异常用，默认返回null，使用默认的错误消息
     *
     * @return 实体不存在的错误码
     */
    protected String getNonexistentErorrCode() {
        return null;
    }

}
