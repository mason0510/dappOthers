package com.happy.otc.test.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ClassInfo<T> {
    private static class Field {
        private final String name;
        private Method getter;
        private Method setter;

        private Field(String name) {
            this.name = name;
        }
    }

    private Map<String, Field> fieldMap = new HashMap<>();
    private String primaryKey;
    private Constructor<T> constructor;

    public ClassInfo(Class<T> clazz, String primaryKey) {
        this.primaryKey = primaryKey;

        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No constructor found for class " + clazz.getSimpleName());
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName().substring(3);
            if (name.length() > 0) {
                char array[] = name.toCharArray();
                array[0] = Character.toLowerCase(array[0]);
                name = new String(array);
            }

            Field field = fieldMap.getOrDefault(name, new Field(name));
            if (isGetter(method)) {
                field.getter = method;
            } else if (isSetter(method)) {
                field.setter = method;
            }
            fieldMap.put(name, field);
        }
    }

    private static boolean isGetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
                method.getParameterTypes().length == 0 &&
                !method.getReturnType().equals(void.class) &&
                method.getName().startsWith("get");
    }

    private static boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
                method.getParameterTypes().length == 1 &&
                method.getReturnType().equals(void.class) &&
                method.getName().startsWith("set");
    }

    public Long getKey(T value) {
        try {
            return (Long) fieldMap.get(primaryKey).getter.invoke(value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void fillInId(T value, Set<Long> existingKeys) {
        if (getKey(value) == null) {
            Long newKey = existingKeys.stream().max(Comparator.comparingLong(o -> (Long) o)).orElse(0L) + 1;
            try {
                fieldMap.get(primaryKey).setter.invoke(value, newKey);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void merge(T source, T target, Set<Long> existingKeys) {
        try {
            for (Field field : fieldMap.values()) {
                Object sourceValue = field.getter.invoke(source);

                if (sourceValue == null && field.name.equals(primaryKey)) {
                    sourceValue = existingKeys.stream().max(Comparator.comparingLong(o -> (Long) o)).orElse(0L) + 1;
                }

                if (sourceValue != null) {
                    field.setter.invoke(target, sourceValue);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public T newInstance() {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public boolean match(T value, Map<String, Object> params) {
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Field field = fieldMap.get(entry.getKey());
                if (field != null) {
                    Object entryValue = field.getter.invoke(value);
                    if (entryValue == null || !entryValue.equals(entry.getValue())) {
                        return false;
                    }
                }
            }
            return true;
        } catch(IllegalAccessException | InvocationTargetException e){
            throw new IllegalStateException(e.getMessage());
        }
    }

    public <U> T convert(U otherValue, ClassInfo<U> otherInfo) {
        T result = newInstance();
        merge(result, otherValue, otherInfo);
        return result;
    }

    public <U> void merge(T thisValue, U otherValue, ClassInfo<U> otherInfo) {
        try {
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                if (otherInfo.fieldMap.containsKey(entry.getKey())) {
                    Object otherField = otherInfo.fieldMap.get(entry.getKey()).getter.invoke(otherValue);
                    entry.getValue().setter.invoke(thisValue, otherField);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}