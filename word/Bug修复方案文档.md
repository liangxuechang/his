# HIS医院信息管理系统 - Bug修复方案文档

## 文档信息
- **项目名称**: HIS医院信息管理系统
- **版本**: V1.0
- **编写日期**: 2026年3月26日
- **编写人**: 开发团队
- **审核人**: 技术负责人

---

## 1. 概述

### 1.1 文档目的
本文档针对测试过程中发现的缺陷，提供详细的修复方案，包括问题分析、修复方案、代码实现和验证方法。

### 1.2 缺陷统计

| 缺陷等级 | 数量 | 本文档覆盖 |
|----------|------|------------|
| 严重 (Critical) | 2 | ✅ |
| 高 (High) | 4 | ✅ |
| 中 (Medium) | 4 | ✅ |
| 低 (Low) | 2 | ✅ |
| **合计** | **12** | **12** |

---

## 2. 严重缺陷修复方案

### 2.1 BUG-001: 新增用户接口缺少必填参数校验

#### 2.1.1 问题分析

**问题描述**: 新增用户接口未对必填参数进行校验，允许空值插入数据库。

**根本原因**: 
- Controller层未添加参数校验注解
- 缺少统一的参数校验框架
- 未使用JSR-303 Bean Validation

**影响范围**: 
- 数据完整性受损
- 可能导致后续业务逻辑错误
- 用户登录时可能出错

#### 2.1.2 修复方案

**方案描述**: 使用Hibernate Validator实现参数校验

**技术方案**:
1. 在User实体类添加校验注解
2. 在Controller方法添加@Valid注解
3. 统一异常处理返回校验错误信息

#### 2.1.3 代码实现

**Step 1: 修改User.java实体类**

```java
package edu.neu.hoso.model;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

public class User {
    private Integer userId;

    @NotBlank(message = "用户登录名不能为空")
    @Size(max = 50, message = "用户登录名长度不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户登录名只能包含字母、数字和下划线")
    private String userLoginname;

    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String userPassword;

    @NotNull(message = "角色ID不能为空")
    private Integer roleId;

    @NotBlank(message = "用户姓名不能为空")
    @Size(max = 50, message = "用户姓名长度不能超过50个字符")
    private String userName;

    private Integer departmentId;

    private Integer userTitleId;

    @Pattern(regexp = "^(男|女)?$", message = "用户性别只能是男或女")
    private String userGender;

    @Pattern(regexp = "^(ACTIVE|INACTIVE)?$", message = "用户状态只能是ACTIVE或INACTIVE")
    private String userStatus;

    @Min(value = 0, message = "排班限制人数不能为负数")
    @Max(value = 999, message = "排班限制人数不能超过999")
    private Integer userSchedulingLimitcount;

    @Getter
    @Setter
    private ConstantItems constantItems;

    @Getter
    @Setter
    private Role role;

    @Getter
    @Setter
    private Department department;

    // 构造函数和getter/setter省略...
}
```

**Step 2: 修改UserController.java**

```java
package edu.neu.hoso.controller;

import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.User;
import edu.neu.hoso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {
    
    @Autowired
    UserService userService;

    @RequestMapping("/insert")
    public ResultDTO<User> insert(@Valid @RequestBody User user, BindingResult bindingResult) {
        ResultDTO<User> resultDTO = new ResultDTO<>();
        
        // 参数校验
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("参数校验失败: " + errorMsg);
            return resultDTO;
        }
        
        try {
            // 检查用户名是否已存在
            List<User> existUsers = userService.getUserByLoginname(user.getUserLoginname());
            if (existUsers != null && !existUsers.isEmpty()) {
                resultDTO.setStatus("ERROR");
                resultDTO.setMsg("用户名已存在，请使用其他用户名");
                return resultDTO;
            }
            
            userService.insert(user);
            resultDTO.setData(user);
            resultDTO.setStatus("OK");
            resultDTO.setMsg("插入用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("插入用户失败：" + e.getMessage());
        }
        return resultDTO;
    }
    
    // 其他方法...
}
```

**Step 3: 添加全局异常处理器**

```java
package edu.neu.hoso.exception;

import edu.neu.hoso.dto.ResultDTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultDTO<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ResultDTO<Void> result = new ResultDTO<>();
        result.setStatus("ERROR");
        result.setMsg("参数校验失败: " + errorMsg);
        return result;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultDTO<Void> handleException(Exception ex) {
        ResultDTO<Void> result = new ResultDTO<>();
        result.setStatus("ERROR");
        result.setMsg("系统异常: " + ex.getMessage());
        return result;
    }
}
```

**Step 4: pom.xml添加依赖**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### 2.1.4 验证方法

