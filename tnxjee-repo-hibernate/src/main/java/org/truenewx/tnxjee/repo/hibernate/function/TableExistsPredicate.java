package org.truenewx.tnxjee.repo.hibernate.function;

import java.util.function.BiPredicate;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;

/**
 * 表存在断言
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface TableExistsPredicate extends BiPredicate<DataSource, String> {

    Class<? extends Dialect> getDialectClass();

}
