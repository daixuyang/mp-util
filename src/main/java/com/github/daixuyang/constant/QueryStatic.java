package com.github.daixuyang.constant;

/**
 * 常用常量
 * @author 代旭杨
 */
public class QueryStatic {
    public static final String SERIAL_VERSION_UID = "serialVersionUID";
    /**
     * 默认映射的表名
     */
    public static final String DEFAULT_PREFIX = "t.";
    public static final String EMPTY = "";
    public static final String LOWER_COLON = ":";
    public static final String LOWER_QUESTION = "?";
    public static final String POINT = ".";


    /**
     * 根据表名获取前缀
     * @param field 表映射的名称
     * @return 表名+列名
     */
    public static String tableField(String field) {
        return QueryStatic.DEFAULT_PREFIX + field;
    }
}
