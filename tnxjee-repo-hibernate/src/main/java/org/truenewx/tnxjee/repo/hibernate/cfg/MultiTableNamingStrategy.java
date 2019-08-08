package org.truenewx.tnxjee.repo.hibernate.cfg;

import javax.sql.DataSource;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.repo.hibernate.function.TableExistsPredicate;

/**
 * 多表名映射命名策略
 *
 * @author jianglei
 */
public class MultiTableNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private static final long serialVersionUID = -5851271869133476909L;

    private DataSource dataSource;
    private TableExistsPredicate predicate;

    public MultiTableNamingStrategy(DataSource dataSource, TableExistsPredicate predicate) {
        Assert.notNull(dataSource, "dataSource must be not null");
        this.dataSource = dataSource;
        Assert.notNull(predicate, "predicate must be not null");
        this.predicate = predicate;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        String[] names = name.getText().split(Strings.COMMA);
        if (names.length > 1) {
            for (String n : names) {
                n = n.trim();
                if (this.predicate.test(this.dataSource, n)) {
                    return new Identifier(n, name.isQuoted());
                }
            }
        }
        return super.toPhysicalTableName(name, context);
    }
}
