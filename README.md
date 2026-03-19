# HIS 医院信息管理系统

Hospital Information System (HIS) 是一个基于Spring Boot + MyBatis的医疗管理系统，用于医院的日常运营管理。

## 项目结构

```
src/main/java/edu/neu/hoso/
├── config/          # 配置类
├── controller/      # 控制层（Controller）
│   ├── UserController.java    # 用户管理控制器
│   ├── DepartmentController.java # 科室管理控制器
│   ├── Doctor*Controller.java # 医生相关控制器
│   └── ...                    # 其他业务控制器
├── converter/       # 数据转换器
├── dto/             # 数据传输对象
│   ├── ResultDTO.java        # 通用返回结果
│   ├── UserAuthDTO.java      # 用户认证结果DTO
│   └── UserRequestDTO.java   # 用户操作请求DTO
├── example/         # MyBatis Example查询条件类
├── model/           # 数据模型（Model + Mapper）
│   ├── User.java              # 用户实体类
│   ├── UserMapper.java        # User Mapper接口
│   ├── Role.java              # 角色实体类
│   ├── Department.java        # 科室实体类
│   └── ...                    # 其他业务模型
├── service/         # 业务逻辑层（Service）
│   ├── UserService.java       # 用户服务接口
│   ├── impl/                  # 服务实现类
│   │   ├── UserServiceImpl.java # 用户服务实现
│   │   └── ...                # 其他业务服务实现
└── utils/           # 工具类

src/main/resources/
├── mapper/          # MyBatis Mapper XML文件
│   ├── UserMapper.xml        # User SQL映射
│   └── ...                    # 其他Mapper XML
└── application.properties # 应用配置文件
```

## 核心功能模块

| 模块 | 功能说明 |
|------|----------|
| **用户管理** | 用户增删改查、角色权限控制、密码加密存储 |
| **科室管理** | 科室信息维护 |
| **医生诊疗** | 门诊医生工作站、电子病历、处方开具 |
| **药品管理** | 药品信息、库存管理 |
| **挂号系统** | 患者挂号、排班管理 |
| **统计分析** | 工作量统计、医疗数据统计 |

## 技术栈

- **后端框架**: Spring Boot 2.1.5
- **ORM框架**: MyBatis 2.0.1
- **数据库**: MySQL
- **安全框架**: jBCrypt（BCrypt密码加密）
- **缓存**: Redis
- **JSON处理**: FastJSON 1.2.54
- **工具库**: Lombok 1.18.30

## 安全特性

### BCrypt 密码加密
系统采用jBCrypt提供的BCrypt强哈希加密算法存储用户密码。

> **重要提示**: 数据库用户密码加密了，原文密码是123456

### 权限控制
- 角色功能权限：通过`function_id`区分用户权限
- 管理员角色：`function_id = 2` 拥有用户管理权限
- 敏感操作（新增/删除用户）需要管理员身份验证

## API接口说明

### 用户管理接口

| 接口 | 请求方式 | 参数 | 说明 | 权限要求 |
|------|----------|------|------|----------|
| `/user/insert` | POST | `username`, `password`, `user` | 新增用户 | 需要管理员 |
| `/user/delete` | POST | `username`, `password`, `userId` | 删除用户 | 需要管理员 |
| `/user/update` | POST | `user` | 更新用户信息 | - |
| `/user/getUserById` | GET | `id` | 根据ID查询用户 | - |
| `/user/getAllUser` | GET | - | 查询所有用户 | - |
| `/user/getAllUserWithRole` | GET | - | 查询所有用户（含角色信息） | - |
| `/user/getAllRole` | GET | - | 查询所有角色 | - |

### 请求示例

**新增用户**
```json
{
  "username": "admin",
  "password": "123456",
  "user": {
    "userLoginname": "newuser",
    "userPassword": "123456",
    "roleId": 3,
    "userName": "新用户",
    "departmentId": 1
  }
}
```

**删除用户**
```json
{
  "username": "admin",
  "password": "123456",
  "userId": 100
}
```

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.x
- MySQL 5.7+
- Redis（可选）

### 数据库配置

1. 创建数据库并导入脚本：
```bash
mysql -u root -p < hosoneu.sql
```

2. 修改 `application.properties` 中的数据库连接配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hosoneu?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=your_password
```

### 启动应用

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn package
java -jar target/hoso-0.0.1-SNAPSHOT.jar
```

## 开发说明

### 新增用户流程
1. 接收请求（包含操作员账号密码和新用户信息）
2. 验证操作员身份和密码（BCrypt比对）
3. 检查操作员是否为管理员角色（`function_id = 2`）
4. 对新用户密码进行BCrypt加密
5. 持久化到数据库

### 删除用户流程
1. 接收请求（包含操作员账号密码和待删除用户ID）
2. 验证操作员身份和密码（BCrypt比对）
3. 检查操作员是否为管理员角色（`function_id = 2`）
4. 执行删除操作

## 项目状态

- ✅ 基础框架搭建完成
- ✅ MyBatis实体和Mapper生成完成
- ✅ Redis缓存集成完成
- ✅ 用户权限控制（BCrypt加密）完成
- 🔄 持续开发完善中...

> TestController、RedisController 为测试使用，测试验证通过可正常进行CRUD操作和缓存操作。
