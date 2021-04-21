package org.truenewx.tnxjee.repo.jpa.codegen;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.codegen.ClassGeneratorSupport;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.entity.relation.Relation;
import org.truenewx.tnxjee.model.entity.relation.RelationKey;
import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;
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

    private String ownedUnityBaseTemplateLocation = "META-INF/template/owned-unity-repo.ftl";
    private String ownedUnityExtTemplateLocation = "META-INF/template/owned-unity-repox.ftl";
    private String ownedUnityImplTemplateLocation = "META-INF/template/owned-unity-repo-impl.ftl";

    private String relationBaseTemplateLocation = "META-INF/template/relation-repo.ftl";
    private String relationExtTemplateLocation = "META-INF/template/relation-repox.ftl";
    private String relationImplTemplateLocation = "META-INF/template/relation-repo-impl.ftl";

    public JpaRepoGeneratorImpl(String modelBasePackage, String targetBasePackage) {
        super(modelBasePackage, targetBasePackage);
    }

    @Override
    public void generate(String... modules) throws Exception {
        generate(this.modelBasePackage, (module, entityClass) -> {
            try {
                generate(module, entityClass, true); // 默认均生成实现类，多余的可手工删除
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
            Class<?> keyClass = ClassUtil.getActualGenericType(entityClass, Unity.class, 0);
            putClassName(params, keyClass, "key");
            if (OwnedUnity.class.isAssignableFrom(entityClass)) { // 从属单体一定需要生成实现类
                Class<?> ownerClass = ClassUtil.getActualGenericType(entityClass, OwnedUnity.class, 1);
                putClassName(params, ownerClass, "owner");
                String repoxClassSimpleName = generate(module, entityClass, params, this.ownedUnityExtTemplateLocation,
                        "x");
                params.put("repoxClassSimpleName", repoxClassSimpleName);
                generate(module, entityClass, params, this.ownedUnityImplTemplateLocation, "Impl");
                generate(module, entityClass, params, this.ownedUnityBaseTemplateLocation, Strings.EMPTY);
            } else {
                if (withImpl) {
                    String repoxClassSimpleName = generate(module, entityClass, params, this.unityExtTemplateLocation,
                            "x");
                    params.put("repoxClassSimpleName", repoxClassSimpleName);
                    generate(module, entityClass, params, this.unityImplTemplateLocation, "Impl");
                }
                generate(module, entityClass, params, this.unityBaseTemplateLocation, Strings.EMPTY);
            }
        } else if (Relation.class.isAssignableFrom(entityClass)) { // 关系实体一定需要生成实现类
            Class<?> leftKeyClass = ClassUtil.getActualGenericType(entityClass, Relation.class, 0);
            putClassName(params, leftKeyClass, "leftKey");
            Class<?> rightKeyClass = ClassUtil.getActualGenericType(entityClass, Relation.class, 1);
            putClassName(params, rightKeyClass, "rightKey");
            String repoxClassSimpleName = generate(module, entityClass, params, this.relationExtTemplateLocation,
                    "x");
            params.put("repoxClassSimpleName", repoxClassSimpleName);
            generate(module, entityClass, params, this.relationBaseTemplateLocation, Strings.EMPTY);
            Field keyField = ClassUtil.findField(entityClass, RelationKey.class);
            Binate<Field, Field> keyFieldBinate = getRelationKeyField(keyField.getType());
            String keyPropertyName = keyField.getName();
            String leftKeyPropertyName = keyPropertyName + Strings.DOT + keyFieldBinate.getLeft().getName();
            params.put("leftKeyPropertyName", leftKeyPropertyName);
            String rightKeyPropertyName = keyPropertyName + Strings.DOT + keyFieldBinate.getRight().getName();
            params.put("rightKeyPropertyName", rightKeyPropertyName);
            generate(module, entityClass, params, this.relationImplTemplateLocation, "Impl");
        }
    }

    private void putClassName(Map<String, Object> params, Class<?> keyClass, String paramNamePrefix) {
        params.put(paramNamePrefix + "ClassSimpleName", keyClass.getSimpleName());
        String keyClassName = getImportedClassName(keyClass);
        if (keyClassName != null) {
            params.put(paramNamePrefix + "ClassName", keyClassName);
        }
    }

    private String getImportedClassName(Class<?> clazz) {
        if (clazz.isPrimitive() || "java.lang".equals(clazz.getPackageName())) {
            return null;
        }
        return clazz.getName();
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

    private Binate<Field, Field> getRelationKeyField(Class<?> relationKeyClass) {
        Field leftField = null;
        Field rightField = null;
        Field[] fields = relationKeyClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                if (leftField == null) {
                    leftField = field;
                } else if (rightField == null) {
                    rightField = field;
                } else {
                    break;
                }
            }
        }
        return new Binary<>(leftField, rightField);
    }

}