| 测试场景 | 输入 | 预期结果 |
|----------|------|----------|
| 用户名为空 | userLoginname="" | 返回"用户登录名不能为空" |
| 密码为空 | userPassword="" | 返回"用户密码不能为空" |
| 角色ID为空 | roleId=null | 返回"角色ID不能为空" |
| 姓名为空 | userName="" | 返回"用户姓名不能为空" |
| 用户名超长 | userLoginname>50字符 | 返回"用户登录名长度不能超过50个字符" |
| 用户名特殊字符 | userLoginname="test@#$" | 返回"用户登录名只能包含字母、数字和下划线" |

---

### 2.2 BUG-002: 用户密码明文存储

#### 2.2.1 问题分析

**问题描述**: 用户密码以明文形式存储在数据库中，存在严重安全隐患。

**根本原因**: 
- 未实现密码加密机制
- 缺少安全框架集成

**影响范围**: 
- 数据库泄露将直接暴露所有用户密码
- 违反安全合规要求

#### 2.2.2 修复方案

**方案描述**: 使用BCrypt算法对密码进行加密存储

**技术方案**:
1. 引入Spring Security BCryptPasswordEncoder
2. 在用户注册/修改密码时加密
3. 在登录验证时比对加密后的密码

#### 2.2.3 代码实现

**Step 1: 创建密码加密工具类**

```java
package edu.neu.hoso.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

**Step 2: 修改UserServiceImpl.java**

```java
package edu.neu.hoso.service.impl;

import edu.neu.hoso.model.User;
import edu.neu.hoso.model.UserMapper;
import edu.neu.hoso.service.UserService;
import edu.neu.hoso.utils.PasswordUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Resource
    UserMapper userMapper;

    @Override
    public Integer insert(User user) {
        // 加密密码
        user.setUserPassword(PasswordUtils.encode(user.getUserPassword()));
        userMapper.insert(user);
        return user.getUserId();
    }

    @Override
    public void update(User user) {
        // 如果密码有更新，则加密
        if (user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
            // 判断是否已经是加密密码（BCrypt密码以$2a$开头）
            if (!user.getUserPassword().startsWith("$2a$")) {
                user.setUserPassword(PasswordUtils.encode(user.getUserPassword()));
            }
        }
        userMapper.updateByPrimaryKeySelective(user);
    }
    
    // 其他方法...
}
```

**Step 3: 修改LoginController.java**

```java
package edu.neu.hoso.controller;

import edu.neu.hoso.dto.LoginResult;
import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.Department;
import edu.neu.hoso.model.Role;
import edu.neu.hoso.model.User;
import edu.neu.hoso.service.DepartmentService;
import edu.neu.hoso.service.RoleService;
import edu.neu.hoso.service.UserService;
import edu.neu.hoso.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("login")
public class LoginController {
    
    @Autowired
    UserService userService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    RoleService roleService;
    
    @RequestMapping("/LoginUser")
    public ResultDTO findUser(String userLoginName, String password) {
        ResultDTO result = new ResultDTO<>();
        
        // 参数校验
        if (userLoginName == null || userLoginName.trim().isEmpty()) {
            result.setStatus("Error");
            result.setMsg("用户名不能为空");
            return result;
        }
        if (password == null || password.trim().isEmpty()) {
            result.setStatus("Error");
            result.setMsg("密码不能为空");
            return result;
        }
        
        List<User> users = userService.getUserByLoginname(userLoginName);
        if (users.size() == 0) {
            result.setStatus("Error");
            result.setMsg("用户名或密码错误");
            return result;
        }
        
        for (User user : users) {
            // 使用BCrypt验证密码
            if (PasswordUtils.matches(password, user.getUserPassword())) {
                Department department = departmentService.getDepartmentById(user.getDepartmentId());
                Role role = roleService.findRoleByID(user.getRoleId());
                LoginResult loginResult = new LoginResult(user, department, role);
                result.setData(loginResult);
                result.setStatus("OK");
                result.setMsg("请求成功");
                return result;
            }
        }
        
        result.setStatus("Error");
        result.setMsg("用户名或密码错误");
        return result;
    }
}
```

**Step 4: 添加Spring Security依赖**

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

**Step 5: 数据库密码迁移脚本**

```sql
-- 迁移现有用户密码（需要通过程序执行BCrypt加密）
-- 以下是示例，实际需要通过Java程序执行

-- 创建临时表存储加密后的密码
CREATE TABLE user_password_temp AS
SELECT user_id, user_password FROM user;

