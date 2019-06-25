package org.truenewx.tnxjee.core.serializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.LongCodec;

/**
 * JSON日期的序列化和反序列化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JsonDateCodec extends LongCodec {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType,
            int features) throws IOException {
        if (object == null) {
            serializer.getWriter().writeNull();
            return;
        }

        Date date = (Date) object;
        serializer.write(date.getTime());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Long value = super.deserialze(parser, type, fieldName);
        return (T) new Date(value);
    }
}
