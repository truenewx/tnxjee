package org.truenewx.tnxjee.service.spec.region;

import java.util.Locale;

/**
 * 具体国家的行政区划来源
 *
 * @author jianglei
 * 
 */
public interface NationalRegionSource {
    /**
     * 获取国家代号
     *
     * @return 国家代号
     */
    String getNation();

    /**
     * 获取当前国家的行政区划，其显示名为指定显示区域下的文本
     *
     * @param locale 显示区域
     * @return 当前国家的行政区划
     */
    Region getNationalRegion(Locale locale);

    /**
     * 获取指定行政区划代号对应的行政区划
     *
     * @param code   行政区划代号
     * @param locale 显示区域
     * @return 行政区划
     */

    Region getSubRegion(String code, Locale locale);

    /**
     * 获取指定行政区划名称对应的行政区划
     *
     * @param provinceCaption 省份名称
     * @param cityCaption     市名称
     * @param countyCaption   县名称
     * @param locale          显示区域
     * @return 行政区划选项
     */

    Region getSubRegion(String provinceCaption, String cityCaption, String countyCaption,
            Locale locale);
}
