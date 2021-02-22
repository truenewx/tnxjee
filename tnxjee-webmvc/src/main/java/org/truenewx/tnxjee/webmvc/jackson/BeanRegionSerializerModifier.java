package org.truenewx.tnxjee.webmvc.jackson;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.spec.region.Region;
import org.truenewx.tnxjee.service.spec.region.RegionCode;
import org.truenewx.tnxjee.service.spec.region.RegionSource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * Bean行政区划序列化器修改器
 */
public class BeanRegionSerializerModifier extends BeanSerializerModifier {

    private RegionSource regionSource;

    public BeanRegionSerializerModifier(RegionSource regionSource) {
        this.regionSource = regionSource;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
            List<BeanPropertyWriter> beanProperties) {
        if (this.regionSource != null) {
            for (int i = 0; i < beanProperties.size(); i++) {
                BeanPropertyWriter writer = beanProperties.get(i);
                RegionCode regionCodeAnnotation = writer.getAnnotation(RegionCode.class);
                if (writer.getType().getRawClass() == String.class && regionCodeAnnotation != null) {
                    beanProperties.set(i, new BeanPropertyWriter(writer) {

                        private static final long serialVersionUID = 1410150784339432332L;

                        @Override
                        public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
                                throws Exception {
                            super.serializeAsField(bean, gen, prov);
                            String propertyName = getName();
                            AnnotatedMember member = getMember();
                            String regionCode = (String) member.getValue(bean);
                            if (StringUtils.isNotBlank(regionCode)) {
                                Region region = BeanRegionSerializerModifier.this.regionSource
                                        .getRegion(regionCode, prov.getLocale());
                                if (region != null) {
                                    String caption = region.getCaption();
                                    if (caption != null) {
                                        StringBuilder sb = new StringBuilder(caption);
                                        Region parentRegion = region.getParent();
                                        while (parentRegion != null && parentRegion.getLevel() >= regionCodeAnnotation
                                                .captionLevel()) {
                                            String parentCaption = parentRegion.getCaption();
                                            if (parentCaption != null) {
                                                sb.insert(0, parentCaption);
                                            }
                                            parentRegion = parentRegion.getParent();
                                        }
                                        caption = sb.toString();
                                        gen.writeStringField(getCaptionPropertyName(propertyName), caption);
                                    }
                                }
                            }
                        }

                    });
                }
            }
        }
        return beanProperties;
    }

    protected String getCaptionPropertyName(String propertyName) {
        return propertyName + Strings.UNDERLINE + "caption";
    }

}
