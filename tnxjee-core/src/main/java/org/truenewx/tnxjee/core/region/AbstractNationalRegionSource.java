package org.truenewx.tnxjee.core.region;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;

/**
 * @author jianglei
 */
public abstract class AbstractNationalRegionSource
        implements NationalRegionSource, InitializingBean {

    /**
     * 国家代号
     */
    private String nation;
    protected String basename;
    /**
     * 显示区域-当前国家级行政区划的映射集
     */
    protected Map<Locale, Region> localeNationalRegionMap = new HashMap<>();
    /**
     * 显示区域-区划代号-行政区划的映射集
     */
    protected Map<Locale, Map<String, Region>> localeCodeSubsMap = new HashMap<>();
    /**
     * 显示区域-区划名称-行政区划的映射集
     */
    protected Map<Locale, Map<String, Region>> localeCaptionSubsMap = new HashMap<>();

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public void setNation(String nation) {
        Assert.isTrue(nation.length() == RegionSource.NATION_LENGTH,
                "The length of nation must be " + RegionSource.NATION_LENGTH);
        this.nation = nation.toUpperCase();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.basename == null) { // 如果未配置则按照默认规则确定资源文件基本名称
            setBasename("classpath:META-INF/region/" + this.nation);
        }
    }

    @Override
    public String getNation() {
        return this.nation;
    }

    @Override
    public Region getNationalRegion(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Region region = this.localeNationalRegionMap.get(locale);
        if (region == null) {
            region = buildNationalRegion(locale);
        }
        return region;
    }

    @Override
    public Region getSubRegion(String code, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Map<String, Region> codeSubsMap = this.localeCodeSubsMap.get(locale);
        if (codeSubsMap == null) {
            buildNationalRegion(locale);
            codeSubsMap = this.localeCodeSubsMap.get(locale);
        }
        if (codeSubsMap != null) {
            return codeSubsMap.get(code);
        }
        return null;
    }

    @Override
    public Region getSubRegion(String provinceCaption, String cityCaption, String countyCaption,
            Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Map<String, Region> captionSubsMap = this.localeCaptionSubsMap.get(locale);
        if (captionSubsMap == null) {
            buildNationalRegion(locale);
            captionSubsMap = this.localeCaptionSubsMap.get(locale);
        }
        if (captionSubsMap != null) {
            StringBuffer caption = new StringBuffer(provinceCaption);
            if (cityCaption != null) {
                caption.append(Strings.MINUS).append(cityCaption);
                if (countyCaption != null) { // 市级名称不为空，县级名称才有效
                    caption.append(Strings.MINUS).append(countyCaption);
                }
            }
            return captionSubsMap.get(caption.toString());
        }
        return null;
    }

    protected abstract Region buildNationalRegion(Locale locale);
}
