package com.github.daixuyang.utils;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.github.daixuyang.annotation.MpQuery;
import com.github.daixuyang.constant.QueryStatic;
import com.github.daixuyang.constant.QueryType;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author 小代
 */
public class MpUtil {


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
    private static void generateWrapper(Object o, QueryWrapper<Object> wrapper) {
        try {
            Field[] allFields = ReflectUtil.getFields(o.getClass());

            for (Field field : allFields) {
                field.setAccessible(true);
                // 如果是static
                if(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())){
                    continue;
                }
                if(!Tool.isSimpleType(field.getType())){
                    continue;
                }

                // 数据表字段
                String column = QueryStatic.DEFAULT_PREFIX + Tool.humpToLine2(field.getName());

                // 值
                Object value = field.get(o);
                //默认值
                // 有注解的情况
                if (field.isAnnotationPresent(MpQuery.class)) {
                    MpQuery annotation = field.getAnnotation(MpQuery.class);
                    String type = annotation.type();

                    //优先使用自定义表前缀和字段属性
                    column = annotation.prefix() + QueryStatic.POINT + Tool.humpToLine2(field.getName());

                    //优先使用自定义字段
                    column = getField(column, annotation);

                    //有默认值重写默认值
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
