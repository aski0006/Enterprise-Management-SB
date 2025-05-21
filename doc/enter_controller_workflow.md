# EnterController工作流程分析

## 1. 整体架构

EnterController主要处理用户登录和注册功能，是系统的入口控制器。它通过REST API接收客户端请求，并将请求转发给EnterService进行业务处理。

## 2. 类图
```mermaid
classDiagram
    direction TB
    
    class EnterController {
        -EnterService enterService
        +login(LoginRequest)
        +register(RegisterRequest)
    }
    
    class EnterService {
        -SysLogger sysLogger
        -UserRepository userRepository
        +loginService(LoginRequest)
        +registerService(RegisterRequest)
    }
    
    class UserRepository {
        +save(UserModel)
        +findById(String)
        +findByUsername(String)
    }
    
    class UserModel {
        -String id
        -String name
        -String username
        -String password
        -String email
        -String role
    }
    
    class LoginRequest {
        -String username
        -String password
    }
    
    class RegisterRequest {
        -String name
        -String username
        -String password
        -String confirmPassword
        -String email
    }
    
    EnterController --> EnterService : 使用
    EnterService --> UserRepository : 使用
    EnterService --> UserModel : 使用
    EnterController --> LoginRequest : 接收
    EnterController --> RegisterRequest : 接收
```

## 3. 登录工作流程

### 3.1 序列图
```mermaid
sequenceDiagram
    participant Client
    participant EnterController
    participant EnterService
    participant UserRepository
    participant Database
    participant AuthContext
    participant JwtUtils
    participant SysLogger
    participant MessageConstructor
    
    Client->>EnterController: POST /api/enter/login
    EnterController->>EnterService: 调用loginService()
    EnterService->>UserRepository: 查询用户名
    UserRepository->>Database: 查询用户数据
    Database-->>UserRepository: 返回查询结果
    
    alt 用户不存在
        EnterService->>SysLogger: 记录警告日志
        EnterService-->>EnterController: 返回错误结果
        EnterController-->>Client: 返回400错误
    else 密码不匹配
        EnterService->>SysLogger: 记录警告日志
        EnterService-->>EnterController: 返回错误结果
        EnterController-->>Client: 返回400错误
    else 登录成功
        EnterService->>JwtUtils: 生成JWT令牌
        JwtUtils-->>EnterService: 返回生成的令牌
        
        EnterService->>AuthContext: 设置用户ID和权限
        
        EnterService->>SysLogger: 记录登录信息
        EnterService-->>EnterController: 返回成功结果
        EnterController-->>Client: 返回200 OK和令牌
    end
```

### 3.2 工作步骤
1. 客户端发送POST请求到`/api/enter/login`接口，携带用户名和密码
2. EnterController接收请求，调用EnterService的loginService方法
3. EnterService执行以下操作：
   - 调用UserRepository根据用户名查询用户
   - 如果用户不存在，返回错误
   - 如果密码不匹配，返回错误
   - 生成JWT令牌
   - 设置用户权限
   - 记录登录日志
   - 返回包含令牌的成功响应

## 4. 注册工作流程

### 4.1 序列图
```mermaid
sequenceDiagram
    participant Client
    participant EnterController
    participant EnterService
    participant UserRepository
    participant Database
    participant SysLogger
    participant MessageConstructor
    
    Client->>EnterController: POST /api/enter/register
    EnterController->>EnterService: 调用registerService()
    
    EnterService->>UserRepository: 查询用户名是否存在
    UserRepository->>Database: 执行查询
    Database-->>UserRepository: 返回结果
    
    alt 用户名已存在
        EnterService->>SysLogger: 记录警告日志
        EnterService-->>EnterController: 返回错误结果
        EnterController-->>Client: 返回400错误
    else 密码不匹配
        EnterService->>SysLogger: 记录警告日志
        EnterService-->>EnterController: 返回错误结果
        EnterController-->>Client: 返回400错误
    else 注册成功
        EnterService->>UserRepository: 保存新用户
        UserRepository->>Database: 插入新记录
        Database-->>UserRepository: 返回插入结果
        
        EnterService->>SysLogger: 记录注册信息
        EnterService-->>EnterController: 返回成功结果
        EnterController-->>Client: 返回200 OK
    end
```

### 4.2 工作步骤
1. 客户端发送POST请求到`/api/enter/register`接口，携带注册信息（姓名、用户名、密码、确认密码、邮箱）
2. EnterController接收请求，调用EnterService的registerService方法
3. EnterService执行以下操作：
   - 检查用户名是否已存在
   - 检查密码和确认密码是否一致
   - 创建新的UserModel对象并设置属性
   - 调用UserRepository保存用户
   - 记录注册日志
   - 返回成功响应

## 5. 核心组件说明

### 5.1 EnterController
- 控制器类，负责接收HTTP请求
- 提供两个API接口：
  - `POST /api/enter/login`：处理登录请求
  - `POST /api/enter/register`：处理注册请求
- 将请求委托给EnterService处理

### 5.2 EnterService
- 服务类，实现登录和注册的核心业务逻辑
- 依赖项：
  - SysLogger：用于记录日志
  - UserRepository：用于访问数据库
- 主要方法：
  - loginService()：处理登录业务
  - registerService()：处理注册业务

### 5.3 UserRepository
- 数据访问接口，继承自JpaRepository
- 提供标准的CRUD操作
- 自定义方法：
  - findByUsername()：根据用户名查找用户

### 5.4 UserModel
- 用户实体类，映射到数据库表`user`
- 包含以下字段：
  - id：用户ID（UUID）
  - name：用户姓名
  - username：登录用户名
  - password：登录密码
  - email：邮箱地址
  - role：用户角色（admin或common）

## 6. 日志记录

EnterService使用SysLogger记录详细的登录和注册信息：
- 登录尝试（成功或失败）
- 注册尝试（成功或失败）
- 使用MessageConstructor构造格式化的日志消息

## 7. 安全性

- 使用JWT进行身份验证
- 根据用户角色分配不同权限
  - admin：管理员权限
  - common：普通用户权限
- 密码以明文形式存储（当前实现，建议在生产环境中改为哈希存储）