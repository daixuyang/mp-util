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

/**
 * @author 小代
 */
public class MpUtil {


    /**
     * 获取所有属性
     * @param object 对象
     * @return Field[]
     */
    private static Field[] getAllFields(Object object){
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 将驼峰转为对应数据库的下划线字段的查询表达式
     *
     * @param o 实体类
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
                System.out.println("字段:" + column + "\t值:" + value);
                if (field.isAnnotationPresent(MpQuery.class)) {
                    MpQuery annotation = field.getAnnotation(MpQuery.class);
                    String type = annotation.type();
                    // 重新赋值数据表字段
                    column = annotation.prefix() + QueryStatic.POINT + Tool.humpToLine2(field.getName());

                    //优先使用自定义字段
                    column = getField(column, annotation, value);

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

    /**
     * 获取自定义列
     *
     * @param column     条件要构造的列
     * @param annotation 注解
     * @param value      当前属性的值
     * @return 条件要构造的列
     */
    private static String getField(String column, MpQuery annotation, Object value) {
        // 指定一个字段
        String fd = annotation.field();
        if (Strings.isNotBlank(fd)) {
            column = annotation.prefix() + QueryStatic.POINT + Tool.humpToLine2(fd);
        }

        // 三目指定的字段
        String fieldMk = annotation.fieldMk();
        if (Strings.isNotBlank(fieldMk)) {
            fieldMk = fieldMk.replaceAll("\\s", "");
            // 目标状态的值
            String val = fieldMk.split("\\?")[0];
            // 自定义的两个字段
            String[] fields = fieldMk.split("\\?")[1].split(QueryStatic.LOWER_COLON);

            fd = value.equals(val) ? fields[0] : fields[1];

            column = annotation.prefix() + QueryStatic.POINT + Tool.humpToLine2(fd);
        }
        return column;
    }
}
