package org.truenewx.tnxjee.core.enums.support;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.core.caption.CaptionUtil;
import org.truenewx.tnxjee.core.spec.Name;
import org.truenewx.tnxjee.core.util.ClassUtil;

/**
 * 函数：从枚举类构建默认枚举类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncBuildDefaultEnumType implements BiFunction<Class<?>, Locale, EnumType> {
    /**
     * 单实例
     */
    public static FuncBuildDefaultEnumType INSTANCE = new FuncBuildDefaultEnumType();

    private FuncBuildDefaultEnumType() {
    }

    @Override
    public EnumType apply(Class<?> enumClass, Locale locale) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(enumClass.getName() + " is not an enum");
        }
        EnumType enumType = newEnumType(enumClass);
        for (Enum<?> enumConstant : (Enum<?>[]) enumClass.getEnumConstants()) {
            Field field = ClassUtil.getField(enumConstant);
            String caption = CaptionUtil.getCaption(field, locale);
            if (caption == null) { // 默认用枚举常量名称作为显示名
                caption = enumConstant.name();
            }
            enumType.addItem(new EnumItem(enumConstant.ordinal(), enumConstant.name(), caption));
        }
        return enumType;
    }

    public String getEnumTypeName(Class<?> enumClass) {
        String typeName = null;
        Name name = enumClass.getAnnotation(Name.class);
        if (name != null) {
            typeName = name.value();
        }
        if (StringUtils.isBlank(typeName)) {
            typeName = enumClass.getName();
        }
        return typeName;
    }

    /**
     * 创建枚举类型对象，不含枚举项目
     *
     * @param enumClass 枚举类
     * @return 不含枚举项目的枚举类型对象
     */
    private EnumType newEnumType(Class<?> enumClass) {
        String typeCaption = null;
        Caption captionAnno = enumClass.getAnnotation(Caption.class);
        if (captionAnno != null) {
            typeCaption = captionAnno.value();
        }
        // 默认使用枚举类型简称为显示名称
        if (StringUtils.isBlank(typeCaption)) {
            typeCaption = enumClass.getSimpleName();
        }
        String typeName = getEnumTypeName(enumClass);
        return new EnumType(typeName, typeCaption);
    }

}
