package org.truenewx.tnxjee.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.function.FuncMaxNumber;
import org.truenewx.tnxjee.core.util.function.FuncMinNumber;

/**
 * 数学工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MathUtil {
    /**
     * 数值100
     */
    public static final BigDecimal HUNDRED = new BigDecimal(100);

    private MathUtil() {
    }

    /**
     * 获取随机双精度浮点数
     *
     * @param min 最小值
     * @param max 最大值（不包含）
     * @return 随机双精度浮点数
     */
    public static double randomDouble(double min, double max) {
        double d = Math.random();
        return min + (max - min) * d;
    }

    /**
     * 获取随机字节数
     *
     * @param min 最小值
     * @param max 最大值（不包含）
     * @return 随机字节数
     */
    public static byte randomByte(byte min, byte max) {
        return (byte) randomDouble(min, max);
    }

    /**
     * 获取随机整数
     *
     * @param min 最小值
     * @param max 最大值（不包含）
     * @return 随机整数
     */
    public static int randomInt(int min, int max) {
        return (int) randomDouble(min, max);
    }

    /**
     * 获取随机长整数
     *
     * @param min 最小值
     * @param max 最大值（不包含）
     * @return 随机长整数
     */
    public static long randomLong(long min, long max) {
        return (long) randomDouble(min, max);
    }

    /**
     * 以指定中奖几率抽奖，返回是否中奖
     *
     * @param probability 中奖几率
     * @return true if 中奖, otherwise false
     */
    public static boolean drawLottery(double probability) {
        return randomDouble(0, 1) < probability;
    }

    /**
     * 解析转换指定字符串为十进制数字，如果字符串不是合法的十进制数字格式，则返回null
     *
     * @param s 字符串
     * @return 转换后的十进制数字
     */
    public static BigDecimal parseDecimal(String s) {
        return parseDecimal(s, null);
    }

    /**
     * 解析转换指定字符串为十进制数字，如果字符串不是十进制数字格式，则返回指定默认值
     *
     * @param s            字符串
     * @param defaultValue 默认值
     * @return 转换后的十进制数字
     */
    public static BigDecimal parseDecimal(String s, BigDecimal defaultValue) {
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 解析转换指定字符串为长整型数字，如果字符串不是长整型数字格式，则返回0
     *
     * @param s 字符串
     * @return 转换后的长整型数字
     */
    public static long parseLong(String s) {
        return parseLong(s, 0);
    }

    /**
     * 解析转换指定字符串为长整型数字，如果字符串不是整型数字格式，则返回指定默认值
     *
     * @param s            字符串
     * @param defaultValue 默认值
     * @return 转换后的长整型数字
     */
    public static long parseLong(String s, long defaultValue) {
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Long parseLongObject(String s, Long defaultValue) {
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 以指定分隔符拆分指定字符串，并解析为长整数数组
     *
     * @param s         字符串
     * @param separator 分隔符
     * @return 长整数数组
     */
    public static long[] parseLongArray(String s, String separator) {
        if (StringUtils.isNotBlank(s)) {
            String[] array = s.split(separator);
            long[] result = new long[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = parseLong(array[i]);
            }
            return result;
        }
        return new long[0];
    }

    /**
     * 以指定分隔符拆分指定字符串，并解析为长整数对象数组
     *
     * @param s         字符串
     * @param separator 分隔符
     * @return 长整数数组
     */
    public static Long[] parseLongObjectArray(String s, String separator) {
        if (StringUtils.isNotBlank(s)) {
            String[] array = s.split(separator);
            Long[] result = new Long[array.length];
            for (int i = 0; i < array.length; i++) {
                try {
                    result[i] = Long.valueOf(array[i]);
                } catch (NumberFormatException e) {
                    result[i] = null; // 无法解析时结果为null
                }
            }
            return result;
        }
        return new Long[0];
    }

    /**
     * 解析转换指定字符串为整型数字，如果字符串不是整型数字格式，则返回0
     *
     * @param s 字符串
     * @return 转换后的整型数字
     */
    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    /**
     * 解析转换指定字符串为整型数字，如果字符串不是整型数字格式，则返回指定默认值
     *
     * @param s            字符串
     * @param defaultValue 默认值
     * @return 转换后的整型数字
     */
    public static int parseInt(String s, int defaultValue) {
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Integer parseInteger(String s) {
        return parseInteger(s, null);
    }

    public static Integer parseInteger(String s, Integer defaultValue) {
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 以指定分隔符拆分指定字符串，并解析为整数数组
     *
     * @param s         字符串
     * @param separator 分隔符
     * @return 整数数组
     */
    public static int[] parseIntArray(String s, String separator) {
        if (StringUtils.isNotBlank(s)) {
            String[] array = s.split(separator);
            int[] result = new int[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = parseInt(array[i]);
            }
            return result;
        }
        return new int[0];
    }

    /**
     * 以指定分隔符拆分指定字符串，并解析为整数对象数组
     *
     * @param s         字符串
     * @param separator 分隔符
     * @return 整数对象数组
     */
    public static Integer[] parseIntegerArray(String s, String separator) {
        if (StringUtils.isNotBlank(s)) {
            String[] array = s.split(separator);
            Integer[] result = new Integer[array.length];
            for (int i = 0; i < array.length; i++) {
                try {
                    result[i] = Integer.valueOf(array[i]);
                } catch (NumberFormatException e) {
                    result[i] = null; // 解析错误则对应位置为null
                }
            }
            return result;
        }
        return new Integer[0];
    }

    /**
     * 格式化指定数字对象
     *
     * @param number    数字对象
     * @param minScale  最小精度
     * @param maxScale  最大精度
     * @param toPercent 是否转换为百分比
     * @return 格式化后的字符串
     */
    public static String formatNumber(Object number, int minScale, int maxScale,
            boolean toPercent) {
        if (number == null) {
            return "";
        }
        NumberFormat format = toPercent ? NumberFormat.getPercentInstance()
                : NumberFormat.getNumberInstance();
        format.setGroupingUsed(false);
        format.setMinimumFractionDigits(minScale);
        format.setMaximumFractionDigits(maxScale);
        return format.format(number);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T minValue(Class<?> type) {
        return (T) FuncMinNumber.INSTANCE.apply(type);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T minValue(Class<?> type, int precision, int scale) {
        T typeMinValue = minValue(type);
        if (typeMinValue != null) {
            if (precision == 0) {
                return typeMinValue;
            }
            BigDecimal typeMinDecimal = new BigDecimal(typeMinValue.doubleValue()); // 负值
            // 各数值类型的最小值均为整数，转换为朴素字符串形式后，字符串长度-1即为最小值长度
            int minLength = typeMinDecimal.toPlainString().length() - 1; // 负值带有负号
            // 最小值都不是最大长度下的最大值（即-999...999），所以实际允许的整数部分长度必须小于上述最小值长度
            int intLength = precision - scale;
            // 也就意味着整数部分长度如果大于等于上述最小值长度，则数值类型最小值即为结果
            if (intLength >= minLength) {
                return typeMinValue;
            }
            // 否则，整数部分和小数部分全为9，取负号，即为结果
            BigDecimal min = BigDecimal.TEN.pow(intLength)
                    .subtract(BigDecimal.ONE.divide(BigDecimal.TEN.pow(scale)))
                    .setScale(scale, RoundingMode.HALF_UP).negate();
            if (type == Long.class) {
                return (T) Long.valueOf(min.longValue());
            } else if (type == Integer.class) {
                return (T) Integer.valueOf(min.intValue());
            } else if (type == Short.class) {
                return (T) Short.valueOf(min.shortValue());
            } else if (type == Byte.class) {
                return (T) Byte.valueOf(min.byteValue());
            } else if (type == Double.class) {
                return (T) Double.valueOf(min.doubleValue());
            } else if (type == Float.class) {
                return (T) Float.valueOf(min.floatValue());
            } else if (type == BigDecimal.class) {
                return (T) min;
            }
        }
        return null;
    }

    /**
     * 获取指定数值类型的最大值
     *
     * @param type 数值类型
     * @return 最大值
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T maxValue(Class<?> type) {
        return (T) FuncMaxNumber.INSTANCE.apply(type);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T maxValue(Class<?> type, int precision, int scale) {
        T typeMaxValue = maxValue(type);
        if (typeMaxValue != null) {
            if (precision == 0) {
                return typeMaxValue;
            }
            BigDecimal typeMaxDecimal = new BigDecimal(typeMaxValue.doubleValue());
            // 各数值类型的最大值均为整数，转换为朴素字符串形式后，字符串长度即为最大值长度
            int maxLength = typeMaxDecimal.toPlainString().length();
            // 最大值都不是最大长度下的最大值（即999...999），所以实际允许的整数部分长度必须小于上述最大值长度
            int intLength = precision - scale;
            // 也就意味着整数部分长度如果大于等于上述最大值长度，则数值类型最大值即为结果
            if (intLength >= maxLength) {
                return typeMaxValue;
            }
            // 否则，整数部分和小数部分全为9即为结果
            BigDecimal max = BigDecimal.TEN.pow(intLength)
                    .subtract(BigDecimal.ONE.divide(BigDecimal.TEN.pow(scale)))
                    .setScale(scale, RoundingMode.HALF_UP);
            if (type == Long.class) {
                return (T) Long.valueOf(max.longValue());
            } else if (type == Integer.class) {
                return (T) Integer.valueOf(max.intValue());
            } else if (type == Short.class) {
                return (T) Short.valueOf(max.shortValue());
            } else if (type == Byte.class) {
                return (T) Byte.valueOf(max.byteValue());
            } else if (type == Double.class) {
                return (T) Double.valueOf(max.doubleValue());
            } else if (type == Float.class) {
                return (T) Float.valueOf(max.floatValue());
            } else if (type == BigDecimal.class) {
                return (T) max;
            }
        }
        return null;
    }

    public static int minValueIndexOf(double[] array) {
        double min = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (min > array[i]) {
                min = array[i];
                index = i;
            }
        }
        return index;
    }

    public static int maxValueIndexOf(double[] array) {
        double max = Double.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (max < array[i]) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }

    public static int minValueIndexOf(Number[] array) {
        double min = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (min > array[i].doubleValue()) {
                min = array[i].doubleValue();
                index = i;
            }
        }
        return index;
    }

    public static int maxValueIndexOf(Number[] array) {
        double max = Double.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (max < array[i].doubleValue()) {
                max = array[i].doubleValue();
                index = i;
            }
        }
        return index;
    }

    public static byte[] int2Bytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((value >> 24) & 0xFF);
        bytes[1] = (byte) ((value >> 16) & 0xFF);
        bytes[2] = (byte) ((value >> 8) & 0xFF);
        bytes[3] = (byte) (value & 0xFF);
        return bytes;
    }

    public static int bytes2Int(byte[] bytes, int offset) {
        return (((bytes[offset] & 0xFF) << 24) | ((bytes[offset + 1] & 0xFF) << 16)
                | ((bytes[offset + 2] & 0xFF) << 8) | (bytes[offset + 3] & 0xFF));
    }

    public static int hex2Int(String hex) {
        return hex2Int(hex, 0);
    }

    public static int hex2Int(String hex, int defaultValue) {
        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
