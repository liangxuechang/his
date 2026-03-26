# UserController Bug修复方案文档

## 文档信息
- **项目名称**: HOSO 医院管理系统
- **修复模块**: 用户管理模块 (UserController)
- **文档版本**: V1.0
- **编写日期**: 2026-03-26

---

## 一、修复概述

### 1.1 修复目标
根据缺陷报告中的11个缺陷，制定详细的修复方案，提升系统的稳定性、安全性和用户体验。

### 1.2 修复范围
- LoginController - 登录接口
- UserController - 用户管理接口
- 新增拦截器 - 登录权限校验
- 新增全局异常处理

### 1.3 修复优先级
| 优先级 | 缺陷数量 | 修复顺序 |
|--------|----------|----------|
| P0 - 立即修复 | 4 | 第1阶段 |
| P1 - 优先修复 | 4 | 第2阶段 |
| P2 - 后续修复 | 3 | 第3阶段 |

---

## 二、第1阶段修复方案（P0 - 立即修复）

### 2.1 BUG-011: 用户接口缺乏登录权限校验

#### 问题描述
所有用户管理接口都未进行登录状态校验，未登录用户可以直接调用接口进行操作。

#### 修复方案
**1. 创建登录拦截器**

新建文件：`src/main/java/edu/neu/hoso/interceptor/LoginInterceptor.java`

```java
package edu.neu.hoso.interceptor;

import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求（跨域预检）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            PrintWriter writer = response.getWriter();
            ResultDTO<Object> result = new ResultDTO<>();
            result.setStatus("UNAUTHORIZED");
            result.setMsg("用户未登录，请先登录");
            writer.write(result.toString());
            writer.flush();
            writer.close();
            return false;
        }
        return true;
    }
}
```

**2. 注册拦截器**

新建文件：`src/main/java/edu/neu/hoso/config/WebConfig.java`

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
                .addPathPatterns("/user/**", "/doctor/**", "/admin/**")
                .excludePathPatterns("/login/**", "/error");
    }
}
```

**3. 修改登录接口，登录成功后存入Session**

修改文件：`src/main/java/edu/neu/hoso/controller/LoginController.java`

```java
@RequestMapping("/LoginUser")
public ResultDTO findUser(String userLoginName, String password, HttpSession session) {
    ResultDTO result = new ResultDTO<>();
    // ... 原有验证逻辑 ...
    
    for (User user : users) {
        if (user.getUserPassword().equals(password)) {
            // 登录成功，存入Session
            session.setAttribute("user", user);
            
            Department department = departmentService.getDepartmentById(user.getDepartmentId());
            Role role = roleService.findRoleByID(user.getRoleId());
            LoginResult loginResult = new LoginResult(user, department, role);
            result.setData(loginResult);
            result.setStatus("OK");
            result.setMsg("请求成功");
            return result;
        }
    }
    // ... 原有逻辑 ...
}
```

---

### 2.2 BUG-003: 新增用户未校验登录名重复

#### 问题描述
新增用户时未检查userLoginname是否已存在，当插入重复登录名时，会导致数据库唯一约束异常。

#### 修复方案

**修改文件：`src/main/java/edu/neu/hoso/service/impl/UserServiceImpl.java`**

```java
@Override
public Integer insert(User user) {
    // 检查登录名是否已存在
    if (user.getUserLoginname() == null || user.getUserLoginname().trim().isEmpty()) {
        throw new RuntimeException("登录名不能为空");
    }
    
    List<User> existUsers = getUserByLoginname(user.getUserLoginname());
    if (existUsers != null && !existUsers.isEmpty()) {
        throw new RuntimeException("该登录名已存在：" + user.getUserLoginname());
    }
    
    userMapper.insert(user);
    return user.getUserId();
}
```

---

### 2.3 BUG-005: 新增用户未过滤XSS攻击

#### 问题描述
新增用户接口未对用户输入进行XSS过滤，当传入包含脚本代码的字符串时，可能导致XSS攻击。

#### 修复方案

**1. 添加依赖（pom.xml）**

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.9</version>
</dependency>
```

