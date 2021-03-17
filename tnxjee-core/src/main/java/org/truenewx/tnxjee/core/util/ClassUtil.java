package org.truenewx.tnxjee.core.util;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 类的工具类<br/>
 * 本类中的方法只与Class有关，不与某个具体Object相关
 *
 * @author jianglei
 */
public class ClassUtil {

    private ClassUtil() {
    }

    /**
     * 查找指定类的指定属性字段上的指定类型注解
     *
     * @param clazz           类
     * @param propertyName    属性名，可以是父类中的属性
     * @param annotationClass 注解类型
     * @return 注解对象
     */
    public static <A extends Annotation> A findAnnotation(Class<?> clazz, String propertyName,
            Class<A> annotationClass) {
        Field field = findField(clazz, propertyName);
        if (field != null) {
            A annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 在指定类及其各级父类中查找表示指定属性的Field对象，如果无法找到则返回null
     *
     * @param clazz        类
     * @param propertyName 属性名
     * @return 表示指定属性的Field对象
     */
    public static Field findField(Class<?> clazz, String propertyName) {
        if (clazz != null && clazz != Object.class) { // Object类无法取到任何字段
            try {
                return clazz.getDeclaredField(propertyName);
            } catch (SecurityException e) {
                // 当前类找到但无权限访问，则返回null
                return null;
            } catch (NoSuchFieldException e) {
                // 当前类找不到，则到父类中找
                return findField(clazz.getSuperclass(), propertyName);
            }
        }
        return null;
    }

    public static Field findField(Class<?> clazz, Class<?> fieldType) {
        if (clazz != null && clazz != Object.class) { // Object类无法取到任何字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (fieldType.isAssignableFrom(field.getType())) {
                    return field;
                }
            }
            return findField(clazz.getSuperclass(), fieldType);
        }
        return null;
    }

    /**
     * 获取指定类的源于其父类的实际泛型类型集<br/>
     * 如果指定类没有父类，或者父类没有泛型，则返回长度为0的空数组
     *
     * @param clazz 指定了父类实际泛型的类
     * @return 实际泛型类型集
     */
    public static Class<?>[] getActualGenericTypes(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        return getActualGenericTypes(type);
    }

