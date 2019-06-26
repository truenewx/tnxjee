package org.truenewx.tnxjee.core.util.function;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 函数：查找类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncFindClass implements Function<String, Class<?>> {
    /**
     * 默认实例
     */
    public static final FuncFindClass DEFAULT = new FuncFindClass();

    private static final Map<String, Class<?>> PRIMITIVE_CLASSES = new HashMap<>();
    private static final Map<String, Class<?>> WRAPPED_CLASSES = new HashMap<>();

    static {
        FuncFindClass.PRIMITIVE_CLASSES.put("void", void.class);
        FuncFindClass.PRIMITIVE_CLASSES.put("boolean", boolean.class);
        FuncFindClass.PRIMITIVE_CLASSES.put("byte", byte.class);
        FuncFindClass.PRIMITIVE_CLASSES.put("char", char.class);
        FuncFindClass.PRIMITIVE_CLASSES.put("short", short.class);
        FuncFindClass.PRIMITIVE_CLASSES.put("int", int.class);
        FuncFindClass.PRIMITIVE_CLASSES.put("long", long.class);
        FuncFindClass.PRIMITIVE_CLASSES.put("float", float.class);
        FuncFindClass.PRIMITIVE_CLASSES.put("double", double.class);

        FuncFindClass.WRAPPED_CLASSES.put("void", Void.class);
        FuncFindClass.WRAPPED_CLASSES.put("boolean", Boolean.class);
        FuncFindClass.WRAPPED_CLASSES.put("byte", Byte.class);
        FuncFindClass.WRAPPED_CLASSES.put("char", Character.class);
        FuncFindClass.WRAPPED_CLASSES.put("short", Short.class);
        FuncFindClass.WRAPPED_CLASSES.put("int", Integer.class);
        FuncFindClass.WRAPPED_CLASSES.put("long", Long.class);
        FuncFindClass.WRAPPED_CLASSES.put("float", Float.class);
        FuncFindClass.WRAPPED_CLASSES.put("double", Double.class);
    }

    private ClassLoader classLoader;
    private boolean wrapPrimitive;

    private FuncFindClass() {
    }

    /**
     * @param classLoader   类加载器
     * @param wrapPrimitive 原生类型是否转换为对应的包裹类型
     */
    public FuncFindClass(ClassLoader classLoader, boolean wrapPrimitive) {
        this.classLoader = classLoader;
        this.wrapPrimitive = wrapPrimitive;
    }

    @Override
    public Class<?> apply(String className) {
        if (StringUtils.isNotBlank(className)) {
            if (!className.contains(Strings.DOT)) { // 不含句点的很可能为原生类型
                Class<?> clazz;
                if (this.wrapPrimitive) {
                    clazz = FuncFindClass.WRAPPED_CLASSES.get(className);
                } else {
                    clazz = FuncFindClass.PRIMITIVE_CLASSES.get(className);
                }
                if (clazz != null) {
                    return clazz;
                }
            }
            try {
                if (this.classLoader != null) {
                    return this.classLoader.loadClass(className);
                } else {
                    return Class.forName(className);
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return null;
    }
}
