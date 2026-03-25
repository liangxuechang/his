# Bug修复方案文档

## 一、修复范围与优先级

### 1.1 待修复Bug列表

| Bug ID | 严重程度 | 优先级 | 修复估计(人天) | 所属模块 |
|--------|----------|--------|----------------|----------|
| BUG-001 | 严重 | P0 | 1 | 登录接口 |
| BUG-004 | 严重 | P0 | 1 | 新增用户接口 |
| BUG-008 | 严重 | P0 | 0.5 | 删除用户接口 |
| BUG-013 | 严重 | P0 | 0.5 | 新增用户接口 |
| BUG-017 | 严重 | P0 | 2 | 删除用户接口 |
| BUG-018 | 严重 | P0 | 2 | 新增用户接口 |
| BUG-002 | 一般 | P1 | 0.5 | 登录接口 |
| BUG-003 | 一般 | P1 | 0.5 | 登录接口 |
| BUG-005 | 一般 | P1 | 1 | 新增用户接口 |
| BUG-006 | 一般 | P1 | 1 | 新增用户接口 |
| BUG-007 | 一般 | P1 | 0.5 | 新增用户接口 |
| BUG-009 | 一般 | P1 | 0.5 | 删除用户接口 |
| BUG-010 | 一般 | P1 | 0.5 | 删除用户接口 |
| BUG-014 | 一般 | P1 | 1 | 通用 |
| BUG-011 | 一般 | P2 | 0.5 | 登录接口 |
| BUG-012 | 一般 | P2 | 0.5 | 新增用户接口 |
| BUG-019 | 一般 | P2 | 2 | 登录接口 |
| BUG-015 | 提示 | P3 | 0.5 | 新增用户接口 |
| BUG-016 | 提示 | P3 | 0.5 | 新增用户接口 |
| BUG-020 | 提示 | P3 | 0.5 | 通用 |
| BUG-021 | 提示 | P3 | 0.5 | 登录接口 |

### 1.2 优先级定义

- **P0**：阻塞性缺陷，必须立即修复
- **P1**：重要缺陷，本版本内必须修复
- **P2**：一般缺陷，可在后续版本修复
- **P3**：轻微缺陷，可根据排期选择性修复

---

## 二、具体修复方案

### 修复方案1：密码加密存储与传输（BUG-001、BUG-013）

**问题描述**：密码明文传输和存储

**修复方案**：

1. **添加密码加密工具类**：
```java
// 新增工具类：edu.neu.hoso.utils.PasswordEncoder
public class PasswordEncoder {
    // 使用BCrypt强哈希算法
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

2. **修改登录接口密码验证逻辑** (`LoginController.java:37-49`)：
```java
// 原逻辑（明文比对）:
if(user.getUserPassword().equals(password)) { ... }

// 修改为（密文比对）:
if(PasswordEncoder.matches(password, user.getUserPassword())) { ... }
```

3. **修改新增用户接口密码存储** (`UserServiceImpl.java:32-44`)：
```java
// 插入前加密密码
user.setUserPassword(PasswordEncoder.encode(user.getUserPassword()));
userMapper.insert(user);
```

4. **依赖添加**（pom.xml）：
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
    <version>5.4.2</version>
</dependency>
```

**影响分析**：
- 需要批量更新现有用户的密码为加密格式
- 登录和新增用户逻辑需要回归测试
- ✅ 安全合规性达标

---

### 修复方案2：参数校验框架引入（多个BUG）

**覆盖Bug**：BUG-002、BUG-003、BUG-004、BUG-008、BUG-010、BUG-012

**修复方案**：使用Spring Validation进行统一参数校验

1. **添加依赖**（pom.xml）：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

2. **修改User实体类添加校验注解** (`User.java`)：
```java
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class User {
    private Integer userId;
    
    @NotBlank(message = "登录名不能为空")
    @Size(max = 50, message = "登录名长度不能超过50")
    private String userLoginname;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String userPassword;
    
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正整数")
    private Integer roleId;
    
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    @Positive(message = "科室ID必须为正整数")
    private Integer departmentId;
    
    @Positive(message = "排班限制数必须为正数")
    private Integer userSchedulingLimitcount;
    
    // ... 其他字段
}
```

3. **Controller层添加@Validated** (`UserController.java:19-42`)：
```java
@RequestMapping("/insert")
public ResultDTO<User> insert(@Validated @RequestBody User user, BindingResult result){
    ResultDTO resultDTO = new ResultDTO();
    // 参数校验
    if (result.hasErrors()) {
        String msg = result.getFieldError().getDefaultMessage();
        resultDTO.setStatus("ERROR");
        resultDTO.setMsg(msg);
        return resultDTO;
    }
    try {
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
```

4. **删除接口参数校验** (`UserController.java:44-66`)：
```java
@RequestMapping("/delete")
public ResultDTO<User> delete(@RequestParam @NotNull(message = "用户ID不能为空") 
                              @Positive(message = "用户ID必须为正整数") Integer id){
    ResultDTO resultDTO = new ResultDTO();
    try {
        userService.deleteById(id);
        resultDTO.setStatus("OK");
        resultDTO.setMsg("删除用户成功！");
    } catch (Exception e) {
        e.printStackTrace();
        resultDTO.setStatus("ERROR");
        resultDTO.setMsg("删除用户失败！");
    }
    return resultDTO;
}
```