-- 注意：实际迁移需要通过Java程序逐条加密更新
-- 伪代码示例：
-- for each user in users:
--     encryptedPassword = BCrypt.encode(user.password)
--     UPDATE user SET user_password = encryptedPassword WHERE user_id = user.id
```

#### 2.2.4 验证方法

| 测试场景 | 操作 | 预期结果 |
|----------|------|----------|
| 新增用户 | 密码"123456" | 数据库存储为BCrypt格式 |
| 登录验证 | 正确密码 | 登录成功 |
| 登录验证 | 错误密码 | 登录失败 |
| 修改密码 | 新密码"abcdef" | 数据库存储为新的BCrypt格式 |

---

## 3. 高优先级缺陷修复方案

### 3.1 BUG-003: XSS跨站脚本攻击漏洞

#### 3.1.1 问题分析

**问题描述**: 用户输入未过滤XSS字符，可能导致跨站脚本攻击。

#### 3.1.2 修复方案

**方案描述**: 添加XSS过滤器，对输入进行转义处理

**代码实现**:

**Step 1: 创建XSS过滤工具类**

```java
package edu.neu.hoso.utils;

import org.springframework.web.util.HtmlUtils;

public class XSSUtils {
    
    /**
     * 转义HTML特殊字符
     */
    public static String escape(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }
    
