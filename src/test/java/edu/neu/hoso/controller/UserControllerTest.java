package edu.neu.hoso.controller;

import com.alibaba.fastjson.JSON;
import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private static Integer testUserId;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    // ==================== 登录接口测试 ====================

    @Test
    public void testLoginSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/LoginUser")
                .param("userLoginName", "admin")
                .param("password", "123456"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("登录成功测试结果: " + content);
    }

    @Test
    public void testLoginWrongUsername() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/LoginUser")
                .param("userLoginName", "nonexistuser")
                .param("password", "123456"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("用户名错误测试结果: " + content);
    }

    @Test
    public void testLoginWrongPassword() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/LoginUser")
                .param("userLoginName", "admin")
                .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("密码错误测试结果: " + content);
    }

    // ==================== 新增用户接口测试 ====================

    @Test
    public void testInsertUserSuccess() throws Exception {
        User user = new User();
        user.setUserLoginname("test_" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(1);
        user.setUserName("测试用户");
        user.setDepartmentId(1);
        user.setUserGender("男");

        MvcResult result = mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("新增用户成功测试结果: " + content);

        // 解析返回的用户ID
        ResultDTO resultDTO = JSON.parseObject(content, ResultDTO.class);
        if ("OK".equals(resultDTO.getStatus()) && resultDTO.getData() != null) {
            User returnedUser = JSON.parseObject(JSON.toJSONString(resultDTO.getData()), User.class);
            testUserId = returnedUser.getUserId();
            System.out.println("测试用户ID: " + testUserId);
        }
    }

    @Test
    public void testInsertUserMissingLoginname() throws Exception {
        User user = new User();
        // userLoginname为空
        user.setUserPassword("123456");
        user.setRoleId(1);
        user.setUserName("测试用户");

        MvcResult result = mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("新增用户-登录名为空测试结果: " + content);
    }

    @Test
    public void testInsertUserMissingPassword() throws Exception {
        User user = new User();
        user.setUserLoginname("test_" + System.currentTimeMillis());
        // userPassword为空
        user.setRoleId(1);
        user.setUserName("测试用户");

        MvcResult result = mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("新增用户-密码为空测试结果: " + content);
    }

    @Test
    public void testInsertUserMissingRoleId() throws Exception {
        User user = new User();
        user.setUserLoginname("test_" + System.currentTimeMillis());
        user.setUserPassword("123456");
        // roleId为空
        user.setUserName("测试用户");

        MvcResult result = mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("新增用户-角色ID为空测试结果: " + content);
    }

    @Test
    public void testInsertUserEmptyJson() throws Exception {
        MvcResult result = mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("新增用户-空JSON测试结果: " + content);
    }

    // ==================== 删除用户接口测试 ====================

    @Test
    public void testDeleteUserSuccess() throws Exception {
        // 先创建一个用户用于删除
        User user = new User();
        user.setUserLoginname("del_test_" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(1);
        user.setUserName("待删除用户");

        MvcResult insertResult = mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String insertContent = insertResult.getResponse().getContentAsString();
        ResultDTO resultDTO = JSON.parseObject(insertContent, ResultDTO.class);
        User returnedUser = JSON.parseObject(JSON.toJSONString(resultDTO.getData()), User.class);
        Integer userIdToDelete = returnedUser.getUserId();

        // 删除该用户
        MvcResult deleteResult = mockMvc.perform(get("/user/delete")
                .param("id", userIdToDelete.toString()))
                .andExpect(status().isOk())
                .andReturn();

        String deleteContent = deleteResult.getResponse().getContentAsString();
        System.out.println("删除用户成功测试结果: " + deleteContent);
    }

    @Test
    public void testDeleteUserNotExist() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/delete")
                .param("id", "999999"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("删除不存在用户测试结果: " + content);
    }

    @Test
    public void testDeleteUserEmptyId() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/delete"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("删除用户-ID为空测试结果: " + content);
    }

    @Test
    public void testDeleteUserInvalidId() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/delete")
                .param("id", "-1"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("删除用户-无效ID测试结果: " + content);
    }

    // ==================== 其他辅助测试 ====================

    @Test
    public void testGetAllUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/getAllUser"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("查询所有用户测试结果: " + content.substring(0, Math.min(content.length(), 500)));
    }
}
