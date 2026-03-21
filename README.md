# HIS - 医院信息系统

![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/hosoneu/his)
![GitHub](https://img.shields.io/github/license/hosoneu/his)
[![HitCount](http://hits.dwyl.com/hosoneu/his.svg)](http://hits.dwyl.com/hosoneu/his)
![GitHub last commit](https://img.shields.io/github/last-commit/hosoneu/his)

## 项目简介

Hospital Information System（HIS，医疗管理系统）是一个基于 Spring Boot 的医院信息管理系统。HIS系统的主要功能按照数据流量、流向及处理过程分为临床诊疗、药品管理、经济管理、综合管理与统计分析等。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.1.5.RELEASE | 基础框架 |
| MyBatis | 2.0.1 | ORM框架 |
| MySQL | - | 数据库 |
| Redis | - | 缓存 |
| Spring Security | - | 安全框架（BCrypt密码加密） |
| FastJSON | 1.2.54 | JSON处理 |
| Lombok | 1.18.30 | 简化代码 |

## 项目结构

```
his/
├── src/
│   └── main/
│       ├── java/
│       │   └── edu/neu/hoso/
│       │       ├── config/                    # 配置类
│       │       │   └── RedisCacheConfig.java  # Redis缓存配置
│       │       │
│       │       ├── controller/                # 控制器层（API接口）
│       │       │   ├── UserController.java    # 用户管理
│       │       │   ├── LoginController.java   # 登录认证
│       │       │   ├── DepartmentController.java    # 科室管理
│       │       │   ├── RegistrationController.java  # 挂号管理
│       │       │   ├── DrugController.java          # 药品管理
│       │       │   ├── DispensingController.java    # 发药管理
│       │       │   ├── DiseaseController.java       # 疾病管理
│       │       │   ├── SchedulingController.java    # 排班管理
│       │       │   ├── WorkloadController.java      # 工作量统计
│       │       │   └── ...                          # 其他控制器
│       │       │
│       │       ├── service/                   # 业务逻辑层
│       │       │   ├── impl/                  # 业务实现类
│       │       │   │   ├── UserServiceImpl.java
│       │       │   │   └── ...
│       │       │   ├── UserService.java       # 业务接口
│       │       │   └── ...
│       │       │
│       │       ├── model/                     # 数据模型层
│       │       │   ├── User.java              # 用户实体
│       │       │   ├── Role.java              # 角色实体
│       │       │   ├── Department.java        # 科室实体
│       │       │   ├── *Mapper.java           # MyBatis Mapper接口
│       │       │   └── ...
│       │       │
│       │       ├── example/                   # MyBatis Example类
│       │       │   ├── UserExample.java
│       │       │   └── ...
│       │       │
│       │       ├── dto/                       # 数据传输对象
│       │       │   ├── ResultDTO.java         # 统一返回结果
│       │       │   └── LoginResult.java       # 登录结果
│       │       │
│       │       ├── converter/                 # 类型转换器
│       │       │   ├── DateConverter.java
│       │       │   └── WorkloadsConverter.java
│       │       │
│       │       ├── utils/                     # 工具类
│       │       │   ├── JsonUtils.java
│       │       │   └── RedisUtils.java
│       │       │
│       │       └── HosoApplication.java       # 启动类
│       │
│       └── resources/
│           ├── mapper/                        # MyBatis XML映射文件
│           │   ├── UserMapper.xml
│           │   └── ...
│           └── application.properties         # 配置文件
│
├── hosoneu.sql                               # 数据库脚本
├── pom.xml                                   # Maven配置
└── README.md                                 # 项目说明
```

## 核心功能模块

### 1. 用户管理模块
- 用户登录认证
- 用户信息增删改查
- 角色权限管理
- **密码BCrypt加密存储**

### 2. 挂号管理模块
- 挂号级别管理
- 挂号信息录入
- 挂号查询

### 3. 诊疗模块
- 医生工作站
- 病历首页管理
- 诊断管理
- 处方管理（西药/中药）
- 检查检验管理

### 4. 药品管理模块
- 药品信息管理
- 发药管理
- 药品库存管理

### 5. 排班管理模块
- 排班规则设置
- 排班信息管理

### 6. 工作量统计模块
- 个人工作量统计
- 工作量报表

## 数据库设计

### 核心数据表

| 表名 | 说明 |
|------|------|
| user | 用户信息表 |
| role | 角色信息表 |
| department | 科室信息表 |
| registration | 挂号信息表 |
| drugs | 药品信息表 |
| patient | 患者信息表 |
| medical_record | 病历信息表 |
| prescription | 处方信息表 |

## API 接口说明

### 用户接口 `/user`

| 接口 | 方法 | 说明 |
|------|------|------|
| /insert | POST | 新增用户（需管理员权限） |
| /delete | POST | 删除用户（需管理员权限） |
| /update | POST | 更新用户信息 |
| /getUserById | GET | 根据ID查询用户 |
| /getAllUser | GET | 获取所有用户 |

### 登录接口 `/login`

| 接口 | 方法 | 说明 |
|------|------|------|
| /LoginUser | POST | 用户登录验证 |

## 权限说明

系统采用基于角色的权限控制：
- `Function_id = 2` 为管理员角色，拥有用户增删权限
- 其他角色无权进行用户管理操作

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.x
- MySQL 5.7+
- Redis

### 启动步骤

1. 克隆项目
```bash
git clone https://github.com/hosoneu/his.git
```

2. 创建数据库并导入SQL
```bash
mysql -u root -p < hosoneu.sql
```

3. 修改数据库配置
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/his
spring.datasource.username=root
spring.datasource.password=your_password
```

4. 启动项目
```bash
mvn spring-boot:run
```

## 重要说明

**数据库用户密码加密了，原文密码是123456**

用户密码采用BCrypt算法进行加密存储，登录验证时使用BCrypt进行密码比对。

## 开发团队

本项目由东北大学开发团队维护。

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。
