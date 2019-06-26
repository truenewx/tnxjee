package org.truenewx.tnxjee.core.util.algorithm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;

/**
 * 算法：获取指定类型的默认值
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoDefaultValue implements Algorithm {

    private static final Map<Class<?>, Object> DEFAULTS = new HashMap<>();

    static {
        DEFAULTS.put(boolean.class, false);
        DEFAULTS.put(char.class, '\0');
        DEFAULTS.put(byte.class, (byte) 0);
        DEFAULTS.put(short.class, (short) 0);
        DEFAULTS.put(int.class, 0);
        DEFAULTS.put(long.class, 0L);
        DEFAULTS.put(float.class, 0f);
        DEFAULTS.put(double.class, 0d);
        DEFAULTS.put(String.class, Strings.EMPTY);
        DEFAULTS.put(Byte.class, Byte.valueOf((byte) 0));
        DEFAULTS.put(Character.class, Character.valueOf((char) 0));
        DEFAULTS.put(Short.class, Short.valueOf((short) 0));
        DEFAULTS.put(Integer.class, Integer.valueOf(0));
        DEFAULTS.put(Long.class, Long.valueOf(0l));
        DEFAULTS.put(Float.class, Float.valueOf(0.0f));
        DEFAULTS.put(Double.class, Double.valueOf(0.0d));
        DEFAULTS.put(Boolean.class, Boolean.FALSE);
        DEFAULTS.put(BigDecimal.class, BigDecimal.ZERO);
        DEFAULTS.put(BigInteger.class, BigInteger.ZERO);
        DEFAULTS.put(Currency.class, Currency.getInstance(Locale.getDefault()));
    }

    private AlgoDefaultValue() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T visit(Class<T> clazz) {
        if (clazz.isEnum()) {
            return clazz.getEnumConstants()[0];
        }
        if (Date.class.isAssignableFrom(clazz)) {
            return (T) new Date();
        }
        if (Temporal.class.isAssignableFrom(clazz)) {
            try {
                return (T) clazz.getMethod("now").invoke(null);
            } catch (ReflectiveOperationException e) {
                return null;
            }
        }
        T result = (T) DEFAULTS.get(clazz);
        if (result == null) {
            try {
                result = clazz.getConstructor().newInstance();
            } catch (Exception e) {
            }
        }
        return result;
    }

}
