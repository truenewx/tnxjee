package org.truenewx.tnxjee.model.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.parser.FreeMarkerTemplateParser;
import org.truenewx.tnxjee.core.parser.TemplateParser;

/**
 * 类生成器支持
 *
 * @author jianglei
 */
public abstract class ClassGeneratorSupport extends ModelBasedGeneratorSupport {

    private String targetBasePackage;
    private TemplateParser templateParser = new FreeMarkerTemplateParser();

    public ClassGeneratorSupport(String modelBasePackage, String targetBasePackage) {
        super(modelBasePackage);
        this.targetBasePackage = targetBasePackage;
    }

    public void setTemplateParser(TemplateParser templateParser) {
        this.templateParser = templateParser;
    }

    protected final String getTargetModulePackageName(String module) {
        String packageName = this.targetBasePackage;
        if (StringUtils.isNotBlank(module)) {
            packageName += Strings.DOT + module;
        }
        return packageName;
    }

    protected final void generate(String className, String templateLocation, Map<String, Object> params)
            throws IOException {
        if (templateLocation != null) {
            try {
                ClassUtils.forName(className, null);
            } catch (ClassNotFoundException e) {
                File template = new ClassPathResource(templateLocation).getFile();
                String content = this.templateParser.parse(template, params, Locale.getDefault());
                File file = getJavaFile(className);
                FileWriter writer = new FileWriter(file);
                IOUtils.write(content, writer);
                writer.close();
            }
        }
    }

    private File getJavaFile(String className) throws IOException {
        File file = new ClassPathResource("/").getFile(); // classpath根
        file = file.getParentFile().getParentFile(); // 工程根
        String path = "src/main/java/" + className.replaceAll("[.]", Strings.SLASH) + ".java";
        file = new File(file, path);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
