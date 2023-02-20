package com.happy.otc.bifutures.utill;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2018\11\21 0021.
 */
public class NumberUtil {
    public static final BigDecimal ZERO;
    public static final BigDecimal TEN;
    public static final BigDecimal HUNDRED;
    public static final BigDecimal THOUSAND;

    static {
        ZERO = BigDecimal.ZERO;
        TEN = BigDecimal.TEN;
        HUNDRED = new BigDecimal(100);
        THOUSAND = new BigDecimal(1000);
    }

    public NumberUtil() {
    }

    public static String format(double number, String pattern, RoundingMode rm) {
        DecimalFormat f = new DecimalFormat(pattern);
        f.setRoundingMode(rm);
        return f.format(number);
    }

    public static String format(double number, String pattern) {
        return format(number, pattern, RoundingMode.HALF_EVEN);
    }

    public static String format(Object number, String pattern, RoundingMode rm) {
        if(number == null) {
            return "";
        } else {
            DecimalFormat f = new DecimalFormat(pattern);
            f.setRoundingMode(rm);
            return f.format(number);
        }
    }

    public static String format(Object number, String pattern) {
        return format(number, pattern, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal roundNumber(BigDecimal number, int mode) {
        if(mode <= 0) {
            throw new IllegalArgumentException("mode必须大于0");
        } else if(number != null) {
            BigDecimal modeDec = new BigDecimal(mode);
            return number.divide(modeDec, 0, RoundingMode.FLOOR).multiply(modeDec);
        } else {
            return null;
        }
    }

    public static boolean isPositive(BigDecimal number) {
        return number != null && number.compareTo(ZERO) > 0;
    }

    public static boolean isNonnegative(BigDecimal number) {
        return number != null && number.compareTo(ZERO) >= 0;
    }

    public static BigDecimal reverseNumber(BigDecimal number) {
        return number != null?ZERO.subtract(number):null;
    }

    public static BigDecimal percent2decimal(Double d) {
        return d != null?(new BigDecimal(Double.toString(d.doubleValue()))).divide(new BigDecimal(100)).setScale(10, RoundingMode.HALF_UP):null;
    }

    public static BigDecimal percent2decimal(Float f) {
        return f != null?(new BigDecimal(Float.toString(f.floatValue()))).divide(new BigDecimal(100)).setScale(10, RoundingMode.HALF_UP):null;
    }

    public static BigDecimal double2decimal(Double d) {
        return d != null?(new BigDecimal(Double.toString(d.doubleValue()))).setScale(10, RoundingMode.HALF_UP):null;
    }

    public static BigDecimal float2decimal(Float f) {
        return f != null?(new BigDecimal(Float.toString(f.floatValue()))).setScale(10, RoundingMode.HALF_UP):null;
    }

    public static BigDecimal int2decimal(Integer i) {
        return i != null?new BigDecimal(i.intValue()):null;
    }

    public static BigDecimal long2decimal(Long l) {
        return l != null?new BigDecimal(l.longValue()):null;
    }

    public static BigDecimal divide(BigDecimal num, BigDecimal divisor, int scale) {
        return num != null && divisor != null?num.divide(divisor, scale, RoundingMode.HALF_UP):null;
    }

    public static int compare(BigDecimal num1, BigDecimal num2) {
        return num1 == null && num2 == null?0:(num1 == null?-1:(num2 == null?1:num1.compareTo(num2)));
    }

    public static BigDecimal negative(BigDecimal num) {
        return isPositive(num)?BigDecimal.ZERO.subtract(num):num;
    }

    public static BigDecimal getZero() {
        return ZERO;
    }
}
