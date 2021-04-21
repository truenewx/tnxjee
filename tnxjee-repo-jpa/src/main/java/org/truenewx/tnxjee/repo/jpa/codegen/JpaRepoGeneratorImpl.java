package org.truenewx.tnxjee.repo.jpa.codegen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.codegen.ClassGeneratorSupport;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * JPA Repo类生成器实现
 *
 * @author jianglei
 */
public class JpaRepoGeneratorImpl extends ClassGeneratorSupport implements JpaRepoGenerator {

    private String unityBaseTemplateLocation = "META-INF/template/unity-repo.ftl";
    private String unityExtTemplateLocation = "META-INF/template/unity-repox.ftl";
    private String unityImplTemplateLocation = "META-INF/template/unity-repo-impl.ftl";

    public JpaRepoGeneratorImpl(String modelBasePackage, String targetBasePackage) {
        super(modelBasePackage, targetBasePackage);
    }

    @Override
    public void generate(String... modules) throws Exception {
        generate(this.modelBasePackage, (module, entityClass) -> {
            try {
                generate(module, entityClass, false); // 默认不生成实现类
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, modules);
    }

    @Override
    public void generate(Class<? extends Entity> entityClass, boolean withImpl) throws Exception {
        String module = getModule(entityClass);
        generate(module, entityClass, withImpl);
    }

    private void generate(String module, Class<? extends Entity> entityClass, boolean withImpl) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (Unity.class.isAssignableFrom(entityClass)) {
            String keyClassSimpleName = ClassUtil.getActualGenericType(entityClass, Unity.class, 0).getSimpleName();
            params.put("keyClassSimpleName", keyClassSimpleName);
            if (withImpl) {
                String repoxClassSimpleName = generate(module, entityClass, params, this.unityExtTemplateLocation, "x");
                params.put("repoxClassSimpleName", repoxClassSimpleName);
                generate(module, entityClass, params, this.unityImplTemplateLocation, "Impl");
            }
            generate(module, entityClass, params, this.unityBaseTemplateLocation, Strings.EMPTY);
        }
    }

    private String generate(String module, Class<? extends Entity> entityClass, Map<String, Object> params,
            String location, String repoClassNameSuffix) throws IOException {
        String packageName = getTargetModulePackageName(module);
        String entityClassSimpleName = entityClass.getSimpleName();
        String repoClassSimpleName = entityClassSimpleName + "Repo" + repoClassNameSuffix;
        String repoClassName = packageName + Strings.DOT + repoClassSimpleName;
        params.put("packageName", packageName);
        params.put("entityClassName", entityClass.getName());
        params.put("repoClassSimpleName", repoClassSimpleName);
        params.put("entityClassSimpleName", entityClassSimpleName);
        generate(repoClassName, location, params);
        return repoClassSimpleName;
    }

}
