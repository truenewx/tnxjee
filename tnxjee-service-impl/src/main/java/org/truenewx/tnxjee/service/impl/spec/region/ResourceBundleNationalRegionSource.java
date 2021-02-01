package org.truenewx.tnxjee.service.impl.spec.region;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.message.MessagesSource;
import org.truenewx.tnxjee.service.spec.region.Region;

/**
 * 基于资源绑定属性文件的国家级区划来源实现
 *
 * @author jianglei
 */
@Component
public class ResourceBundleNationalRegionSource extends AbstractNationalRegionSource {
    /**
     * 消息集来源
     */
    @Autowired(required = false)
    private MessagesSource messagesSource;
    /**
     * 区域选项映射集解析器
     */
    @Autowired
    private RegionMapParser parser;

    /**
     * 构建指定显示区域的当前国家行政区划
     *
     * @param locale 显示区域
     * @return 当前国家行政区划
     */
    @Override
    protected Region buildNationalRegion(Locale locale) {
        Map<String, String> messages = this.messagesSource.getMessages(locale);
        String nation = getNation();
        String nationCaption = messages.get(nation);
        if (nationCaption != null) { // 取得到国家显示名才构建国家级区域选项
            Region nationalRegion = new Region(nation, nationCaption);
            if (this.parser != null) {
                Iterable<Region> subs = this.parser.parseAll(messages);

                Map<String, Region> codeSubsMap = new HashMap<>();
                Map<String, Region> captionSubsMap = new HashMap<>();
                for (Region sub : subs) {
                    codeSubsMap.put(sub.getCode(), sub);
                    StringBuilder caption = new StringBuilder(sub.getCaption());
                    Region parent = sub.getParent();
                    if (parent == null) { // 所有子选项中未指定父选项的才作为下一级子选项加入国家级选项中
                        nationalRegion.addSub(sub);
                    }
                    while (parent != null && !parent.getCode().equals(nation)) { // 不加国别名称
                        caption.insert(0, Strings.MINUS).insert(0, parent.getCaption());
                        parent = parent.getParent();
                    }
                    captionSubsMap.put(caption.toString(), sub);
                }
                this.localeCodeSubsMap.put(locale, codeSubsMap);
                this.localeCaptionSubsMap.put(locale, captionSubsMap);
            }
            this.localeNationalRegionMap.put(locale, nationalRegion);
            return nationalRegion;
        }
        return null;
    }
}
