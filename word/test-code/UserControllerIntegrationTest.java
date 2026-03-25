package edu.neu.hoso.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.hoso.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @Before
    public void setUp() {
        testUser = new User();
        testUser.setUserLoginname("testuser_integration");
        testUser.setUserPassword("123456");
        testUser.setRoleId(3);
        testUser.setUserName("集成测试用户");
        testUser.setDepartmentId(1);
        testUser.setUserTitleId(9);
        testUser.setUserGender("男");
        testUser.setUserStatus("ACTIVE");
        testUser.setUserSchedulingLimitcount(50);
    }

    @Test
    public void testInsert_Success() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("插入用户成功！"))
                .andExpect(jsonPath("$.data.userLoginname").value("testuser_integration"))
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    public void testInsert_MinimalFields() throws Exception {
        User minimalUser = new User();
        minimalUser.setUserLoginname("minimal_user");
        minimalUser.setUserPassword("123456");
        minimalUser.setRoleId(3);
        minimalUser.setUserName("最小字段用户");

        String userJson = objectMapper.writeValueAsString(minimalUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    public void testInsert_EmptyLoginname() throws Exception {
        testUser.setUserLoginname("");
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    public void testInsert_EmptyPassword() throws Exception {
        testUser.setUserPassword("");
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    public void testInsert_NullRoleId() throws Exception {
        testUser.setRoleId(null);
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testInsert_EmptyBody() throws Exception {
        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testInsert_InvalidRoleId() throws Exception {
        testUser.setRoleId(999);
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testInsert_InvalidDepartmentId() throws Exception {
        testUser.setDepartmentId(999);
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testInsert_DuplicateLoginname() throws Exception {
        testUser.setUserLoginname("admin");
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testInsert_SqlInjection() throws Exception {
        testUser.setUserLoginname("test'; DROP TABLE user;--");
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testInsert_XssAttack() throws Exception {
        testUser.setUserName("<script>alert('xss')</script>");
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete_Success() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUser);

        MvcResult insertResult = mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andReturn();

        String response = insertResult.getResponse().getContentAsString();
        Integer userId = objectMapper.readTree(response).path("data").path("userId").asInt();

        mockMvc.perform(post("/user/delete")
                .param("id", userId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("删除用户成功！"));
    }

    @Test
    public void testDelete_ExistingUser() throws Exception {
        mockMvc.perform(post("/user/delete")
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    public void testDelete_NonExistingUser() throws Exception {
        mockMvc.perform(post("/user/delete")
                .param("id", "99999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    public void testDelete_NullId() throws Exception {
        mockMvc.perform(post("/user/delete"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete_ZeroId() throws Exception {
        mockMvc.perform(post("/user/delete")
                .param("id", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    public void testDelete_NegativeId() throws Exception {
        mockMvc.perform(post("/user/delete")
                .param("id", "-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    public void testDelete_InvalidId() throws Exception {
        mockMvc.perform(post("/user/delete")
                .param("id", "abc"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUserById_Success() throws Exception {
        mockMvc.perform(post("/user/getUserById")
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        mockMvc.perform(post("/user/getUserById")
                .param("id", "99999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testGetAllUser_Success() throws Exception {
        mockMvc.perform(post("/user/getAllUser"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }

    @Test
    public void testGetUserByRole_Success() throws Exception {
        mockMvc.perform(post("/user/getUserByRole")
                .param("roleId", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testGetAllUserWithRole_Success() throws Exception {
        mockMvc.perform(post("/user/getAllUserWithRole"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testGetAllRole_Success() throws Exception {
        mockMvc.perform(post("/user/getAllRole"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testUpdate_Success() throws Exception {
        User updateUser = new User();
        updateUser.setUserId(2);
        updateUser.setUserName("张医生（已更新）");

        String userJson = objectMapper.writeValueAsString(updateUser);

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("更新用户成功！"));
    }

    @Test
    public void testUpdate_NonExistingUser() throws Exception {
        User updateUser = new User();
        updateUser.setUserId(99999);
        updateUser.setUserName("不存在的用户");

        String userJson = objectMapper.writeValueAsString(updateUser);

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    public void testCrudWorkflow() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUser);

        MvcResult insertResult = mockMvc.perform(post("/user/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andReturn();

        String response = insertResult.getResponse().getContentAsString();
        Integer userId = objectMapper.readTree(response).path("data").path("userId").asInt();

        mockMvc.perform(post("/user/getUserById")
                .param("id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.userLoginname").value("testuser_integration"));

        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setUserName("更新后的用户名");

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        mockMvc.perform(post("/user/delete")
                .param("id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        mockMvc.perform(post("/user/getUserById")
                .param("id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