**2. 修改User实体类，在setter中进行转义**

修改文件：`src/main/java/edu/neu/hoso/model/User.java`

```java
import org.apache.commons.lang3.StringEscapeUtils;

public void setUserLoginname(String userLoginname) {
    this.userLoginname = userLoginname == null ? null : StringEscapeUtils.escapeHtml4(userLoginname.trim());
}

public void setUserName(String userName) {
    this.userName = userName == null ? null : StringEscapeUtils.escapeHtml4(userName.trim());
}

public void setUserPassword(String userPassword) {
    this.userPassword = userPassword == null ? null : userPassword.trim();
}
```

---

### 2.4 BUG-002: 新增用户接口缺乏必填参数校验

#### 问题描述
新增用户接口未对必填字段进行校验，当传入空对象或缺少必填字段时，会导致数据库插入null值或抛出异常。

#### 修复方案

**修改文件：`src/main/java/edu/neu/hoso/service/impl/UserServiceImpl.java`**

```java
@Override
public Integer insert(User user) {
    // 参数校验
    if (user == null) {
        throw new RuntimeException("用户对象不能为空");
    }
    
    if (user.getUserLoginname() == null || user.getUserLoginname().trim().isEmpty()) {
        throw new RuntimeException("登录名不能为空");
    }
    
    if (user.getUserPassword() == null || user.getUserPassword().trim().isEmpty()) {
        throw new RuntimeException("密码不能为空");
    }
    
    if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
        throw new RuntimeException("用户姓名不能为空");
    }
    
    if (user.getRoleId() == null) {
        throw new RuntimeException("角色ID不能为空");
    }
    
    if (user.getDepartmentId() == null) {
        throw new RuntimeException("科室ID不能为空");
    }
    
    // 检查登录名是否已存在
    List<User> existUsers = getUserByLoginname(user.getUserLoginname());
    if (existUsers != null && !existUsers.isEmpty()) {
        throw new RuntimeException("该登录名已存在：" + user.getUserLoginname());
    }
    
    // 长度校验
    if (user.getUserLoginname().length() > 50) {
        throw new RuntimeException("登录名长度不能超过50个字符");
    }
    
    if (user.getUserPassword().length() > 100) {
        throw new RuntimeException("密码长度不能超过100个字符");
    }
    
    if (user.getUserName().length() > 50) {
        throw new RuntimeException("用户姓名长度不能超过50个字符");
    }
    
    userMapper.insert(user);
    return user.getUserId();
}
```

---

## 三、第2阶段修复方案（P1 - 优先修复）

### 3.1 BUG-001: 登录接口缺乏参数校验

#### 修复方案

**修改文件：`src/main/java/edu/neu/hoso/controller/LoginController.java`**

```java
@RequestMapping("/LoginUser")
public ResultDTO findUser(String userLoginName, String password, HttpSession session) {
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
    
    // ... 原有逻辑 ...
}
```

---

### 3.2 BUG-007: 删除用户未校验用户是否存在

#### 修复方案

**修改文件：`src/main/java/edu/neu/hoso/controller/UserController.java`**

```java
@RequestMapping("/delete")
public ResultDTO<User> delete(Integer id) {
    ResultDTO resultDTO = new ResultDTO();
    try {
        // 参数校验
        if (id == null) {
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("用户ID不能为空");
            return resultDTO;
        }
        
        if (id <= 0) {
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("用户ID必须大于0");
            return resultDTO;
        }
        
        // 检查用户是否存在
        User user = userService.getUserById(id);
        if (user == null) {
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("用户不存在，ID：" + id);
            return resultDTO;
        }
        
        userService.deleteById(id);
        resultDTO.setStatus("OK");
        resultDTO.setMsg("删除用户成功！");
    } catch (Exception e) {
        e.printStackTrace();
        resultDTO.setStatus("ERROR");
        resultDTO.setMsg("删除用户失败：" + e.getMessage());
    }
    return resultDTO;
}
```

