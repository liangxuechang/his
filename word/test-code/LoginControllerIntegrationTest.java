package edu.neu.hoso.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.hoso.dto.ResultDTO;
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
public class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLogin_Admin_Success() throws Exception {
        mockMvc.perform(post("/login/LoginUser")
                .param("userLoginName", "admin")
                .param("password", "admin123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("请求成功"))
                .andExpect(jsonPath("$.data.user.userLoginname").value("admin"))
                .andExpect(jsonPath("$.data.role.roleName").value("系统管理员"));
    }

    @Test
    public void testLogin_Doctor_Success() throws Exception {
        mockMvc.perform(post("/login/LoginUser")
                .param("userLoginName", "doctor001")
                .param("password", "123456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.user.userLoginname").value("doctor001"));
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        mockMvc.perform(post("/login/LoginUser")
                .param("userLoginName", "admin")
                .param("password", "wrongpassword"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));
    }

    @Test
    public void testLogin_UserNotFound() throws Exception {
        mockMvc.perform(post("/login/LoginUser")
                .param("userLoginName", "notexist")
                .param("password", "123456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));
    }

    @Test
    public void testLogin_EmptyUsername() throws Exception {
        mockMvc.perform(post("/login/LoginUser")
                .param("userLoginName", "")
                .param("password", "123456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Error"));
    }

    @Test
    public void testLogin_EmptyPassword() throws Exception {
        mockMvc.perform(post("/login/LoginUser")
                .param("userLoginName", "admin")
                .param("password", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Error"));
    }

    @Test
    public void testLogin_MissingParameters() throws Exception {
        mockMvc.perform(post("/login/LoginUser"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Error"));
    }

    @Test
    public void testLogin_SqlInjection() throws Exception {
        mockMvc.perform(post("/login/LoginUser")
                .param("userLoginName", "admin' OR '1'='1")
                .param("password", "123456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Error"));
    }

    @Test
    public void testLogin_XssAttack() throws Exception {
        mockMvc.perform(post("/login/LoginUser")
                .param("userLoginName", "<script>alert('xss')</script>")
                .param("password", "123456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Error"));
    }
}
