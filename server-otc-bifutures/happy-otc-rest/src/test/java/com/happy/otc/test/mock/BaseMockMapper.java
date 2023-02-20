package com.happy.otc.test.mock;

import com.happy.otc.test.utils.ClassInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseMockMapper<VALUE, VALUE_VO> {
    public final Map<Long, VALUE> content = new LinkedHashMap<>();
    public final ClassInfo<VALUE> classInfo;
    public final ClassInfo<VALUE_VO> classInfoVO;

    public BaseMockMapper(Class<VALUE> clazz, Class<VALUE_VO> clazzVO, String primaryKey) {
        classInfo = new ClassInfo<>(clazz, primaryKey);
        classInfoVO = new ClassInfo<>(clazzVO, primaryKey);
    }

    public void clear() {
        content.clear();
    }

    public int deleteByPrimaryKey(Long commodityId) {
        VALUE result = content.remove(commodityId);
        return result == null ? 0 : 1;
    }

    public int insert(VALUE value) {
        Long key = classInfo.getKey(value);
        if (content.containsKey(key)) {
            throw new IllegalStateException("Key " + key + " already exist.");
        }
        content.put(key, value);
        return 1;
    }

    public int insertSelective(VALUE value) {
        classInfo.fillInId(value, content.keySet());
        insert(value);
        return 1;
    }

    public VALUE selectByPrimaryKey(Long key) {
        return content.get(key);
    }

    public List<VALUE> findByParam(Map<String, Object> params) {
        List<VALUE> result = new ArrayList<>();
        for (VALUE value: content.values()) {
            if (classInfo.match(value, params)) {
                result.add(value);
            }
        }
        return result;
    }

    public List<VALUE> getAll() {
        return new ArrayList<>(content.values());
    }

    public List<VALUE_VO> findVOByParam(Map<String, Object> params) {
        return findByParam(params).stream().map(this::toVO).collect(Collectors.toList());
    }

    public int updateByPrimaryKeySelective(VALUE record) {
        Long key = classInfo.getKey(record);
        if (key == null || !content.containsKey(key)) {
            throw new IllegalStateException("Key " + key + " does not exist");
        }
        classInfo.merge(record, content.get(key), content.keySet());
        return 1;
    }

    public int updateByPrimaryKey(VALUE record) {
        Long key = classInfo.getKey(record);
        if (key == null || !content.containsKey(key)) {
            throw new IllegalStateException("Key " + key + " does not exist");
        }
        VALUE result = classInfo.newInstance();
        classInfo.merge(content.get(key), result, content.keySet());
        classInfo.merge(record, result, content.keySet());
        content.put(key, result);
        return 1;
    }

    public void addAll(List<VALUE> values) {
        for (VALUE value: values) {
            Long id = classInfo.getKey(value);
            content.put(id, value);
        }
    }

    public void generate(int count, Function<Long, VALUE> generator) {
        content.clear();
        for (int i=0; i<count; i++) {
            insert(generator.apply((long) i));
        }
    }

    public VALUE_VO toVO(VALUE value) {
        return classInfoVO.convert(value, classInfo);
    }

    public VALUE fromVO(VALUE_VO value) {
        return classInfo.convert(value, classInfoVO);
    }

    public abstract List<VALUE> defaultValue();

    public void makeDefault() {
        clear();
        defaultValue().forEach(this::insertSelective);
    }
}
