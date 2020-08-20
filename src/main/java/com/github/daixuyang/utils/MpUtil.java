package com.github.daixuyang.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.github.daixuyang.annotation.MpQuery;
import com.github.daixuyang.constant.QueryStatic;
import com.github.daixuyang.constant.QueryType;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author 小代
 */
public class MpUtil {

    /**
     * 获取所有属性
     *
     * @param object 对象
     * @return Field[]
     */
    private static Field[] getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    public static QueryWrapper<Object> generateWrapper(Object o) {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        generateWrapper(o, wrapper);
        return wrapper;
    }

    /**
     * 将驼峰转为对应数据库的下划线字段的查询表达式
     *
     * @param o       实体类
     * @param wrapper 条件构造器
     */
    public static void generateWrapper(Object o, QueryWrapper<Object> wrapper) {
        try {
            Field[] allFields = getAllFields(o);

            for (Field field : allFields) {
                field.setAccessible(true);
                if (QueryStatic.SERIAL_VERSION_UID.equals(field.getName())) {
                    continue;
                }

                // 数据表字段
                String column = QueryStatic.DEFAULT_PREFIX + Tool.humpToLine2(field.getName());

                // 值
                Object value = field.get(o);
                //默认值
                printValue(column, value);
                // 有注解的情况
                if (field.isAnnotationPresent(MpQuery.class)) {
                    MpQuery annotation = field.getAnnotation(MpQuery.class);
                    String type = annotation.type();

                    //优先使用自定义表前缀和字段属性
                    column = annotation.prefix() + QueryStatic.POINT + Tool.humpToLine2(field.getName());

                    //优先使用自定义字段
                    column = getField(column, annotation);

                    //优先使用自定义默认值
                    value = getDefaultValue(annotation, value);


                    switch (type) {
                        case QueryType.EQ:
                            wrapper.eq(ObjectUtils.isNotEmpty(value), column, value);
                            break;
                        case QueryType.LIKE:
                            wrapper.like(ObjectUtils.isNotEmpty(value), column, value);
                            break;
                        case QueryType.LIKE_LEFT:
                            wrapper.likeLeft(ObjectUtils.isNotEmpty(value), column, value);
                            break;
                        case QueryType.LIKE_RIGHT:
                            wrapper.likeRight(ObjectUtils.isNotEmpty(value), column, value);
                            break;
                        case QueryType.LE:
                            wrapper.le(ObjectUtils.isNotEmpty(value), column, value);
                            break;
                        case QueryType.LT:
                            wrapper.lt(ObjectUtils.isNotEmpty(value), column, value);
                            break;
                        case QueryType.GE:
                            wrapper.ge(ObjectUtils.isNotEmpty(value), column, value);
                            break;
                        case QueryType.GT:
                            wrapper.gt(ObjectUtils.isNotEmpty(value), column, value);
                            break;
                        case QueryType.IS_NULL:
                            wrapper.isNull(ObjectUtils.isNotEmpty(value), column);
                            break;
                        case QueryType.IS_NOT_NULL:
                            wrapper.isNotNull(ObjectUtils.isNotEmpty(value), column);
                            break;
                        case QueryType.IS_EMPTY:
                            wrapper.eq(column, QueryStatic.EMPTY);
                            break;
                        case QueryType.CUSTOMIZE:
                            wrapper.apply(!Objects.isNull(value), String.valueOf(value));
                            break;
                        case QueryType.IS_NOT_EMPTY:
                            wrapper.ne(column, QueryStatic.EMPTY);
                            break;
                        default:
                            break;
                    }
                } else {
                    wrapper.eq(ObjectUtils.isNotEmpty(value), column, value);
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void printValue(String column, Object value) {
        System.out.println("字段:" + column + "\t值:" + value);
    }

    /**
     * 获取自定义列
     *
     * @param column     条件要构造的列
     * @param annotation 注解
     * @return 条件要构造的列
     */
    private static String getField(String column, MpQuery annotation) {
        // 指定的字段
        String fd = annotation.field();
        if (Strings.isNotBlank(fd)) {
            column = annotation.prefix() + QueryStatic.POINT + Tool.humpToLine2(fd);
        }
        return column;
    }

    /**
     * 获取注解上的默认值
     *
     * @param annotation 注解
     * @param value 值
     * @return 默认值
     */
    private static Object getDefaultValue(MpQuery annotation, Object value) {
        if (Strings.isNotBlank(annotation.defaultValue())) {
            return annotation.defaultValue();
        }
        return value;
    }
}
