package org.truenewx.tnxjee.repo.jpa.codegen;

import org.truenewx.tnxjee.core.Strings;

/**
 * 基于模型的生成器
 *
 * @author jianglei
 */
public abstract class ModelBasedGenerator {

    protected String modelBasePackage;

    public ModelBasedGenerator(String modelBasePackage) {
        this.modelBasePackage = modelBasePackage;
    }

    protected final String getModule(Class<?> clazz) {
        String packageName = clazz.getPackageName();
        if (packageName.length() > this.modelBasePackage.length()) {
            String module = packageName.substring(this.modelBasePackage.length() + 1);
            int index = module.indexOf(Strings.DOT);
            if (index > 0) { // 如果有多级子包，也只取基础包的直接下级包作为模块名
                module = module.substring(0, index);
            }
            return module;
        }
        return null;
    }

}
