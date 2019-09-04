package org.truenewx.tnxjee.core.region;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 基于JSON文件的国家级区划来源实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JsonFileNationalRegionSource extends AbstractNationalRegionSource {

    private String encoding = Strings.ENCODING_UTF8;

    /**
     * @param encoding 从配置文件中读取内容时使用的字符集
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * 构建指定显示区域的当前国家行政区划
     *
     * @param locale 显示区域
     * @return 当前国家行政区划
     */
    @Override
    protected Region buildNationalRegion(Locale locale) {
        Resource resource = IOUtil.findI18nResource(this.basename, locale, "json");
        if (resource != null) {
            try {
                String json = IOUtils.toString(resource.getInputStream(), this.encoding);
                if (StringUtils.isNotBlank(json)) {
                    MutableRegion nationalRegion = JsonUtil.json2Bean(json, MutableRegion.class);
                    this.localeNationalRegionMap.put(locale, nationalRegion);
                    putLocaleSubsMap(locale, nationalRegion);
                    return nationalRegion;
                }
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return null;
    }

    private void putLocaleSubsMap(Locale locale, Region region) {
        Map<String, Region> codeSubMap = this.localeCodeSubsMap.get(locale);
        if (codeSubMap == null) {
            codeSubMap = new HashMap<>();
            this.localeCodeSubsMap.put(locale, codeSubMap);
        }

        Map<String, Region> captionSubMap = this.localeCaptionSubsMap.get(locale);
        if (captionSubMap == null) {
            captionSubMap = new HashMap<>();
            this.localeCaptionSubsMap.put(locale, captionSubMap);
        }

        Collection<Region> subs = region.getSubs();
        if (subs != null) {
            for (Region sub : subs) {
                codeSubMap.put(sub.getCode(), sub);
                captionSubMap.put(sub.getCaption(), sub);
                putLocaleSubsMap(locale, sub);
            }
        }
    }
}
