package org.truenewx.tnxjee.core.enums.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;

/**
 * 枚举字典
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumDict {

    private Locale locale;
    private Map<Binate<String, String>, EnumType> types = new HashMap<>();

    public EnumDict(Locale locale) {
        if (locale == null) {
            this.locale = Locale.getDefault();
        } else {
            this.locale = locale;
        }
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void addType(EnumType type) {
        if (type != null) {
            Binate<String, String> key = new Binary<>(type.getName(),
                    type.getSubname());
            this.types.put(key, type);
        }
    }

    public EnumType getType(String name) {
        return getType(name, null);
    }

    public EnumType getType(String name, String subname) {
        Binate<String, String> key = new Binary<>(name, subname);
        return this.types.get(key);
    }
}
