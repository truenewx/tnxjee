package org.truenewx.tnxjee.core.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.CollectionUtils;

/**
 * 过滤符号集
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FilteredTokens {
    private Set<String> includes;
    private Set<String> excludes;

    public Set<String> getIncludes() {
        return this.includes;
    }

    public Set<String> getExcludes() {
        return this.excludes;
    }

    /**
     * 添加包含的符号集
     *
     * @param properties
     *            包含的符号集
     */
    public void addIncluded(final String... properties) {
        if (this.includes == null) {
            this.includes = new HashSet<>();
        }
        for (final String property : properties) {
            this.includes.add(property);
        }
    }

    /**
     * 添加排除的符号集
     *
     * @param properties
     *            排除的符号集
     */
    public void addExcluded(final String... properties) {
        if (this.excludes == null) {
            this.excludes = new HashSet<>();
        }
        for (final String property : properties) {
            this.excludes.add(property);
        }
    }

    /**
     *
     * @return 包含符号集和排除符号集是否均为空
     */
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.includes) && CollectionUtils.isEmpty(this.excludes);
    }
}
