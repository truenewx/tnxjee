package org.truenewx.tnxjee.webmvc.convert.converter;

import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.TemporalUtil;

@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String source) {
        return TemporalUtil.parseDateTime(source);
    }

}
