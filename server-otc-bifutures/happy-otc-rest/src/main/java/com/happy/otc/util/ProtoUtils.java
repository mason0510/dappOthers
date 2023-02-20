package com.happy.otc.util;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.utils.DateUtils;
import com.happy.otc.proto.ResultInfo;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.apache.logging.log4j.LogManager;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuligang on 2018/07/20
 */
public class ProtoUtils {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ProtoUtils.class);

    /**
     * 返回Boolean类型Result
     * @param result
     * @return
     */
    public static ResultInfo.ResultBooleanVO createBoolSuccess(Boolean result) {
        ResultInfo.ResultBooleanVO.Builder resultBuild = ResultInfo.ResultBooleanVO.newBuilder();
        resultBuild.setCode(ApiResponseCode.SUCCESS.get())
                .setSuccess(true)
                .setServerTime(DateUtils.getNowDate(""))
                .setData(result);
        return resultBuild.build();
    }

    /**
     * 返回String类型Result
     * @param result
     * @return
     */
    public static ResultInfo.ResultStringVO createStringSuccess(String result) {
        ResultInfo.ResultStringVO.Builder resultBuild = ResultInfo.ResultStringVO.newBuilder();
        resultBuild.setCode(ApiResponseCode.SUCCESS.get())
                .setSuccess(true)
                .setServerTime(DateUtils.getNowDate(""))
                .setData(result);
        return resultBuild.build();
    }

    /**
     * 返回自定义类型的Result
     * @param data      返回Data的值
     * @param type      返回Result的类型
     * @return
     */
    public static <T extends Message, S> T createResultSuccess(S data, T type) {
        Message.Builder builder = type.newBuilderForType();
        Descriptors.Descriptor descriptorForType = type.getDescriptorForType();

        Descriptors.FieldDescriptor code = descriptorForType.findFieldByName("code");
        Descriptors.FieldDescriptor success = descriptorForType.findFieldByName("success");
        Descriptors.FieldDescriptor message = descriptorForType.findFieldByName("message");
        Descriptors.FieldDescriptor serverTime = descriptorForType.findFieldByName("serverTime");
        Descriptors.FieldDescriptor fData = descriptorForType.findFieldByName("data");
        builder.setField(code, ApiResponseCode.SUCCESS.get());
        builder.setField(success, true);
        builder.setField(message, ApiResponseCode.SUCCESS.getName());
        builder.setField(serverTime, DateUtils.getNowDate(""));
        builder.setField(fData, data);
        return (T) builder.build();
    }

    /**
     * 返回自定义类型的PageResult
     * @param data              Data的值
     * @param currentPage       当前页
     * @param pageSize          每页条数
     * @param totalResults      总条数
     * @param type              返回Result的类型
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T extends Message, S> T createPageResultSuccess(List<S> data, int currentPage, int pageSize, long totalResults, T type) {
        T result = null;

//            builder = type.getMethod("newBuilder").invoke(null);
        Message.Builder builder = type.newBuilderForType();

        Descriptors.Descriptor descriptorForType = type.getDescriptorForType();
        Descriptors.FieldDescriptor code = descriptorForType.findFieldByName("code");
        Descriptors.FieldDescriptor success = descriptorForType.findFieldByName("success");
        Descriptors.FieldDescriptor message = descriptorForType.findFieldByName("message");
        Descriptors.FieldDescriptor serverTime = descriptorForType.findFieldByName("serverTime");
        Descriptors.FieldDescriptor fData = descriptorForType.findFieldByName("data");
        Descriptors.FieldDescriptor fCurrentPage = descriptorForType.findFieldByName("currentPage");
        Descriptors.FieldDescriptor fTotalResults = descriptorForType.findFieldByName("totalResults");
        Descriptors.FieldDescriptor fPageSize = descriptorForType.findFieldByName("pageSize");
        Descriptors.FieldDescriptor fTotalPage = descriptorForType.findFieldByName("totalPage");
        Descriptors.FieldDescriptor fHasNext = descriptorForType.findFieldByName("hasNext");
        Descriptors.FieldDescriptor fHasPrevious = descriptorForType.findFieldByName("hasPrevious");

        if (data.size() > 0) {
            builder.setField(code, ApiResponseCode.SUCCESS.get());
            builder.setField(success, true);
            builder.setField(message, ApiResponseCode.SUCCESS.getName());
        } else {
            //返回空数组时，按无数据错误处理
            builder.setField(code, ApiResponseCode.DATA_NOT_EXIST.get());
            builder.setField(success, false);
            builder.setField(message, ApiResponseCode.DATA_NOT_EXIST.getName());
        }
        builder.setField(serverTime, DateUtils.getNowDate(""));
        builder.setField(fCurrentPage, currentPage);
        builder.setField(fTotalResults, totalResults);
        builder.setField(fPageSize, pageSize);
        int totalPage = (int) Math.ceil((double) totalResults / (double) pageSize);
        boolean hasNext = currentPage < totalPage;
        boolean hasPrevious = currentPage > 1;
        builder.setField(fTotalPage, totalPage);
        builder.setField(fHasNext, hasNext);
        builder.setField(fHasPrevious, hasPrevious);
        for (S item : data) {
            builder.addRepeatedField(fData, item);
        }

        return (T) builder.build();
    }

    /**
     * JavaBean转化为Protobuf
     * @param javaBean
     * @param protoBuilder
     * @return
     */
    public static Message javaBeanToProtoBean(Object javaBean, Message.Builder protoBuilder) {

        try {
            //获取Builder的反射类
            Descriptors.Descriptor descriptor = protoBuilder.getDescriptorForType();

            //获取JavaBean的所有字段
            Field[] fields = javaBean.getClass().getDeclaredFields();
            for (Field item : fields) {
                try {
                    String fName = item.getName();
                    item.setAccessible(true);
                    Object jObject = item.get(javaBean);
                    if (null == jObject) {
                        continue;
                    }
                    //日期型在protobuf中转为Long型
                    if (jObject instanceof Date) {
                        jObject = ((Date) jObject).getTime();
                    }
                    Descriptors.FieldDescriptor fd = descriptor.findFieldByName(fName);
                    if (null != fd) {
                        if (fd.isRepeated()) {
                            //数组
                            List<Object> dList = (List<Object>) jObject; //数据为List集合
                            for (Object oItem : dList) {
                                switch (fd.getJavaType()){
                                    //枚举型
                                    case ENUM:
                                        Descriptors.EnumValueDescriptor value = getEnumValue(fd, oItem);
                                        protoBuilder.addRepeatedField(fd, value);
                                        break;
                                    //自定义类型
                                    case MESSAGE:
                                        //自定义对象继续需要通过builder来解析处理，回调
                                        Message.Builder subBuilder = protoBuilder.newBuilderForField(fd);
                                        Message pBean = javaBeanToProtoBean(oItem, subBuilder);
                                        protoBuilder.addRepeatedField(fd, pBean);
                                        break;
                                    //其他类型
                                    default:
                                        protoBuilder.addRepeatedField(fd, oItem);
                                        break;
                                }
                            }
                        } else {
                            //非数组
                            switch (fd.getJavaType()){
                                //枚举型
                                case ENUM:
                                    Descriptors.EnumValueDescriptor value = getEnumValue(fd, jObject);
                                    protoBuilder.setField(fd, value);
                                    break;
                                    //自定义类型
                                case MESSAGE:
                                    //自定义对象继续需要通过builder来解析处理，回调
                                    Message.Builder subBuilder = protoBuilder.getFieldBuilder(fd);
                                    Message pBean = javaBeanToProtoBean(jObject, subBuilder);
                                    protoBuilder.setField(fd,pBean);
                                    break;
                                    //其他类型
                                default:
                                    protoBuilder.setField(fd,jObject);
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("javaBeanToProtoBean method  item reflect error, item name:" + item.getName());
                }
            }

            Message rObject = protoBuilder.build();

            return rObject;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("convert javabean to protobuf bean error,e:", e);
            return null;
        }
    }

    /**
     * 获取枚举值
     * @param fd
     * @param jObject
     * @return
     */
    private static Descriptors.EnumValueDescriptor getEnumValue(Descriptors.FieldDescriptor fd, Object jObject) {
        Descriptors.EnumValueDescriptor value;
        if (jObject instanceof String) {
            value = fd.getEnumType().findValueByName((String) jObject);
        } else {
            Integer number;
            if (jObject instanceof Byte) {
                number = ((Byte) jObject).intValue();
            } else {
                number = (int) jObject;
            }
            value = fd.getEnumType().findValueByNumber(number);
        }
        return value;
    }

}