**代码质量提升**：
- 统一参数校验规范
- 错误信息清晰明确
- 减少重复校验代码

---

### 修复方案3：登录名校验（BUG-005）

**问题描述**：新增用户时未校验登录名唯一性

**修复方案**：

1. **UserService添加方法**：
```java
public interface UserService {
    // 新增方法：检查登录名是否已存在
    boolean isLoginnameExists(String loginname);
}
```

2. **UserServiceImpl实现**：
```java
@Override
public boolean isLoginnameExists(String loginname) {
    UserExample userExample = new UserExample();
    UserExample.Criteria criteria = userExample.createCriteria();
    criteria.andUserLoginnameEqualTo(loginname);
    return userMapper.countByExample(userExample) > 0;
}

@Override
public Integer insert(User user) {
    // 新增：插入前校验唯一性
    if (isLoginnameExists(user.getUserLoginname())) {
        throw new IllegalArgumentException("登录名已存在：" + user.getUserLoginname());
    }
    user.setUserPassword(PasswordEncoder.encode(user.getUserPassword()));
    userMapper.insert(user);
    return user.getUserId();
}
```

3. **Controller捕获异常**：
```java
catch (IllegalArgumentException e) {
    resultDTO.setStatus("ERROR");
    resultDTO.setMsg(e.getMessage());
}
```

---

### 修复方案4：删除接口返回正确性（BUG-009）

**问题描述**：删除不存在的用户也返回成功

**修复方案** (`UserServiceImpl.java:46-58`)：
```java
@Override
public void deleteById(Integer id) {
    // 先查询用户是否存在
    User user = userMapper.selectByPrimaryKey(id);
    if (user == null) {
        throw new IllegalArgumentException("用户不存在，ID：" + id);
    }
    userMapper.deleteByPrimaryKey(id);
}
```

**Controller修改**：
```java
@RequestMapping("/delete")
public ResultDTO<User> delete(@RequestParam @NotNull(message = "用户ID不能为空") 
                              @Positive(message = "用户ID必须为正整数") Integer id){
    ResultDTO resultDTO = new ResultDTO();
    try {
        userService.deleteById(id);
        resultDTO.setStatus("OK");
        resultDTO.setMsg("删除用户成功！");
    } catch (IllegalArgumentException e) {
        resultDTO.setStatus("ERROR");
        resultDTO.setMsg(e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        resultDTO.setStatus("ERROR");
        resultDTO.setMsg("删除用户失败！");
    }
    return resultDTO;
}
```

---

### 修复方案5：权限控制框架引入（BUG-017、BUG-018）

**问题描述**：接口无权限控制，任何人都可操作

**修复方案**：使用Spring Security + JWT实现认证授权

1. **添加依赖**：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.2</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.2</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.2</version>
    <scope>runtime</scope>