---

### 3.3 BUG-004: 新增用户未校验字段长度

#### 修复方案
已在BUG-002修复方案中包含长度校验，无需额外修改。

---

### 3.4 BUG-006: 新增用户未处理空对象请求

#### 修复方案
已在BUG-002修复方案中包含空对象校验，无需额外修改。

---

## 四、第3阶段修复方案（P2 - 后续修复）

### 4.1 BUG-008: 删除用户未校验空ID参数

#### 修复方案
已在BUG-007修复方案中包含空ID校验，无需额外修改。

---

### 4.2 BUG-009: 删除用户未校验负数ID

#### 修复方案
已在BUG-007修复方案中包含负数ID校验，无需额外修改。

---

### 4.3 BUG-010: 删除用户未处理类型转换异常

#### 修复方案

**创建全局异常处理器**

新建文件：`src/main/java/edu/neu/hoso/exception/GlobalExceptionHandler.java`

```java
package edu.neu.hoso.exception;

import edu.neu.hoso.dto.ResultDTO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultDTO handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        ResultDTO result = new ResultDTO();
        result.setStatus("ERROR");
        result.setMsg("参数类型错误：" + e.getName() + "应为" + e.getRequiredType().getSimpleName());
        return result;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResultDTO handleRuntimeException(RuntimeException e) {
        ResultDTO result = new ResultDTO();
        result.setStatus("ERROR");
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(Exception.class)
    public ResultDTO handleException(Exception e) {
        ResultDTO result = new ResultDTO();
        result.setStatus("ERROR");
        result.setMsg("系统异常：" + e.getMessage());
        return result;
    }
}
```

---

## 五、完整修复后的代码

### 5.1 修复后的UserController.java

```java
package edu.neu.hoso.controller;

import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.User;
import edu.neu.hoso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {
    
    @Autowired
    UserService userService;

    @RequestMapping("/insert")
    public ResultDTO<User> insert(@RequestBody User user) {
        ResultDTO resultDTO = new ResultDTO();
        try {
            userService.insert(user);
            resultDTO.setData(user);
            resultDTO.setStatus("OK");
            resultDTO.setMsg("插入用户成功！");
        } catch (RuntimeException e) {
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("插入用户失败：" + e.getMessage());
        }
        return resultDTO;
    }

    @RequestMapping("/delete")
    public ResultDTO<User> delete(@RequestParam Integer id) {
        ResultDTO resultDTO = new ResultDTO();
        try {
            // 参数校验
            if (id == null) {
                resultDTO.setStatus("ERROR");
                resultDTO.setMsg("用户ID不能为空");
                return resultDTO;
            }
            
            if (id <= 0) {
                resultDTO.setStatus("ERROR");
                resultDTO.setMsg("用户ID必须大于0");
                return resultDTO;
            }
            
            // 检查用户是否存在
            User user = userService.getUserById(id);
            if (user == null) {
                resultDTO.setStatus("ERROR");
                resultDTO.setMsg("用户不存在，ID：" + id);
                return resultDTO;
            }
            
            userService.deleteById(id);
            resultDTO.setStatus("OK");
            resultDTO.setMsg("删除用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("删除用户失败：" + e.getMessage());
        }
        return resultDTO;
    }

    // ... 其他方法保持不变 ...
}
```

---

### 5.2 修复后的UserServiceImpl.java

