#### MyBatis plus 条件构造器

```xml
    <dependency>
        <groupId>com.github.daixuyang</groupId>
        <artifactId>mp-util</artifactId>
        <version>1.0.8.RELEASE</version>
    </dependency>
```

* 举个栗子

```java
 /**
    * 查询列表数据
    * @param form 参数
    * @return 返回table列表
    */
    @Override
    public CommonTable findList(FormCdyyTaskCletveEomic form){
        Page<CdyyTaskCletveEomic> page = new Page<>(ServletUtils.getParameterToInt("page"), ServletUtils.getParameterToInt("limit"));
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        MpUtil.generateWrapper(form, wrapper);
        List<CdyyTaskCletveEomicVo> list = cdyyTaskCletveEomicMapper.findList(page, wrapper);
        return CommonTable.initCommonTable(0, null, page.getTotal(), list);
    }
```

> 说明： 如果form对象里面的某些属性有值，MpUtil.generateWrapper(form, wrapper)，这行代码会自动将有值得构建为mybatis plus 的eq条件表达式。

* 对象属性上也可以通过注解指定查询方式

```java
@MpQuery(type = QueryType.LIKE)
private String fileName;
```

* 注解支持的类型

```java
public class QueryType {

    public static final String EQ = "eq";
    public static final String LIKE = "like";
    public static final String LIKE_LEFT = "likeLeft";
    public static final String LIKE_RIGHT = "likeRight";
    /** 小于等于 */
    public static final String LE = "le";
    /** 小于 */
    public static final String LT = "lt";
    /** 大于等于 */
    public static final String GE = "ge";
    /** 大于 */
    public static final String GT = "gt";

    /** 为 null */
    public static final String IS_NULL = "isNull";

    /** 不为 null */
    public static final String IS_NOT_NULL = "isNotNull";

    /** 为空 */
    public static final String IS_EMPTY = "isEmpty";

    /** 不为空 */
    public static final String IS_NOT_EMPTY = "isNotEmpty";

    /** 忽略字段 */
    public static final String IGNORE = "ignore";


}
```

