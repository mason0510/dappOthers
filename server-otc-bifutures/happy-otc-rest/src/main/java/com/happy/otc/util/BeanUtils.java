package com.happy.otc.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * JAVABEAN工具类
 */
public class BeanUtils {

    /**
     * 对象属性考贝工具<常用工具，在service中请用super.copyObjectList()>
     *
     * @param source
     * @param type
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     *
     *             修订内容：
     *
     */
    public static <T, S> List<T> copyObjectList(List<S> source, Class<T> type) {
        if (source == null || source.size() == 0) {
            return Collections.emptyList();
        }
        List<T> target = new ArrayList<T>();
        for (S s : source) {
            target.add(copyObject(s, type));
        }
        return target;
    }

    /**
     * 对象属性考贝工具<常用工具，在service中请用super.copyObject()>
     *
     * @param source
     * @param type
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     *
     *             修订内容：
     *
     */
    public static <T, S> T copyObject(S source, Class<T> type) {
        if (source == null) {
            return null;
        }
        T target = null;
        try {
            target = type.newInstance();
            copyProperties(source, target);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return target;
    }

    public static <T, S> void copyObject(S source, T target) {
        if (source == null || target == null) {
            return;
        }
        copyProperties(source, target);
    }

    /**
     * 对象属性考贝工具<常用工具，在service中请用super.copyObject()>
     *
     * @param source
     * @param type
     * @param ignoreProperties
     *            不处理的字段
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     *
     */
    public static <T, S> T copyObject(S source, Class<T> type, String... ignoreProperties)
            throws InstantiationException, IllegalAccessException {
        if (source == null) {
            return null;
        }
        T target = type.newInstance();
        copyProperties(source, target, ignoreProperties);
        return target;
    }

    /**
     * 拷贝属性
     *
     * @param source
     * @param target
     *
     *            修订内容：
     *
     */
    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    /**
     * 拷贝属性
     *
     * @param source
     *            拷贝原对象
     * @param target
     *            拷贝目标对象
     * @param ignoreProperties
     *            不进行拷贝的属性
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 取JAVABEAN的所有属性,通过get方式出现的所有元素
     * @param javaBean
     * @return
     *
     */
    public static List<String> beanProperties(Object javaBean) {

        List<String> propList = new ArrayList<String>();
        Method[] mds = javaBean.getClass().getMethods();
        for (int i = 0; i < mds.length; i++) {
            Method md = mds[i];
            String name = md.getName();
            Class<?> type = md.getReturnType();
            if (name.startsWith("get")) {
                if (type == Integer.class || type == String.class || type == Long.class || type == Double.class
                        || type == Boolean.class || type == Byte.class
                        || type == Float.class || type == Short.class
                        || type == Date.class || type == Object[].class) {
                    String prop = name.substring(3, name.length());
                    String firstMark = prop.substring(0, 1);
                    String newProp = firstMark.toLowerCase() + prop.substring(1, prop.length());
                    propList.add(newProp);
                }
            }
        }
        return propList;
    }

    /**
     * 判断list是否为空或null
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(List<Object> obj) {
        return obj == null || obj.size() == 0;
    }

    /**
     * 获取对象属性值
     *
     * @param obj
     * @param property
     * @return
     */
    public static Object getPropertyValue(Object obj, String property) {
        try {
            return org.springframework.beans.BeanUtils.getPropertyDescriptor(obj.getClass(), property).getReadMethod()
                    .invoke(obj, new Object[] {});
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 拷贝source中不为null的值
     *
     * @param source
     * @param target
     */
    public static void copyNotNullProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 获取source中为null的属性名
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            } else if (isBaseDataType(srcValue.getClass())) {
                if (srcValue.getClass().equals(String.class)) {
                    srcValue = StringUtils.trim(srcValue.toString());
                    if (srcValue.equals("null") || srcValue.equals("undefined") || srcValue.equals("")) {
                        emptyNames.add(pd.getName());
                    }
                }
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static boolean isBaseDataType(Class<?> clazz) {
        return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class)
                || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class)
                || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class)
                || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class)
                || clazz.isPrimitive());
    }
}
