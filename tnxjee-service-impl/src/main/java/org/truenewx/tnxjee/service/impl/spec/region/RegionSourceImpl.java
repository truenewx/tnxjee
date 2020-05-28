package org.truenewx.tnxjee.service.impl.spec.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.service.spec.region.NationalRegionSource;
import org.truenewx.tnxjee.service.spec.region.Region;
import org.truenewx.tnxjee.service.spec.region.RegionSource;

/**
 * 行政区划来源实现
 *
 * @author jianglei
 * 
 */
public class RegionSourceImpl implements RegionSource, ContextInitializedBean {
    /**
     * 国家级行政区划解析器映射集
     */
    private Map<String, NationalRegionSource> nationalSources = new LinkedHashMap<>();

    public void setNationalSources(Iterable<NationalRegionSource> nationalRegionSources) {
        for (NationalRegionSource nationalRegionSource : nationalRegionSources) {
            String nation = nationalRegionSource.getNation();
            if (nation != null && nation.length() == RegionSource.NATION_LENGTH) { // 国家代号必须为固定长度
                this.nationalSources.put(nation.toUpperCase(), nationalRegionSource);
            }
        }
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, NationalRegionSource> nrss = context.getBeansOfType(NationalRegionSource.class);
        setNationalSources(nrss.values()); // 如果有重复，则覆盖原有配置
    }

    /**
     * 从区划代号中获取国家代号，如果区划代号不合法，则返回null
     *
     * @param region 区划代号
     * @return 国家代号
     */
    private String getNation(String region) {
        if (region.length() >= RegionSource.NATION_LENGTH) {
            return region.substring(0, RegionSource.NATION_LENGTH).toUpperCase();
        }
        return null;
    }

    @Override
    public Region getRegion(String regionCode, Locale locale) {
        String nation = getNation(regionCode);
        if (nation != null) {
            NationalRegionSource nationalOptionSource = this.nationalSources.get(nation);
            if (nationalOptionSource != null) {
                if (nation.equals(regionCode)) { // 指定区划即为国家，直接取国家区划选项
                    return nationalOptionSource.getNationalRegion(locale);
                } else { // 否则从子孙区划中查找
                    return nationalOptionSource.getSubRegion(regionCode, locale);
                }
            }
        }
        return null;
    }

    @Override
    public Region getRegion(String nation, String provinceCaption, String cityCaption,
            String countyCaption, Locale locale) {
        NationalRegionSource nationalOptionSource = this.nationalSources.get(nation);
        if (nationalOptionSource != null) {
            if (provinceCaption == null) { // 如果未指定省份名称，则直接取国家区划选项
                return nationalOptionSource.getNationalRegion(locale);
            } else { // 否则从子孙区划中查找
                return nationalOptionSource.getSubRegion(provinceCaption, cityCaption,
                        countyCaption, locale);
            }
        }
        return null;
    }

    @Override
    public Collection<Region> getNationalRegions(Locale locale) {
        Collection<Region> result = new ArrayList<>();
        for (NationalRegionSource nationalResolver : this.nationalSources.values()) {
            result.add(nationalResolver.getNationalRegion(locale));
        }
        return result;
    }

    @Override
    public Region getNationalRegion(String nation, Locale locale) {
        NationalRegionSource source = this.nationalSources.get(nation);
        if (source != null) {
            return source.getNationalRegion(locale);
        }
        return null;
    }
}
