package org.truenewx.tnxjee.service.impl.spec.region;

import java.util.*;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.spec.region.Region;

/**
 * 源于中国国家统计局数据的行政区划映射集解析器
 *
 * @author jianglei
 */
@Component
public class StatsGovCnRegionMapParser implements RegionMapParser {

    @Override
    public Iterable<Region> parseAll(Map<String, String> codeCaptionMap) {
        // 取得排好序的代号集
        Set<String> codes = new TreeSet<>(codeCaptionMap.keySet());
        // 遍历排序后的代号集构建结果
        Collection<Region> result = new ArrayList<>();
        Map<String, Region> codeRegionMapping = new HashMap<>();
        for (String code : codes) {
            if (code.length() == 8) { // 代码一定是8位的
                String value = codeCaptionMap.get(code);
                String caption;
                String group;
                int index = value.indexOf(Strings.LEFT_BRACKET);
                if (index >= 0) { // 如果有括号，则括号中的值为分组
                    caption = value.substring(0, index);
                    group = value.substring(index + 1, value.length() - Strings.RIGHT_BRACKET.length());
                } else { // 没有括号，则没有分组，值即为显示名
                    caption = value;
                    group = null;
                }

                Region region = buildRegion(codeRegionMapping, group, code, caption);
                result.add(region);
                codeRegionMapping.put(region.getCode(), region);
            }
        }
        return result;
    }

    private Region buildRegion(Map<String, Region> codeRegionMapping, String group, String code, String caption) {
        Region region;
        if (code.endsWith("0000")) { // 0000结尾的为省级区划，省级区划不需要考虑父级区划，且所有省级区划均为有效
            region = new Region(code, caption, group);
        } else { // 其它为市级或县级区划，需考虑父级区划
            region = new Region(code, caption, group);
            Region parentRegion = getParentRegion(codeRegionMapping, code);
            if (parentRegion != null) {
                parentRegion.addSub(region);
            }
        }
        return region;
    }

    /**
     * 从指定代号-区划映射集中找出指定代号的父级区划
     *
     * @param codeRegionMap 代号-区划映射集
     * @param code          代号
     * @return 父级区划
     */
    private Region getParentRegion(Map<String, Region> codeRegionMap, String code) {
        if (code.length() <= 2) { // 代号长度小于等于2，则不可能有父级
            return null;
        }
        String parentCode = getParentCode(code); // 父级代号
        Region parentRegion = codeRegionMap.get(parentCode);
        if (parentRegion != null) {
            return parentRegion;
        }
        // 如果直接父级区划找不到，则再向上一级查找父级区划
        return getParentRegion(codeRegionMap, parentCode);
    }

    /**
     * 获取指定代号的父级代号
     *
     * @param code 代号
     * @return 父级代号
     */
    private String getParentCode(String code) {
        int length = code.length(); // 应有的长度
        // 取非00结尾的前缀部分
        while (code.endsWith("00")) {
            code = code.substring(0, code.length() - 2);
        }
        // 再往前取一级
        code = code.substring(0, code.length() - 2);
        // 如果此时代号小于等于两位，则已经到达国家代号，返回国家代号作为父级代号
        if (code.length() <= 2) {
            return code;
        }
        // 后面补00直到长度为应有长度
        while (code.length() < length) {
            code += "00";
        }
        return code;
    }
}
