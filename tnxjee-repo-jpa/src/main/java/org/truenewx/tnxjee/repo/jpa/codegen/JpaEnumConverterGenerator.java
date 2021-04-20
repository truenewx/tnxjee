package org.truenewx.tnxjee.repo.jpa.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.parser.FreeMarkerTemplateParser;
import org.truenewx.tnxjee.core.parser.TemplateParser;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * JPA枚举转换器生成器
 *
 * @author jianglei
 */
public class JpaEnumConverterGenerator extends ModelBasedGenerator {

    private String repoBasePackage;
    private String templateLocation = "META-INF/template/enum-converter.ftl";
    private String arrayTemplateLocation = "META-INF/template/enum-array-converter.ftl";
    private TemplateParser templateParser = new FreeMarkerTemplateParser();

    public JpaEnumConverterGenerator(String modelBasePackage, String repoBasePackage) {
        super(modelBasePackage);
        this.repoBasePackage = repoBasePackage;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setArrayTemplateLocation(String arrayTemplateLocation) {
        this.arrayTemplateLocation = arrayTemplateLocation;
    }

    public void setTemplateParser(TemplateParser templateParser) {
        this.templateParser = templateParser;
    }

    public String generate(Class<?> enumClass) {
        boolean ifArray = enumClass.isArray();
        if (ifArray) {
            enumClass = enumClass.getComponentType();
        }
        if (enumClass.isEnum()) {
            try {
                String module = getModule(enumClass);
                String packageName = getPackageName(module);
                String enumClassSimpleName = enumClass.getSimpleName();
                String converterName = enumClassSimpleName + (ifArray ? "ArrayConverter" : "Converter");
                String converterClassName = packageName + Strings.DOT + converterName;
                try {
                    ClassUtils.forName(converterClassName, null);
                } catch (ClassNotFoundException e) {
                    String location = ifArray ? this.arrayTemplateLocation : this.templateLocation;
                    File template = new ClassPathResource(location).getFile();
                    Map<String, Object> params = new HashMap<>();
                    params.put("packageName", packageName);
                    params.put("enumClassName", enumClass.getName());
                    params.put("enumClassSimpleName", enumClassSimpleName);
                    params.put("converterName", converterName);
                    String content = this.templateParser.parse(template, params, Locale.getDefault());
                    File file = getJavaFile(packageName, converterName);
                    FileWriter writer = new FileWriter(file);
                    IOUtils.write(content, writer);
                    writer.close();
                }
                return converterClassName;
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return null;
    }

    private String getPackageName(String module) {
        String packageName = this.repoBasePackage;
        if (StringUtils.isNotBlank(module)) {
            packageName += Strings.DOT + module;
        }
        packageName += ".converter";
        return packageName;
    }

    private File getJavaFile(String packageName, String classSimpleName) throws IOException {
        File file = new ClassPathResource("/").getFile(); // classpath根
        file = file.getParentFile().getParentFile(); // 工程根
        String path = "src/main/java/" + packageName
                .replaceAll("[.]", Strings.SLASH) + Strings.SLASH + classSimpleName + ".java";
        file = new File(file, path);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

}
