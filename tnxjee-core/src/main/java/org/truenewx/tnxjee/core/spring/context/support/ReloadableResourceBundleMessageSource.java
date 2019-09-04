package org.truenewx.tnxjee.core.spring.context.support;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.context.MessagesSource;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 基于资源包属性集的消息来源
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ReloadableResourceBundleMessageSource
        extends org.springframework.context.support.ReloadableResourceBundleMessageSource
        implements MessagesSource {

    private static String PROPERTIES_SUFFIX = ".properties";
    private ResourcePatternResolver resourcePatternResolver;
    private Locale[] locales;

    public ReloadableResourceBundleMessageSource(Locale[] locales) {
        this.locales = locales;
    }

    public ReloadableResourceBundleMessageSource() {
        this(new Locale[0]);
    }

    @Autowired
    public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    public void setBasenames(String... basenames) {
        Set<String> set = new LinkedHashSet<>();
        for (String basename : basenames) {
            basename = basename.trim();
            if (basename.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
                try {
                    Resource[] resources = this.resourcePatternResolver
                            .getResources(basename + PROPERTIES_SUFFIX);
                    for (Resource resource : resources) {
                        String path = resource.getURI().toString();
                        path = path.substring(0, path.length() - PROPERTIES_SUFFIX.length());
                        // 去掉路径中可能的Locale后缀
                        for (Locale locale : this.locales) {
                            String suffix = Strings.UNDERLINE + locale.toString();
                            if (path.endsWith(suffix)) {
                                path = path.substring(0, path.length() - suffix.length());
                                break;
                            }
                        }
                        set.add(path);
                    }
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            } else {
                set.add(basename);
            }
        }
        super.setBasenames(set.toArray(new String[set.size()]));
    }

    @Override
    public Map<String, String> getMessages(Locale locale) {
        Map<String, String> messages = new TreeMap<>();
        Properties properties = getMergedProperties(locale).getProperties();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            messages.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return messages;
    }

    @Override
    public Map<String, String> getMessages(Locale locale, String prefix,
            boolean resultContainsPrefix) {
        Map<String, String> messages = new TreeMap<>();
        int prefixLength = prefix.length();
        Properties properties = getMergedProperties(locale).getProperties();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(prefix)) {
                if (!resultContainsPrefix) {
                    key = key.substring(prefixLength);
                }
                messages.put(key, entry.getValue().toString());
            }
        }
        return messages;
    }

}