    /**
     * 获取指定类的源于其父类的第index个实际泛型类型
     *
     * @param clazz 指定了父类实际泛型的类
     * @param index 要取的泛型位置索引
     * @return 实际泛型类型
     */
    public static <T> Class<T> getActualGenericType(Class<?> clazz, int index) {
        Type superClass = clazz.getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            return getActualGenericType((ParameterizedType) superClass, index);
        }
        return null;
    }

    /**
     * 获取指定超类型的第index个实际泛型类型
     *
     * @param superType 超类型
     * @param index     要取的泛型位置索引
     * @return 实际泛型类型
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> getActualGenericType(ParameterizedType superType, int index) {
        if (superType != null) {
            Type[] types = superType.getActualTypeArguments();
            Type type = types[index];
            if (type instanceof ParameterizedType) {
                type = ((ParameterizedType) type).getRawType();
            }
            if (type instanceof Class) {
                return (Class<T>) type;
            }
        }
        return null;
    }

    /**
     * 获取指定类型的实际泛型类型集
     *
     * @param type 类型
     * @return 实际泛型类型集
     */
    private static Class<?>[] getActualGenericTypes(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Class<?>[] classes = new Class<?>[types.length];
            for (int i = 0; i < types.length; i++) {
                Type argType = types[i];
                if (argType instanceof ParameterizedType) {
                    argType = ((ParameterizedType) argType).getRawType();
                } else if (argType instanceof Class) {
                    classes[i] = (Class<?>) argType;
                } else {
                    classes[i] = null;
                }
            }
            return classes;
        }
        return new Class<?>[0];
    }

    /**
     * 获取指定类的源于指定接口的实际泛型类型集
     *
     * @param clazz          指定了接口实际泛型的类
     * @param interfaceClass 接口类型
     * @return 实际泛型类型集
     */
    public static Class<?>[] getActualGenericTypes(Class<?> clazz, Class<?> interfaceClass) {
        Collection<ParameterizedType> types = findParameterizedGenericInterfaces(clazz);
        Type type = getMatchedGenericType(types, interfaceClass);
        return getActualGenericTypes(type);
    }

    /**
     * 获取指定类的源于指定接口的第index个实际泛型类型
     *
     * @param clazz          指定了指定接口实际泛型的类
     * @param interfaceClass 接口类型
     * @param index          要取的泛型位置索引
     * @return 实际泛型类型
     */
    public static <T> Class<T> getActualGenericType(Class<?> clazz, Class<?> interfaceClass,
            int index) {
        Collection<ParameterizedType> types = findParameterizedGenericInterfaces(clazz);
        ParameterizedType superInterface = getMatchedGenericType(types, interfaceClass);
        return getActualGenericType(superInterface, index);
    }

    /**
     * 查出指定类的所有带泛型的接口类型清单
     *
     * @param clazz 类型
     * @return 带泛型的接口类型清单
     */
    private static Collection<ParameterizedType> findParameterizedGenericInterfaces(
            Class<?> clazz) {
        List<ParameterizedType> types = new ArrayList<>();
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                types.add((ParameterizedType) type);
            } else if (type instanceof Class) {
                types.addAll(findParameterizedGenericInterfaces((Class<?>) type));
            }
        }
        Type superclass = clazz.getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            types.add((ParameterizedType) superclass);
        } else if (superclass instanceof Class<?>) {
            types.addAll(findParameterizedGenericInterfaces((Class<?>) superclass));
        }
        return types;
    }

    /**
     * 获取指定类的指定注解的value()值
     *
     * @param clazz           类
     * @param annotationClass 注解类
     * @return 注解的value()值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getClassAnnotationValue(Class<?> clazz,
            Class<? extends Annotation> annotationClass) {
        Annotation annotation = AnnotationUtils.findAnnotation(clazz, annotationClass);
        return annotation == null ? null : (T) AnnotationUtils.getValue(annotation);
    }

    /**
     * 获取指定类的指定属性字段上指定类型的注解value()值
     *
     * @param clazz           类
     * @param propertyName    属性名
     * @param annotationClass 注解类型
     * @return 注解的value()值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldAnnotationValue(Class<?> clazz, String propertyName,
            Class<? extends Annotation> annotationClass) {
        Annotation annotation = findAnnotation(clazz, propertyName, annotationClass);
        return annotation == null ? null : (T) AnnotationUtils.getValue(annotation);
    }

    /**
     * 获取指定类型集中与指定接口类型泛型匹配的类型
     *
     * @param types          类型集
     * @param interfaceClass 接口类型
     * @return 指定类型集中与指定接口类型泛型匹配的类型
     */
    private static ParameterizedType getMatchedGenericType(Collection<ParameterizedType> types,
            Class<?> interfaceClass) {
        for (ParameterizedType type : types) {
            Type rawType = type.getRawType();
            if (rawType.equals(interfaceClass)) {
                return type;
            }
        }
        for (ParameterizedType type : types) {
            Type rawType = type.getRawType();
            if (rawType instanceof Class) {
                Class<?> rawClass = (Class<?>) rawType;
                if (interfaceClass.isAssignableFrom(rawClass)) {
                    Type[] actualTypeArguments = type.getActualTypeArguments();
                    // 构造满足条件的ParameterizedType
                    return new ParameterizedType() {
                        @Override
                        public Type[] getActualTypeArguments() {
                            // 从子接口中找出匹配父接口泛型的类型
                            TypeVariable<?>[] typeParameters = interfaceClass.getTypeParameters();
                            Type[] result = new Type[typeParameters.length];
                            for (int i = 0; i < typeParameters.length; i++) {
                                TypeVariable<?>[] tvs = rawClass.getTypeParameters();
                                for (int j = 0; j < tvs.length; j++) {
                                    TypeVariable<?> tv = tvs[j];
                                    if (tv.getName().equals(typeParameters[i].getName())) {
                                        result[i] = actualTypeArguments[j];
                                        break;
                                    }
                                }
                            }
                            return result;
                        }

                        @Override
                        public Type getRawType() {
                            return type.getRawType();
                        }

                        @Override
                        public Type getOwnerType() {
                            return type.getOwnerType();
                        }
                    };
                }
            }
        }
        return null;
    }

    /**
     * 获取指定类的指定公开静态属性值
     *
     * @param clazz        类
     * @param propertyName 属性名
     * @return 属性值
     */
    public static Object getPublicStaticPropertyValue(Class<?> clazz, String propertyName) {
        try {
            Field field = clazz.getDeclaredField(propertyName);
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                return field.get(null);
            }
        } catch (Exception e) {
            // 忽略异常，返回null
        }
        return null;
    }

    /**
     * 获取指定类的简单属性名。简单属性包括：原始类型，字符串，数字，日期，URI，URL，Locale
     *
     * @param clazz 类
     * @return 指定类的简单属性名
     */
    public static Set<String> getSimplePropertyNames(Class<?> clazz) {
        Set<String> names = new HashSet<>();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor pd : propertyDescriptors) {
            if (BeanUtils.isSimpleValueType(pd.getPropertyType())) {
                String name = pd.getName();
                if (!"class".equals(name)) {
                    names.add(name);
                }
            }
        }
        return names;
    }

    /**
     * 获取指定类的所有简单属性字段。简单属性包括：原始类型，字符串，数字，日期，URI，URL，Locale
     *
     * @param clazz 类
     * @return 指定类的所有简单属性字段
     */
    public static List<Field> getSimplePropertyField(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        // 在所有有效属性中识别简单类型字段
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor pd : propertyDescriptors) {
            Class<?> propertyType = pd.getPropertyType();
            if (propertyType != null && BeanUtils.isSimpleValueType(propertyType)) {
                String name = pd.getName();
                if (!"class".equals(name)) {
                    Field field = findField(clazz, name);
                    if (field != null) {
                        fields.add(field);
                    }
                }
            }
        }
        return fields;
    }

    /**
     * 查找指定类型中的属性元数据集
     *
     * @param clazz            类型
     * @param gettable         是否包含可读属性
     * @param settable         是否包含可写属性
     * @param parent           是否包含父类中的属性
     * @param includePredicate 属性包含判定
     * @return 属性元数据集
     */
    public static Collection<PropertyMeta> findPropertyMetas(Class<?> clazz, boolean gettable,
            boolean settable, boolean parent, BiPredicate<Class<?>, String> includePredicate) {
        Map<String, PropertyMeta> result = new LinkedHashMap<>();
        if (gettable || settable) {
            if (parent) { // 先加入父类的属性
                Class<?> superclass = clazz.getSuperclass();
                if (superclass != null && superclass != Object.class) {
                    Collection<PropertyMeta> propertyMetas = findPropertyMetas(superclass, gettable,
                            settable, true, includePredicate);
                    for (PropertyMeta propertyMeta : propertyMetas) {
                        result.put(propertyMeta.getName(), propertyMeta);
                    }
                }
            }
            if (clazz.isInterface()) { // 如果类型为接口，则根据getter/setter方法名称进行筛选
                for (Method method : clazz.getMethods()) {
                    String propertyName = getPropertyName(method, gettable, settable);
                    if (propertyName != null) {
                        Class<?> propertyType = null;
                        String methodName = method.getName();
                        if (methodName.startsWith("get")) { // getter方法，类型取方法结果类型
                            if (gettable) {
                                propertyType = method.getReturnType();
                            }
                        } else if (methodName.startsWith("set")) { // setter方法，类型取第一个参数的类型
                            if (settable) {
                                propertyType = method.getParameterTypes()[0];
                            }
                        }
                        // 可取得属性类型，且通过包含判断，才加入
                        if (propertyType != null && (includePredicate == null
                                || includePredicate.test(propertyType, propertyName))) {
                            addPropertyMeta(result, propertyName, propertyType,
                                    method.getAnnotations());
                        }
                    }
                }
            } else { // 如果类型为类则获取其属性描述进行筛选
                PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
                for (PropertyDescriptor pd : pds) {
                    String propertyName = pd.getName();
                    if (!"class".equals(propertyName) && (includePredicate == null
                            || includePredicate.test(pd.getPropertyType(), propertyName))) {
                        Method readMethod = pd.getReadMethod();
                        Method writeMethod = pd.getWriteMethod();
                        if ((gettable && readMethod != null) || (settable && writeMethod != null)) {
                            // 先添加声明字段上的注解
                            addPropertyMeta(result, propertyName, pd.getPropertyType(),
                                    getDeclaredFieldAnnotations(clazz, propertyName));
                            if (readMethod != null) { // 尝试添加读方法上的注解
                                addPropertyMeta(result, propertyName, pd.getPropertyType(),
                                        readMethod.getAnnotations());
                            }
                            if (writeMethod != null) { // 尝试添加写方法上的注解
                                addPropertyMeta(result, propertyName, pd.getPropertyType(),
                                        writeMethod.getAnnotations());
                            }
                        }
                    }
                }
            }
        }
        return result.values();
    }

    private static void addPropertyMeta(Map<String, PropertyMeta> result, String propertyName,
            Class<?> propertyType, Annotation[] annotations) {
        PropertyMeta propertyMeta = result.get(propertyName);
        if (propertyMeta == null) {
            propertyMeta = new PropertyMeta(propertyName, propertyType, annotations);
        } else {
            propertyMeta.addAnnotations(annotations);
        }
        result.put(propertyName, propertyMeta);
    }

    private static Annotation[] getDeclaredFieldAnnotations(Class<?> clazz, String propertyName) {
        try {
            Field field = clazz.getDeclaredField(propertyName);
            return field.getAnnotations();
        } catch (NoSuchFieldException | SecurityException e) {
        }
        return new Annotation[0];
    }

    /**
     * 获取指定方法可能访问的属性名，如果该方法不是getter/setter方法，则返回null
     *
     * @param method 方法
     * @param getter 是否考虑为getter方法的可能
     * @param setter 是否考虑为setter方法的可能
     * @return 属性名
     */
    private static String getPropertyName(Method method, boolean getter, boolean setter) {
        // 至少要考虑为getter/setter方法中的一种
        if (getter || setter) {
            String methodName = method.getName();
            if (methodName.length() > 3) { // 3为"get"或"set"的长度
                String propertyName = methodName.substring(3); // 可能的属性名
                // 截取出来的属性名首字母需为大写
                if (propertyName.length() > 0 && Character.isUpperCase(propertyName.charAt(0))) {
                    propertyName = StringUtil.firstToLowerCase(propertyName);
                    if (getter && isPropertyMethod(method, propertyName, true)) {
                        return propertyName;
                    }
                    if (setter && isPropertyMethod(method, propertyName, false)) {
                        return propertyName;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断指定方法是否指定属性的访问方法
     *
     * @param method       方法
     * @param propertyName 属性名
     * @param getter       是否getter方法，false-setter方法
     * @return 指定方法是否指定属性的访问方法
     */
    private static boolean isPropertyMethod(Method method, String propertyName, boolean getter) {
        // 必须是公开的非静态方法
        if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
            // 方法名称要匹配
            String methodName = (getter ? "get" : "set")
                    + StringUtil.firstToUpperCase(propertyName);
            if (methodName.equals(method.getName())) {
                if (getter) { // getter方法必须无参数且返回结果不为void
                    return method.getParameterTypes().length == 0
                            && method.getReturnType() != void.class;
                } else { // setter方法必须有一个参数且返回结果为void
                    return method.getParameterTypes().length == 1
                            && method.getReturnType() == void.class;
                }
            }
        }
        return false;
    }

    /**
     * 查找指定类型及其父类中指定属性的访问方法
     *
     * @param clazz        类型
     * @param propertyName 属性名
     * @param getter       是否getter方法，false-setter方法
     * @return 指定类型中指定属性的访问方法
     */
    public static Method findPropertyMethod(Class<?> clazz, String propertyName, boolean getter) {
        for (Method method : clazz.getMethods()) {
            if (isPropertyMethod(method, propertyName, getter)) {
                return method;
            }
        }
        // 此时没找到，则尝试从父类中查找
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            return findPropertyMethod(superclass, propertyName, getter);
        }
        return null;
    }

    /**
     * 在指定类型中查找所有符合Bean规范的属性描述集合
     *
     * @param clazz        类型
     * @param propertyType 期望的属性类型，为空时忽略属性类型限制
     * @return 符合Bean规范的属性描述集合
     */
    public static List<PropertyDescriptor> findPropertyDescriptors(Class<?> clazz, Class<?> propertyType) {
        List<PropertyDescriptor> list = new ArrayList<>();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor pd : pds) {
            Class<?> pt = pd.getPropertyType();
            // 带参数的get方法会导致属性类型为null的属性描述，应忽略
            if (pt != null && (propertyType == null || propertyType.isAssignableFrom(pt))) {
                list.add(pd);
            }
        }
        return list;
    }

    /**
     * 在指定类型中遍历指定属性类型的属性描述
     *
     * @param clazz        类型
     * @param propertyType 期望的属性类型，为空时忽略属性类型限制
     * @param predicate    遍历断言，返回false则终止遍历
     */
    public static void loopPropertyDescriptors(Class<?> clazz, Class<?> propertyType,
            Predicate<PropertyDescriptor> predicate) {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor pd : pds) {
            Class<?> pt = pd.getPropertyType();
            // 带参数的get方法会导致属性类型为null的属性描述，应忽略
            if (pt != null && (propertyType == null || propertyType.isAssignableFrom(pt))) {
                if (!predicate.test(pd)) {
                    return;
                }
            }
        }
    }

    /**
     * 判断指定类型集合中是否有至少一个类型，与指定类型相同或为其父类型
     *
     * @param classes 类型集合
     * @param clazz   类型
     * @return 指定类型集合中是否有至少一个类型，与指定类型相同或为其父类型
     */
    public static boolean oneIsAssignableFrom(Class<?>[] classes, Class<?> clazz) {
        for (Class<?> type : classes) {
            if (type == null && clazz == null) {
                return true;
            } else if (type != null && clazz != null && type.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定类型是否复合类型
     *
     * @param type 类型
     * @return 指定类型是否复合类型
     */
    public static boolean isComplex(Class<?> type) {
        return type != Object.class && !BeanUtils.isSimpleValueType(type) && !isAggregation(type);
    }

    /**
     * 判断指定类型是否集合类型。集合类型包括数组、Map、Iterable
     *
     * @param type 类型
     * @return 指定类型是否集合类型
     */
    public static boolean isAggregation(Class<?> type) {
        return type.isArray() || Map.class.isAssignableFrom(type) || Iterable.class.isAssignableFrom(type);
    }


    /**
     * 获取指定类型（包括父类）中指定方法名称和参数个数的公开方法清单
     *
     * @param type       类型
     * @param methodName 方法名称
     * @param argCount   参数个数，小于0时忽略个数，返回所有同名方法
     * @return 指定类型中指定方法名称和参数个数的公开方法清单
     */
    public static Collection<Method> findPublicMethods(Class<?> type, String methodName, int argCount) {
        Collection<Method> methods = new ArrayList<>();
        for (Method method : type.getMethods()) {
            if (method.getName().equals(methodName)
                    && (argCount < 0 || method.getParameterTypes().length == argCount)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static String getFullPropertyPath(Class<?> clazz, String propertyName) {
        StringBuffer path = new StringBuffer();
        if (clazz != null) {
            path.append(clazz.getName()).append(Strings.WELL);
        }
        path.append(propertyName);
        return path.toString();
    }

    public static String getSimplePropertyPath(Class<?> clazz, String propertyName) {
        StringBuffer path = new StringBuffer();
        if (clazz != null) {
            path.append(clazz.getSimpleName()).append(Strings.WELL);
        }
        path.append(propertyName);
        return path.toString();
    }

    public static Field getField(Enum<?> enumConstant) {
        Class<?> clazz = enumConstant.getDeclaringClass();
        try {
            return clazz.getDeclaredField(enumConstant.name());
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static <T> T newInstance(Class<T> clazz, Class<?>[] argTypes, Object[] args) {
        try {
            return clazz.getConstructor(argTypes).newInstance(args);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        return newInstance(clazz, argTypes, args);
    }

    /**
     * 遍历指定对象类型中具有指定注解的字段
     *
     * @param clazz           遍历对象类型
     * @param annotationClass 注解类型
     * @param predicate       遍历断言，返回false则终止遍历
     * @param <A>             注解类型
     */
    public static <A extends Annotation> void loopFieldsByAnnotation(Class<?> clazz, Class<A> annotationClass,
            BiPredicate<Field, A> predicate) {
        if (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                A annotation = field.getAnnotation(annotationClass);
                if (annotation != null) {
                    if (!predicate.test(field, annotation)) {
                        return;
                    }
                }
            }
            // 再遍历父类
            loopFieldsByAnnotation(clazz.getSuperclass(), annotationClass, predicate);
        }
    }

    /**
     * 遍历指定对象类型中指定字段类型的字段
     *
     * @param clazz     遍历对象类型
     * @param fieldType 字段类型
     * @param predicate 遍历断言，返回false则终止遍历
     */
    public static void loopFieldsByType(Class<?> clazz, Class<?> fieldType, Predicate<Field> predicate) {
        if (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (fieldType == null || fieldType.isAssignableFrom(field.getType())) {
                    if (!predicate.test(field)) {
                        return;
                    }
                }
            }
            // 再遍历父类
            loopFieldsByType(clazz.getSuperclass(), fieldType, predicate);
        }
    }

    /**
     * 判断指定类型是否包含可读的枚举属性
     *
     * @param clazz 类型
     * @return 指定类型是否包含可读的枚举属性
     */
    public static boolean hasReadableEnumProperty(Class<?> clazz) {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor pd : pds) {
            if (pd.getReadMethod() != null) {
                Class<?> propertyType = pd.getPropertyType();
                if (propertyType != null) {
                    if (propertyType.isEnum()) { // 属性类型为枚举的，则直接返回true
                        return true;
                    }
                    // 属性类型为复合类型，则检查该属性类型是否包含枚举属性
                    if (ClassUtil.isComplex(propertyType) && hasReadableEnumProperty(propertyType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否可序列化的非具化类型
     *
     * @param clazz 类型
     * @return 是否可序列化的非具化类型
     */
    public static boolean isSerializableNonConcrete(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        if ((modifiers & (Modifier.INTERFACE | Modifier.ABSTRACT)) != 0) {
            return Serializable.class.isAssignableFrom(clazz);
        }
        return false;
    }
}