```java
package edu.neu.hoso.service.impl;

import edu.neu.hoso.example.RoleExample;
import edu.neu.hoso.example.UserExample;
import edu.neu.hoso.model.Role;
import edu.neu.hoso.model.RoleMapper;
import edu.neu.hoso.model.User;
import edu.neu.hoso.model.UserMapper;
import edu.neu.hoso.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Resource
    UserMapper userMapper;

    @Resource
    RoleMapper roleMapper;

    @Override
    public Integer insert(User user) {
        // 参数校验
        if (user == null) {
            throw new RuntimeException("用户对象不能为空");
        }
        
        if (StringUtils.isEmpty(user.getUserLoginname())) {
            throw new RuntimeException("登录名不能为空");
        }
        
        if (StringUtils.isEmpty(user.getUserPassword())) {
            throw new RuntimeException("密码不能为空");
        }
        
        if (StringUtils.isEmpty(user.getUserName())) {
            throw new RuntimeException("用户姓名不能为空");
        }
        
        if (user.getRoleId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        
        if (user.getDepartmentId() == null) {
            throw new RuntimeException("科室ID不能为空");
        }
        
        // 检查登录名是否已存在
        List<User> existUsers = getUserByLoginname(user.getUserLoginname());
        if (existUsers != null && !existUsers.isEmpty()) {
            throw new RuntimeException("该登录名已存在：" + user.getUserLoginname());
        }
        
        // 长度校验
        if (user.getUserLoginname().length() > 50) {
            throw new RuntimeException("登录名长度不能超过50个字符");
        }
        
        if (user.getUserPassword().length() > 100) {
            throw new RuntimeException("密码长度不能超过100个字符");
        }
        
        if (user.getUserName().length() > 50) {
            throw new RuntimeException("用户姓名长度不能超过50个字符");
        }
        
        userMapper.insert(user);
        return user.getUserId();
    }

    @Override
    public void deleteById(Integer id) {
        userMapper.deleteByPrimaryKey(id);
    }

    // ... 其他方法保持不变 ...
}
```

---

## 六、修复验证计划

### 6.1 单元测试

新建测试文件：`src/test/java/edu/neu/hoso/controller/UserControllerTest.java`

```java
package edu.neu.hoso.controller;

import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void testInsertWithNullUser() {
        ResultDTO<User> result = userController.insert(null);
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMsg().contains("不能为空"));
    }

    @Test
    public void testInsertWithEmptyLoginname() {
        User user = new User();
        user.setUserPassword("123456");
        user.setRoleId(1);
        user.setUserName("测试");
        user.setDepartmentId(1);
        
        ResultDTO<User> result = userController.insert(user);
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMsg().contains("登录名"));
    }

    @Test
    public void testDeleteWithNullId() {
        ResultDTO<User> result = userController.delete(null);
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMsg().contains("ID不能为空"));
    }

    @Test
    public void testDeleteWithNegativeId() {
        ResultDTO<User> result = userController.delete(-1);
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMsg().contains("大于0"));
    }

    @Test
    public void testDeleteWithNonExistentId() {
        ResultDTO<User> result = userController.delete(999999);
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMsg().contains("不存在"));
    }
}
```

### 6.2 集成测试

使用Postman或JMeter进行接口测试，验证：
1. 未登录访问用户接口返回401
2. 登录后正常访问用户接口
3. 新增用户参数校验正确
4. 删除用户存在性校验正确

---

## 七、修复时间计划

| 阶段 | 修复内容 | 预计工时 | 完成时间 |
|------|----------|----------|----------|
| 第1阶段 | 权限校验、重复校验、XSS过滤、参数校验 | 2天 | Day 1-2 |
| 第2阶段 | 登录参数校验、删除存在性校验 | 1天 | Day 3 |
| 第3阶段 | 全局异常处理 | 0.5天 | Day 4 |
| 测试验证 | 单元测试、集成测试 | 1天 | Day 4-5 |
| **合计** | | **4.5天** | **Day 1-5** |

---

## 八、注意事项

1. **备份代码**: 修复前请备份原有代码
2. **数据库兼容性**: 确保数据库字段长度与校验一致
3. **前端适配**: 前端需要根据新的错误提示做相应调整
4. **回归测试**: 修复后需进行全面的回归测试
5. **文档更新**: 更新接口文档中的错误码说明

---

**文档编制**: 自动化测试系统  
**审核日期**: 2026-03-26  
**版本**: V1.0
