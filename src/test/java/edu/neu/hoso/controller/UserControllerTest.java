package edu.neu.hoso.controller;

import com.alibaba.fastjson.JSON;
import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.User;
import edu.neu.hoso.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 单元测试和集成测试类
 * 包含功能测试、参数校验测试、异常场景测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private MockHttpSession session;

    @Before
    public void setUp() {
        // 模拟登录状态
        session = new MockHttpSession();
        User user = new User();
        user.setUserId(1);
        user.setUserLoginname("admin");
        user.setUserName("管理员");
        session.setAttribute("user", user);
    }

    // ==================== 登录接口测试 ====================

    /**
     * TC-LOGIN-001: 正常登录验证
     */
    @Test
    public void testLoginSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/LoginUser")
                .param("userLoginName", "admin")
                .param("password", "123456"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("OK", resultDTO.getStatus());
        assertEquals("请求成功", resultDTO.getMsg());
        assertNotNull(resultDTO.getData());
    }

    /**
     * TC-LOGIN-002: 错误密码登录验证
     */
    @Test
    public void testLoginWithWrongPassword() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/LoginUser")
                .param("userLoginName", "admin")
                .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("Error", resultDTO.getStatus());
        assertEquals("用户名或密码错误", resultDTO.getMsg());
    }

    /**
     * TC-LOGIN-003: 不存在的用户登录验证
     */
    @Test
    public void testLoginWithNonExistentUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/LoginUser")
                .param("userLoginName", "notexistuser" + System.currentTimeMillis())
                .param("password", "123456"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("Error", resultDTO.getStatus());
        assertEquals("用户名或密码错误", resultDTO.getMsg());
    }

    /**
     * TC-LOGIN-004: 空用户名登录验证
     */
    @Test
    public void testLoginWithEmptyUsername() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/LoginUser")
                .param("userLoginName", "")
                .param("password", "123456"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("Error", resultDTO.getStatus());
    }

    /**
     * TC-LOGIN-005: 空密码登录验证
     */
    @Test
    public void testLoginWithEmptyPassword() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/LoginUser")
                .param("userLoginName", "admin")
                .param("password", ""))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("Error", resultDTO.getStatus());
    }

    // ==================== 新增用户接口测试 ====================

    /**
     * TC-INSERT-001: 正常新增用户验证
     */
    @Test
    @Transactional
    public void testInsertUserSuccess() throws Exception {
        User user = new User();
        user.setUserLoginname("testuser" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);
        user.setUserTitleId(1);
        user.setUserGender("男");
        user.setUserStatus("1");
        user.setUserSchedulingLimitcount(10);

        MvcResult result = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("OK", resultDTO.getStatus());
        assertEquals("插入用户成功！", resultDTO.getMsg());
        assertNotNull(resultDTO.getData());
    }

    /**
     * TC-INSERT-002: 缺少登录名参数验证
     */
    @Test
    public void testInsertUserWithoutLoginname() throws Exception {
        User user = new User();
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        MvcResult result = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
    }

    /**
     * TC-INSERT-003: 缺少密码参数验证
     */
    @Test
    public void testInsertUserWithoutPassword() throws Exception {
        User user = new User();
        user.setUserLoginname("testuser" + System.currentTimeMillis());
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        MvcResult result = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
    }

    /**
     * TC-INSERT-004: 登录名重复验证
     */
    @Test
    @Transactional
    public void testInsertUserWithDuplicateLoginname() throws Exception {
        // 先创建一个用户
        User user1 = new User();
        String loginname = "duptest" + System.currentTimeMillis();
        user1.setUserLoginname(loginname);
        user1.setUserPassword("123456");
        user1.setRoleId(2);
        user1.setUserName("测试用户1");
        user1.setDepartmentId(1);

        mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user1)))
                .andExpect(status().isOk());

        // 再创建同名用户
        User user2 = new User();
        user2.setUserLoginname(loginname);
        user2.setUserPassword("123456");
        user2.setRoleId(2);
        user2.setUserName("测试用户2");
        user2.setDepartmentId(1);

        MvcResult result = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user2)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
        assertTrue(resultDTO.getMsg().contains("已存在"));
    }

    /**
     * TC-INSERT-005: 超长登录名校验
     */
    @Test
    public void testInsertUserWithLongLoginname() throws Exception {
        User user = new User();
        user.setUserLoginname("thisisaverylongusernamethatexceedsthenormallimitofcharacters" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        MvcResult result = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
    }

    /**
     * TC-INSERT-006: 特殊字符登录名校验（XSS测试）
     */
    @Test
    @Transactional
    public void testInsertUserWithXSS() throws Exception {
        User user = new User();
        user.setUserLoginname("testxss" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("<script>alert('xss')</script>");
        user.setDepartmentId(1);

        MvcResult result = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        // 应该成功插入，但XSS被转义
        assertEquals("OK", resultDTO.getStatus());
    }

    /**
     * TC-INSERT-007: 空对象请求验证
     */
    @Test
    public void testInsertUserWithEmptyObject() throws Exception {
        MvcResult result = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
    }

    /**
     * TC-INSERT-008: SQL注入安全测试
     */
    @Test
    @Transactional
    public void testInsertUserWithSQLInjection() throws Exception {
        User user = new User();
        user.setUserLoginname("testsql" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("admin' OR '1'='1");
        user.setDepartmentId(1);

        MvcResult result = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        // MyBatis参数绑定应防止SQL注入
        assertEquals("OK", resultDTO.getStatus());
    }

    // ==================== 删除用户接口测试 ====================

    /**
     * TC-DELETE-001: 正常删除用户验证
     */
    @Test
    @Transactional
    public void testDeleteUserSuccess() throws Exception {
        // 先创建一个用户
        User user = new User();
        user.setUserLoginname("deletetest" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("删除测试用户");
        user.setDepartmentId(1);

        MvcResult insertResult = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String insertContent = insertResult.getResponse().getContentAsString();
        ResultDTO insertDTO = JSON.parseObject(insertContent, ResultDTO.class);
        User insertedUser = JSON.parseObject(JSON.toJSONString(insertDTO.getData()), User.class);

        // 删除该用户
        MvcResult result = mockMvc.perform(get("/user/delete")
                .session(session)
                .param("id", String.valueOf(insertedUser.getUserId())))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("OK", resultDTO.getStatus());
        assertEquals("删除用户成功！", resultDTO.getMsg());
    }

    /**
     * TC-DELETE-002: 删除不存在用户验证
     */
    @Test
    public void testDeleteNonExistentUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/delete")
                .session(session)
                .param("id", "999999"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
        assertTrue(resultDTO.getMsg().contains("不存在"));
    }

    /**
     * TC-DELETE-003: 空ID参数验证
     */
    @Test
    public void testDeleteWithNullId() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/delete")
                .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
    }

    /**
     * TC-DELETE-004: 负数ID参数验证
     */
    @Test
    public void testDeleteWithNegativeId() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/delete")
                .session(session)
                .param("id", "-1"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
    }

    /**
     * TC-DELETE-005: 字符串ID参数验证
     */
    @Test
    public void testDeleteWithStringId() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/delete")
                .session(session)
                .param("id", "abc"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * TC-DELETE-006: 重复删除同一用户验证
     */
    @Test
    @Transactional
    public void testDeleteSameUserTwice() throws Exception {
        // 先创建一个用户
        User user = new User();
        user.setUserLoginname("deletetest2" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("删除测试用户2");
        user.setDepartmentId(1);

        MvcResult insertResult = mockMvc.perform(post("/user/insert")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String insertContent = insertResult.getResponse().getContentAsString();
        ResultDTO insertDTO = JSON.parseObject(insertContent, ResultDTO.class);
        User insertedUser = JSON.parseObject(JSON.toJSONString(insertDTO.getData()), User.class);

        // 第一次删除
        mockMvc.perform(get("/user/delete")
                .session(session)
                .param("id", String.valueOf(insertedUser.getUserId())))
                .andExpect(status().isOk());

        // 第二次删除
        MvcResult result = mockMvc.perform(get("/user/delete")
                .session(session)
                .param("id", String.valueOf(insertedUser.getUserId())))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("ERROR", resultDTO.getStatus());
        assertTrue(resultDTO.getMsg().contains("不存在"));
    }

    /**
     * TC-DELETE-007: 未登录删除用户验证
     */
    @Test
    public void testDeleteWithoutLogin() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/delete")
                .param("id", "1"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    // ==================== 其他接口测试 ====================

    /**
     * 测试查询所有用户
     */
    @Test
    public void testGetAllUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/getAllUser")
                .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("OK", resultDTO.getStatus());
        assertNotNull(resultDTO.getData());
    }

    /**
     * 测试根据ID查询用户
     */
    @Test
    public void testGetUserById() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/getUserById")
                .session(session)
                .param("id", "1"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("OK", resultDTO.getStatus());
        assertNotNull(resultDTO.getData());
    }

    /**
     * 测试更新用户
     */
    @Test
    @Transactional
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setUserId(1);
        user.setUserName("更新后的姓名" + System.currentTimeMillis());

        MvcResult result = mockMvc.perform(post("/user/update")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);

        assertEquals("OK", resultDTO.getStatus());
    }
}
