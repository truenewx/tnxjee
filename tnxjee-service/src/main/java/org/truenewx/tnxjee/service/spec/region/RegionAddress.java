package org.truenewx.tnxjee.service.spec.region;

import org.truenewx.tnxjee.model.validation.constraint.RegionCode;

/**
 * 带行政区划代码的地址
 *
 * @author jianglei
 */
public class RegionAddress {

    @RegionCode
    private String regionCode;
    private String detail;

    public RegionAddress(String regionCode, String detail) {
        this.regionCode = regionCode;
        this.detail = detail;
    }

    public String getRegionCode() {
        return this.regionCode;
    }

    public String getDetail() {
        return this.detail;
    }

}