</dependency>
```

2. **JWT工具类**：
```java
@Component
public class JwtTokenUtil {
    @Value("${jwt.secret:your-secret-key}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private Long expiration;
    
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roleId", user.getRoleId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUserLoginname())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // 其他方法：解析token、验证token等
}
```

3. **Security配置类**：
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/login/**").permitAll()  // 登录接口放行
            .antMatchers("/user/insert", "/user/delete").hasRole("ADMIN")  // 需要管理员权限
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

4. **登录接口返回Token** (`LoginController.java`)：
```java
if(PasswordEncoder.matches(password, user.getUserPassword())){
    Department department=departmentService.getDepartmentById(user.getDepartmentId());
    Role role = roleService.findRoleByID(user.getRoleId());
    String token = jwtTokenUtil.generateToken(user);  // 生成token
    LoginResult loginResult = new LoginResult(user,department,role,token);
    result.setData(loginResult);
    result.setStatus("OK");
    result.setMsg("请求成功");
    return result;
}
```

---

### 修复方案6：统一异常处理（BUG-014）

**问题描述**：异常处理不统一，错误信息不明确

**修复方案**：使用`@ControllerAdvice`全局异常处理

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultDTO handleValidationException(MethodArgumentNotValidException e) {
        ResultDTO result = new ResultDTO();
        result.setStatus("ERROR");
        String message = e.getBindingResult().getFieldError() != null ? 
                         e.getBindingResult().getFieldError().getDefaultMessage() : "参数校验失败";
        result.setMsg(message);
        return result;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResultDTO handleIllegalArgumentException(IllegalArgumentException e) {
        ResultDTO result = new ResultDTO();
        result.setStatus("ERROR");
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultDTO handleException(Exception e) {
        ResultDTO result = new ResultDTO();
        result.setStatus("ERROR");
        result.setMsg("系统内部错误：" + e.getMessage());
        // 可在此处记录错误日志
        return result;
    }
}
```

**效果**：Controller层可移除大量重复的try-catch代码

---

### 修复方案7：代码规范与其他小问题（BUG-015、BUG-016、BUG-020、BUG-021）

1. **状态码统一**（BUG-021）：
   - 所有接口返回状态统一为大写：`"OK"`、`"ERROR"`

2. **泛型正确使用**（BUG-020）：
```java
// 改为带泛型的声明
ResultDTO<User> resultDTO = new ResultDTO<>();
```

3. **枚举值约束**（BUG-015、BUG-016）：
```java
// 新增性别校验注解或在业务层校验
if (user.getUserGender() != null && 
    !Arrays.asList("男", "女", "未知").contains(user.getUserGender())) {
    throw new IllegalArgumentException("性别值非法：" + user.getUserGender());
}
```

---

## 三、修复计划与排期

### 第一阶段：紧急修复（P0缺陷，预计3人天）

| 任务 | 负责人 | 时间 | 依赖 |
|------|--------|------|------|
| 密码加密工具类开发与集成 | 开发A | 第1天 | - |
| 所有接口参数校验添加 | 开发B | 第1-2天 | - |
| 登录名校验唯一性 | 开发A | 第2天 | - |
| 删除接口正确性修复 | 开发B | 第2天下午 | - |
| 单元测试覆盖 | 测试A | 第3天 | 开发完成 |
| P0缺陷回归测试 | 测试A | 第3天下午 | - |

### 第二阶段：重要修复（P1缺陷，预计3人天）

| 任务 | 负责人 | 时间 |
|------|--------|------|
| Spring Security权限框架引入 | 架构师 | 第4-5天 |
| JWT认证实现 | 架构师 | 第5-6天 |
| 全局统一异常处理 | 开发A | 第4天 |
| 角色ID、科室ID合法性校验 | 开发B | 第4天 |
| P1缺陷回归测试 | 测试A | 第6天下午 |

### 第三阶段：优化与完善（P2-P3，预计4人天）

| 任务 | 负责人 | 时间 |
|------|--------|------|
| 登录错误信息优化（防止枚举）| 开发A | 第7天 |
| 登录逻辑性能优化（单次查询验证）| 开发B | 第7天 |
| 字段枚举值校验 | 开发A | 第8天 |
| 代码规范优化 | 开发B | 第8天下午 |
| 全量回归测试 | 测试A | 第9-10天 |

---

## 四、测试与验收标准

### 4.1 单元测试要求
- 新增代码单元测试覆盖率不低于80%
- 修改代码需补充对应的单元测试
- 异常场景测试覆盖不低于90%

### 4.2 接口测试要求
| 测试类型 | 要求 |
|----------|------|
| 功能测试 | 所有用例通过 |
| 参数校验测试 | 非法参数100%拦截并给出明确提示 |
| 异常场景测试 | 系统无500错误，返回友好提示 |
| 安全测试 | 密码密文、SQL注入防护、越权访问拦截 |

### 4.3 性能测试要求
- 修复后性能不低于修复前
- 加解密操作不导致响应时间增加超过20%
- 参数校验不影响原有TPS

### 4.4 代码Review要求
- 所有修复代码需经过至少一人Review
- 安全相关代码需经过安全专项Review
- 架构变更需经过架构师审批

---

## 五、风险与应对措施

| 风险 | 概率 | 影响 | 应对措施 |
|------|------|------|----------|
| Spring Security集成导致原有接口受影响 | 中 | 高 | 先在测试环境隔离验证，配置白名单逐步放开 |
| 密码加密后历史用户无法登录 | 高 | 高 | 1. 编写迁移脚本批量加密历史密码<br>2. 采用兼容模式支持新旧两种密码 |
| 参数校验导致已有调用方失败 | 中 | 高 | 1. 检查并通知所有调用方<br>2. 提供过渡版本支持宽松校验 |
| JWT Token超时导致用户体验下降 | 中 | 中 | 实现Token刷新机制 |

---

## 六、回滚方案

### 6.1 版本回滚触发条件
- 修复后核心功能（登录、新增/删除用户）不可用
- 性能下降超过50%
- 影响其他模块正常运行
- 产生P0级新缺陷

### 6.2 回滚步骤
1. **立即停止**新代码部署
2. **回滚代码**到上一个稳定版本
3. **清理数据库**变更（如加密字段、新增字段）
4. **验证核心功能**可用性
5. **排查失败原因**并修改方案

### 6.3 应急联系人
- 技术负责人：XXX
- 测试负责人：XXX
- 运维负责人：XXX

---

## 七、交付物清单

1. ✅ 修复后的源代码（含单元测试）
2. ✅ 数据库变更脚本（如密码加密更新脚本）
3. 部署文档（含Security配置说明）
4. ✅ 回归测试报告
5. 性能测试对比报告
6. 安全测试评估报告

---

## 八、后续改进建议

1. **代码质量门禁**：在CI/CD流程中加入代码质量检查（SonarQube）
2. **安全扫描**：定期进行安全漏洞扫描，特别是密码、权限相关
3. **性能监控**：生产环境配置APM监控，及时发现性能瓶颈
4. **测试左移**：开发阶段加强单元测试和接口测试，问题早发现早解决
5. **文档同步**：接口文档与代码同步维护，使用Swagger等自动化文档工具
