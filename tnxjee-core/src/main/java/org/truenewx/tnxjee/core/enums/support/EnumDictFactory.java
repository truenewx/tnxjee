package org.truenewx.tnxjee.core.enums.support;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.BooleanEnum;
import org.truenewx.tnxjee.core.spring.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.IOUtil;

/**
 * 枚举字典工厂（解析器实现）
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service("enumDictResolver")
public class EnumDictFactory implements EnumDictResolver, ContextInitializedBean {

    /**
     * 枚举配置文件的基本名称
     */
    private static final String CONFIG_FILE_BASE_NAME = "enums";
    /**
     * 枚举配置文件的扩展名
     */
    private static final String CONFIG_FILE_EXTENSION = "xml";

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<Locale, EnumDict> dicts = new Hashtable<>();
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Override
    public EnumDict getEnumDict(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        EnumDict dict = this.dicts.get(locale);
        if (dict == null) { // 没有相应区域的字典就构建一个
            dict = new EnumDict(locale);
            this.dicts.put(locale, dict);
        }
        return dict;
    }

    @Override
    public EnumType getEnumType(String type, Locale locale) {
        return getEnumType(type, null, locale);
    }

    @Override
    public EnumType getEnumType(String type, String subtype, Locale locale) {
        EnumDict dict = getEnumDict(locale);
        EnumType result = dict.getType(type, subtype);
        if (result == null) { // 枚举字典里没有该枚举类型，则尝试构建枚举类型
            result = buildEnumType(type, subtype, locale);
            dict.addType(result);
        }
        return result;
    }

    private EnumItem getEnumItem(String type, String subtype, String key, Locale locale,
            String... keys) {
        EnumType enumType = getEnumType(type, subtype, locale);
        if (enumType != null) { // 尝试构建不成功时item可能为null
            return enumType.getItem(key, keys);
        }
        return null;
    }

    @Override
    public EnumItem getEnumItem(Enum<?> enumConstant, Locale locale) {
        Class<?> enumClass = enumConstant.getClass();
        String typeName = FuncBuildDefaultEnumType.INSTANCE.getEnumTypeName(enumClass);
        return getEnumItem(typeName, null, enumConstant.name(), locale);
    }

    @Override
    public String getText(String type, String subtype, String key, Locale locale, String... keys) {
        EnumItem item = getEnumItem(type, subtype, key, locale, keys);
        return item == null ? null : item.getCaption();
    }

    @Override
    public String getText(String type, String key, Locale locale, String... keys) {
        return getText(type, null, key, locale, keys);
    }

    @Override
    public String getText(Enum<?> enumConstant, Locale locale) {
        Class<?> enumClass = enumConstant.getClass();
        String typeName = FuncBuildDefaultEnumType.INSTANCE.getEnumTypeName(enumClass);
        return getText(typeName, enumConstant.name(), locale);
    }

    /**
     * 构建指定枚举类型，包括其所有子类型
     *
     * @param type    枚举类型名称
     * @param subtype 子枚举类型名称
     * @param locale  地区
     * @return 枚举类型
     */
    @SuppressWarnings("unchecked")
    private EnumType buildEnumType(String type, String subtype, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        try {
            Class<?> clazz;
            if (BOOLEAN_ENUM_TYPE.equalsIgnoreCase(type)) {
                clazz = BooleanEnum.class;
            } else {
                clazz = this.resourcePatternResolver.getClassLoader().loadClass(type);
            }
            if (clazz.isEnum()) { // 忽略非枚举
                Class<Enum<?>> enumClass = (Class<Enum<?>>) clazz;
                SAXReader reader = new SAXReader();
                // 依次尝试从各级国际化配置文件中取枚举类型
                EnumType enumType = readEnumType(reader, enumClass, subtype, locale);
                // 配置文件中没有，且没有指定子类型，且为默认区域，则从枚举类中构建默认枚举类型
                if (enumType == null && StringUtils.isBlank(subtype)) {
                    enumType = FuncBuildDefaultEnumType.INSTANCE.apply(enumClass, locale);
                }
                return enumType;
            } else {
                this.logger.warn("{} is not an enum class, so didn't build from it", type);
            }
        } catch (ClassNotFoundException e) {
            // type如果不是一个有效的类名，则尝试从枚举配置文件中构建
            SAXReader reader = new SAXReader();
            String basename = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "META-INF/"
                    + EnumDictFactory.CONFIG_FILE_BASE_NAME;
            Resource resource = IOUtil.findI18nResource(basename, locale, CONFIG_FILE_EXTENSION);
            return readEnumType(reader, resource, type, subtype);
        }
        return null;
    }

    /**
     * 从指定枚举类对应的区域配置文件中读取内容并构建枚举类型
     *
     * @param reader    XML读取器
     * @param enumClass 枚举类
     * @param subtype   枚举子类型名
     * @param locale    区域
     * @return 枚举类型
     */
    private EnumType readEnumType(SAXReader reader, Class<Enum<?>> enumClass, String subtype,
            Locale locale) {
        String basename = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils
                .addResourcePathToPackagePath(enumClass, EnumDictFactory.CONFIG_FILE_BASE_NAME);
        Resource resource = IOUtil.findI18nResource(basename, locale, CONFIG_FILE_EXTENSION);
        EnumType result = readEnumType(reader, resource, enumClass.getSimpleName(), subtype);
        if (result == null) {// 与枚举类相关的配置文件不存在，或其中找不到匹配的枚举类型，则尝试从全局配置文件中读取
            basename = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "META-INF/"
                    + EnumDictFactory.CONFIG_FILE_BASE_NAME;
            resource = IOUtil.findI18nResource(basename, locale, CONFIG_FILE_EXTENSION);
            result = readEnumType(reader, resource, enumClass.getName(), subtype);
        }
        if (result != null) {
            result.setName(enumClass.getName()); // 确保枚举类型的名称为枚举类全名
        }
        return result;
    }

    /**
     * 从指定文件中读取内容并构建枚举类型<br/>
     * 如果指定配置文件不存在，或文件中没有指定的枚举类型，则返回null
     *
     * @param reader   XML读取器
     * @param resource 配置文件
     * @param type     枚举类型名
     * @param subtype  枚举子类型名
     * @return 枚举类型
     */
    private EnumType readEnumType(SAXReader reader, Resource resource, String type,
            String subtype) {
        if (resource != null) {
            try {
                Document doc = reader.read(resource.getInputStream());
                List<Element> typeElements = doc.getRootElement().elements("type");
                for (Element typeElement : typeElements) {
                    String typeName = typeElement.attributeValue("name");
                    String typeSubname = typeElement.attributeValue("subname");
                    if (type.equals(typeName) && StringUtils.equals(subtype, typeSubname)) {
                        String typeCaption = typeElement.attributeValue("caption");
                        EnumType enumType = new EnumType(typeName, typeSubname, typeCaption);
                        addEnumItemsToEnumType(typeElement, enumType);
                        return enumType;
                    }
                }
            } catch (DocumentException | IOException e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private void addEnumItemsToEnumType(Element typeElement, EnumType enumType) {
        List<Element> itemElements = typeElement.elements("item");
        for (int i = 0; i < itemElements.size(); i++) {
            Element itemElement = itemElements.get(i);
            EnumItem item = buildEnumItem(itemElement, i);
            enumType.addItem(item);
        }
    }

    private EnumItem buildEnumItem(Element itemElement, int ordinal) {
        String key = itemElement.attributeValue("key");
        String caption = itemElement.attributeValue("caption");
        EnumItem item = new EnumItem(ordinal, key, caption);
        addChildrenToEnumItem(itemElement, item);
        return item;
    }

    /**
     * 解析指定枚举项元素中的子枚举项加入指定枚举项中
     *
     * @param itemElement 枚举项元素
     * @param enumItem    枚举项
     */
    private void addChildrenToEnumItem(Element itemElement, EnumItem enumItem) {
        List<Element> childElements = itemElement.elements("item");
        for (int i = 0; i < childElements.size(); i++) {
            Element childElement = childElements.get(i);
            EnumItem child = buildEnumItem(childElement, i);
            enumItem.addChild(child);
        }
    }

    /**
     * 设置配置文件路径样式
     *
     * @param locationPattern 配置文件路径样式，可用逗号分隔多个
     * @throws IOException 如果配置文件读取出错
     */
    public void setLocationPattern(String locationPattern) throws IOException {
        String[] locations = locationPattern.split(Strings.COMMA);
        SAXReader reader = new SAXReader();
        for (String location : locations) {
            Resource[] resources = this.resourcePatternResolver.getResources(location);
            for (Resource resource : resources) {
                if (resource.exists()) {
                    try {
                        Document doc = reader.read(resource.getInputStream());
                        List<Element> typeElements = doc.getRootElement().elements("type");
                        String resourceName = FilenameUtils.getBaseName(resource.getFilename());
                        Locale locale = getLocale(resourceName);
                        EnumDict dict = getEnumDict(locale);
                        for (Element typeElement : typeElements) {
                            String typeName = typeElement.attributeValue("name");
                            String typeSubname = typeElement.attributeValue("subname");
                            String typeCaption = typeElement.attributeValue("caption");
                            EnumType enumType = new EnumType(typeName, typeSubname, typeCaption);
                            addEnumItemsToEnumType(typeElement, enumType);
                            dict.addType(enumType);
                        }
                    } catch (DocumentException | IOException e) {
                        this.logger.error(e.getMessage(), e);
                    } // 单个配置文件异常不影响对其它配置文件的读取
                }
            }
        }
    }

    /**
     * 从资源信息中确定区域
     *
     * @param resource 资源
     * @return 区域
     */
    private Locale getLocale(String resourceName) {
        // 根据资源名称确定区域，要求资源名称以类似_zh_CN样式结尾，且有其它开头
        String[] names = resourceName.split(Strings.UNDERLINE);
        int length = names.length;
        if (length > 2) { // 含有至少两个下划线，含有语言和国别
            return new Locale(names[length - 2], names[length - 1]);
        } else if (length == 2) { // 含一个下划线，含有语言
            return new Locale(names[1]);
        } else { // 不含下划线，则返回null
            return null;
        }
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        // 从Spring容器中找出所有EnumType，加入对应区域的枚举字典中
        Map<String, EnumType> enumTypeMap = context.getBeansOfType(EnumType.class);
        for (Entry<String, EnumType> entry : enumTypeMap.entrySet()) {
            Locale locale = getLocale(entry.getKey());
            EnumDict dict = getEnumDict(locale);
            dict.addType(entry.getValue());
        }
    }
}
