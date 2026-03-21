# HIS 医院信息管理系统

![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/hosoneu/his)
![GitHub](https://img.shields.io/github/license/hosoneu/his)
[![HitCount](http://hits.dwyl.com/hosoneu/his.svg)](http://hits.dwyl.com/hosoneu/his)
![GitHub last commit](https://img.shields.io/github/last-commit/hosoneu/his)

## 项目简介

HIS（Hospital Information System）医院信息管理系统是一个基于 Spring Boot + MyBatis 开发的医疗管理系统。

HIS系统的主要功能按照数据流量、流向及处理过程分为临床诊疗、药品管理、经济管理、综合管理与统计分析等。

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.1.5.RELEASE | 核心框架 |
| MyBatis | 2.0.1 | ORM框架 |
| MySQL | - | 关系型数据库 |
| Redis | - | 缓存数据库 |
| Spring Security Crypto | 5.7.3 | 密码加密(BCrypt) |
| Maven | - | 项目构建工具 |
| Lombok | 1.18.30 | 简化代码 |
| Fastjson | 1.2.54 | JSON处理 |

### 项目结构

```
his/
├── src/main/java/edu/neu/hoso/
│   ├── config/          # 配置类
│   │   ├── RedisCacheConfig.java    # Redis缓存配置
│   │   └── WebMvcConfig.java        # Web MVC配置
│   ├── controller/      # 控制层
│   │   └── UserController.java      # 用户控制器
│   ├── service/         # 业务层
│   │   ├── UserService.java         # 用户服务接口
│   │   └── impl/
│   │       └── UserServiceImpl.java # 用户服务实现
│   ├── model/           # 数据模型层
│   │   ├── User.java                # 用户实体
│   │   ├── Role.java                # 角色实体
│   │   └── UserMapper.java          # 用户数据访问
│   ├── dto/             # 数据传输对象
│   │   ├── ResultDTO.java           # 统一返回结果
│   │   └── UserValidationResult.java # 用户验证结果
│   └── example/         # MyBatis生成的Example类
├── src/main/resources/
│   ├── mapper/          # MyBatis XML映射文件
│   │   └── UserMapper.xml
│   ├── application.yml  # 应用配置文件
│   └── generatorConfig.xml # MyBatis生成器配置
└── pom.xml              # Maven依赖配置
```

## 核心功能模块

### 1. 用户管理模块

#### 用户验证
- **接口**: `UserService.validateUser(String username, String password)`
- **功能**: 根据用户名查询用户，使用 BCrypt 加密方式校验密码
- **返回**: 验证结果（是否通过、用户信息、提示消息）

#### 新增用户 (/user/insert)
- **权限要求**: 仅管理员角色（Function_id = 2）可操作
- **参数**: 
  - `user`: 用户信息（JSON格式）
  - `username`: 操作人用户名
  - `password`: 操作人密码
- **密码处理**: 用户密码使用 BCrypt 加密后存储

#### 删除用户 (/user/delete)
- **权限要求**: 仅管理员角色（Function_id = 2）可操作
- **参数**:
  - `id`: 待删除用户ID
  - `username`: 操作人用户名
  - `password`: 操作人密码

#### 其他用户操作
- 更新用户 (/user/update)
- 根据ID查询用户 (/user/getUserById)
- 根据角色查询用户 (/user/getUserByRole)
- 查询所有用户 (/user/getAllUser)
- 查询所有用户（带角色信息）(/user/getAllUserWithRole)
- 查询所有角色 (/user/getAllRole)

## 安全说明

### 密码加密

系统使用 **BCrypt** 加密算法对用户密码进行加密存储。BCrypt 是一种自适应哈希函数，具有以下特点：
- 自动包含盐值（salt），防止彩虹表攻击
- 可配置工作因子（强度），默认10轮
- 单向哈希，无法逆向解密

**数据库用户密码加密了，原文密码是123456**

### 权限控制

- 用户角色通过 `Role_ID` 关联
- 管理员角色对应 `Role_ID = 2`
- 敏感操作（新增/删除用户）需要管理员权限验证

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+

### 数据库配置

修改 `application.yml` 中的数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/his?useUnicode=true&characterEncoding=utf-8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 启动项目

```bash
# 克隆项目
git clone https://github.com/hosoneu/his.git

# 进入项目目录
cd his

# 编译打包
mvn clean package

# 运行
java -jar target/hoso-0.0.1-SNAPSHOT.jar
```

或使用 Maven 插件直接运行：

```bash
mvn spring-boot:run
```

### 访问接口

项目启动后，接口访问地址：
- 基础路径: `http://localhost:8080`
- 用户模块: `http://localhost:8080/user/*`

## API 接口示例

### 新增用户

```http
POST /user/insert?username=admin&password=123456
Content-Type: application/json

{
    "userLoginname": "newuser",
    "userPassword": "password123",
    "roleId": 1,
    "userName": "张三",
    "departmentId": 1,
    "userTitleId": 1,
    "userGender": "男",
    "userStatus": "1"
}
```

### 删除用户

```http
POST /user/delete?id=1&username=admin&password=123456
```

## 开发团队

- Mike - 核心开发
- Alan - 开发成员
- Viola - 开发成员
- 29-y - 开发成员

## 许可证

[MIT License](LICENSE)

---

**注意**: 数据库用户密码加密了，原文密码是123456
