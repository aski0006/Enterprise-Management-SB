# 数据库问题解决记录

## 问题描述

在项目中发现了数据库表字段与Java实体类字段名称不匹配的问题，导致应用无法正常工作。主要涉及以下几个方面：

1. `DepartmentBudget` 实体类的字段使用驼峰命名法，而数据库表使用小写下划线命名法
2. `SalaryRecord` 实体类中 `payPeriod` 的类型是 `YearMonth`，但数据库中是 `varbinary(255)`
3. 需要增强全局异常处理，提供更详细的数据库错误诊断信息

## 解决方案

### 1. 实体类字段与数据库表字段匹配

对于 `DepartmentBudget` 实体类，我们添加了 `@Column` 注解来明确映射字段名：

```java
@Column(name = "q1budget")
private Double q1Budget;

@Column(name = "q2budget")
private Double q2Budget;

@Column(name = "q3budget")
private Double q3Budget;

@Column(name = "q4budget")
private Double q4Budget;

@Column(name = "q1expenditure")
private Double q1Expenditure;

@Column(name = "q2expenditure")
private Double q2Expenditure;

@Column(name = "q3expenditure")
private Double q3Expenditure;

@Column(name = "q4expenditure")
private Double q4Expenditure;
```

### 2. 数据类型不匹配的处理

对于 `SalaryRecord` 实体类中的 `payPeriod` 字段，我们将其类型从 `YearMonth` 修改为 `String`，并添加了转换方法：

```java
@Column(name = "pay_period")
private String payPeriod; // 改为String类型，格式如 "2023-05" 表示2023年5月

// 工具方法，将YearMonth转换为String
public void setPayPeriodByYearMonth(YearMonth yearMonth) {
    this.payPeriod = yearMonth.toString();
}

// 工具方法，将String转换为YearMonth
public YearMonth getPayPeriodAsYearMonth() {
    if (this.payPeriod == null) {
        return null;
    }
    return YearMonth.parse(this.payPeriod);
}
```

### 3. 增强异常处理

添加了 `GlobalExceptionHandler` 类来处理各种数据库相关的异常，包括：

- `DataIntegrityViolationException`：处理数据完整性错误
- `SQLGrammarException`：处理SQL语法错误
- `JpaSystemException`：处理JPA系统错误
- `DataAccessException`：处理数据访问错误

### 4. 增加SQL日志配置

在 `application.properties` 中增加SQL日志配置，便于调试：

```properties
# SQL日志配置，便于排查问题
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# 启用Hibernate统计，便于识别性能问题
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=DEBUG
```

### 5. 增加错误码

在 `ErrorCode` 枚举中增加了数据库相关的错误码：

```java
DATABASE_ERROR("500", "数据库错误"),
DATA_INTEGRITY_ERROR("400", "数据完整性错误"),
SYSTEM_ERROR("500", "系统错误");
```

## 问题总结

数据库字段与实体类不匹配是常见问题，可通过以下方式避免：

1. 使用一致的命名规范，如统一使用驼峰命名法或下划线命名法
2. 在实体类中显式指定列名，避免依赖默认映射规则
3. 对于特殊类型的字段，如日期、时间等，确保数据库与实体类类型兼容，必要时添加转换方法
4. 加强异常处理，提供详细的错误日志
5. 使用SQL日志帮助调试数据库问题
6. 在数据初始化时注意检查是否已有数据，避免重复创建

## 后续改进

对于更复杂的项目，可考虑以下改进：

1. 使用 `AttributeConverter` 自动转换复杂类型
2. 引入数据库迁移工具如 Flyway 或 Liquibase
3. 为实体类编写单元测试，验证ORM映射是否正确
4. 使用DTO模式，分离表示层与数据访问层
5. 考虑增加审计功能，记录数据库操作的用户、时间等信息 