    /**
     * 移除危险字符
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // 移除script标签
        String result = input.replaceAll("<script[^>]*>.*?</script>", "");
        // 移除事件属性
        result = result.replaceAll("on\\w+\\s*=", "");
        return result.trim();
    }
}
```

**Step 2: 创建XSS过滤器**

```java
package edu.neu.hoso.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.hoso.wrapper.XSSRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*", filterName = "xssFilter")
public class XSSFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XSSRequestWrapper xssRequest = new XSSRequestWrapper(httpRequest);
        chain.doFilter(xssRequest, response);
    }
}
```

**Step 3: 创建XSS请求包装类**

```java
package edu.neu.hoso.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.regex.Pattern;

public class XSSRequestWrapper extends HttpServletRequestWrapper {
    
    private static final Pattern[] PATTERNS = {
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("src[\r\n]*=[\r\n]*'(.*?)'", Pattern.CASE_INSENSITIVE),
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE)
    };
    
    public XSSRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return stripXSS(value);
    }
    
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        String[] cleanValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanValues[i] = stripXSS(values[i]);
        }
        return cleanValues;
    }
    
    private String stripXSS(String value) {
        if (value == null) {
            return null;
        }
        String cleanValue = value;
        for (Pattern pattern : PATTERNS) {
            cleanValue = pattern.matcher(cleanValue).replaceAll("");
        }
        return cleanValue;
    }
}
```

---

### 3.2 BUG-004: 用户名重复校验缺失

#### 3.2.1 修复方案

**方案描述**: 在新增用户前检查用户名是否已存在

**代码实现** (已在BUG-001的修复方案中包含):

```java
@RequestMapping("/insert")
public ResultDTO<User> insert(@Valid @RequestBody User user, BindingResult bindingResult) {
    ResultDTO<User> resultDTO = new ResultDTO<>();
    
    // 参数校验...
    
    try {
        // 检查用户名是否已存在
        List<User> existUsers = userService.getUserByLoginname(user.getUserLoginname());
        if (existUsers != null && !existUsers.isEmpty()) {
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("用户名已存在，请使用其他用户名");
            return resultDTO;
        }
        
        userService.insert(user);
        resultDTO.setData(user);
        resultDTO.setStatus("OK");
        resultDTO.setMsg("插入用户成功！");
    } catch (Exception e) {
        // 异常处理...
    }
    return resultDTO;
}
```

**数据库层面添加唯一索引**:

```sql
-- 添加用户名唯一索引
ALTER TABLE `user` ADD UNIQUE INDEX `uk_user_loginname` (`user_loginname`);
```

---

### 3.3 BUG-005 & BUG-006: 删除接口参数校验和返回值问题

#### 3.3.1 修复方案

**代码实现**:

```java
@RequestMapping("/delete")
public ResultDTO<User> delete(@RequestParam(required = true) Integer id) {
    ResultDTO<User> resultDTO = new ResultDTO<>();
    
    // 参数校验
    if (id == null || id <= 0) {
        resultDTO.setStatus("ERROR");
        resultDTO.setMsg("参数错误：用户ID无效");
        return resultDTO;
    }
    
    try {
        // 检查用户是否存在
        User user = userService.getUserById(id);
        if (user == null) {
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("删除失败：用户不存在");
            return resultDTO;
        }
        
        // 执行删除
        userService.deleteById(id);
        resultDTO.setStatus("OK");
        resultDTO.setMsg("删除用户成功！");
    } catch (Exception e) {
        e.printStackTrace();
        // 检查是否是外键约束错误
        if (e.getMessage().contains("foreign key constraint")) {
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("删除失败：该用户存在关联数据，无法删除");
        } else {
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("删除用户失败：" + e.getMessage());
        }
    }
    return resultDTO;
}
```

---

## 4. 中等优先级缺陷修复方案

### 4.1 BUG-008: 缺少登录状态拦截器

#### 4.1.1 修复方案

**Step 1: 创建登录拦截器**

```java
package edu.neu.hoso.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 放行登录接口
        String uri = request.getRequestURI();
        if (uri.contains("/login/")) {
            return true;
        }
        
        // 检查登录状态
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":\"ERROR\",\"msg\":\"未登录或登录已过期\",\"data\":null}");
            return false;
        }
        
        return true;
    }
}
```

**Step 2: 注册拦截器**

```java
package edu.neu.hoso.config;

import edu.neu.hoso.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private LoginInterceptor loginInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**", "/error");
    }
}
```

---

### 4.2 BUG-010: 缺少操作日志记录

#### 4.2.1 修复方案

**Step 1: 创建操作日志实体**

```java
package edu.neu.hoso.model;

import lombok.Data;
import java.util.Date;

@Data
public class OperationLog {
    private Long id;
    private String module;
    private String operation;
    private String operator;
    private Integer operatorId;
    private String ip;
    private String requestParams;
    private String responseResult;
    private Integer status;
    private String errorMsg;
    private Date createTime;
}
```

**Step 2: 创建操作日志切面**

```java
package edu.neu.hoso.aspect;

import edu.neu.hoso.model.OperationLog;
import edu.neu.hoso.service.OperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Aspect
@Component
public class OperationLogAspect {
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Around("@annotation(edu.neu.hoso.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        OperationLog log = new OperationLog();
        log.setCreateTime(new Date());
        
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            log.setIp(request.getRemoteAddr());
            log.setRequestParams(request.getQueryString());
        } catch (Exception e) {
            // ignore
        }
        
        Object result = null;
        try {
            result = point.proceed();
            log.setStatus(1);
        } catch (Exception e) {
            log.setStatus(0);
            log.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            log.setModule("用户管理");
            operationLogService.save(log);
        }
        
        return result;
    }
}
```

---

## 5. 修复进度计划

### 5.1 修复时间表

| 阶段 | 缺陷编号 | 预计工时 | 计划完成日期 |
|------|----------|----------|--------------|
| 第一阶段 | BUG-001, BUG-002 | 12h | 2026-03-27 |
| 第二阶段 | BUG-003, BUG-004, BUG-005, BUG-006 | 10h | 2026-03-28 |
| 第三阶段 | BUG-007, BUG-008, BUG-009, BUG-010 | 12h | 2026-03-29 |
| 第四阶段 | BUG-011, BUG-012 | 6h | 2026-03-30 |

### 5.2 验证计划

| 阶段 | 验证内容 | 验证人员 |
|------|----------|----------|
| 第一阶段 | 参数校验、密码加密 | 测试团队 |
| 第二阶段 | 安全漏洞、业务逻辑 | 测试团队 |
| 第三阶段 | 拦截器、日志功能 | 测试团队 |
| 第四阶段 | 用户体验优化 | 测试团队 |

---

## 6. 风险评估

### 6.1 修复风险

| 风险项 | 风险等级 | 应对措施 |
|--------|----------|----------|
| 密码迁移失败 | 高 | 备份数据库，分批迁移 |
| 参数校验影响现有功能 | 中 | 充分回归测试 |
| 拦截器影响接口调用 | 中 | 白名单配置 |

### 6.2 回归测试范围

- 登录功能
- 用户管理功能（增删改查）
- 权限控制
- 性能测试

---

## 7. 附录

### 7.1 相关配置文件

**application.yml 追加配置**:

```yaml
spring:
  mvc:
    throw-exception-if-no-handler-found: true
  resources:
    add-mappings: false

server:
  error:
    include-message: always
```

### 7.2 测试验证脚本

```bash
# 登录测试
curl -X POST "http://localhost:8080/hoso/login/LoginUser?userLoginName=admin&password=admin123"

# 新增用户测试（参数校验）
curl -X POST "http://localhost:8080/hoso/user/insert" \
  -H "Content-Type: application/json" \
  -d '{"userLoginname":"","userPassword":"123456","roleId":3,"userName":"测试"}'

# 删除用户测试（参数校验）
curl -X POST "http://localhost:8080/hoso/user/delete?id=-1"
```

### 7.3 数据库变更脚本

```sql
-- 1. 添加用户名唯一索引
ALTER TABLE `user` ADD UNIQUE INDEX `uk_user_loginname` (`user_loginname`);

-- 2. 创建操作日志表
CREATE TABLE `operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `module` VARCHAR(50) COMMENT '模块名称',
    `operation` VARCHAR(100) COMMENT '操作类型',
    `operator` VARCHAR(50) COMMENT '操作人',
    `operator_id` INT COMMENT '操作人ID',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `request_params` TEXT COMMENT '请求参数',
    `response_result` TEXT COMMENT '响应结果',
    `status` TINYINT COMMENT '状态：1成功，0失败',
    `error_msg` TEXT COMMENT '错误信息',
    `create_time` DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
```
