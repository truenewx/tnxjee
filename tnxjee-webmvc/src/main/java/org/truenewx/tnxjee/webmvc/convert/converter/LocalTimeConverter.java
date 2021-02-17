package org.truenewx.tnxjee.webmvc.convert.converter;

import java.time.LocalTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.TemporalUtil;

@Component
public class LocalTimeConverter implements Converter<String, LocalTime> {

    @Override
    public LocalTime convert(String source) {
        return TemporalUtil.parseTime(source);
    }

}
