package edu.neu.hoso.controller;

import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.Department;
import edu.neu.hoso.model.Role;
import edu.neu.hoso.model.User;
import edu.neu.hoso.service.DepartmentService;
import edu.neu.hoso.service.RoleService;
import edu.neu.hoso.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private LoginController loginController;

    private User testUser;
    private Department testDepartment;
    private Role testRole;

    @Before
    public void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserLoginname("admin");
        testUser.setUserPassword("admin123");
        testUser.setRoleId(1);
        testUser.setUserName("系统管理员");
        testUser.setDepartmentId(1);

        testDepartment = new Department();
        testDepartment.setDepartmentId(1);
        testDepartment.setDepartmentName("内科门诊");

        testRole = new Role();
        testRole.setRoleId(1);
        testRole.setRoleName("系统管理员");
    }

    @Test
    public void testLogin_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        when(userService.getUserByLoginname("admin")).thenReturn(users);
        when(departmentService.getDepartmentById(anyInt())).thenReturn(testDepartment);
        when(roleService.findRoleByID(anyInt())).thenReturn(testRole);

        ResultDTO result = loginController.findUser("admin", "admin123");

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertEquals("请求成功", result.getMsg());
        assertNotNull(result.getData());
    }

    @Test
    public void testLogin_UserNotFound() {
        List<User> users = new ArrayList<>();
        when(userService.getUserByLoginname("notexist")).thenReturn(users);

        ResultDTO result = loginController.findUser("notexist", "password");

        assertNotNull(result);
        assertEquals("Error", result.getStatus());
        assertEquals("用户名或密码错误", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testLogin_WrongPassword() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        when(userService.getUserByLoginname("admin")).thenReturn(users);

        ResultDTO result = loginController.findUser("admin", "wrongpassword");

        assertNotNull(result);
        assertEquals("Error", result.getStatus());
        assertEquals("用户名或密码错误", result.getMsg());
    }

    @Test
    public void testLogin_EmptyUsername() {
        List<User> users = new ArrayList<>();
        when(userService.getUserByLoginname("")).thenReturn(users);

        ResultDTO result = loginController.findUser("", "password");

        assertNotNull(result);
        assertEquals("Error", result.getStatus());
        assertEquals("用户名或密码错误", result.getMsg());
    }

    @Test
    public void testLogin_EmptyPassword() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        when(userService.getUserByLoginname("admin")).thenReturn(users);

        ResultDTO result = loginController.findUser("admin", "");

        assertNotNull(result);
        assertEquals("Error", result.getStatus());
        assertEquals("用户名或密码错误", result.getMsg());
    }

    @Test
    public void testLogin_NullUsername() {
        List<User> users = new ArrayList<>();
        when(userService.getUserByLoginname(null)).thenReturn(users);

        ResultDTO result = loginController.findUser(null, "password");

        assertNotNull(result);
        assertEquals("Error", result.getStatus());
    }

    @Test
    public void testLogin_NullPassword() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        when(userService.getUserByLoginname("admin")).thenReturn(users);

        ResultDTO result = loginController.findUser("admin", null);

        assertNotNull(result);
        assertEquals("Error", result.getStatus());
    }

    @Test
    public void testLogin_MultipleUsersSameName() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUserLoginname("testuser");
        user1.setUserPassword("password1");
        
        User user2 = new User();
        user2.setUserLoginname("testuser");
        user2.setUserPassword("password2");
        
        users.add(user1);
        users.add(user2);

        when(userService.getUserByLoginname("testuser")).thenReturn(users);

        ResultDTO result = loginController.findUser("testuser", "password2");

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
    }

    @Test
    public void testLogin_DoctorUser() {
        User doctorUser = new User();
        doctorUser.setUserId(2);
        doctorUser.setUserLoginname("doctor001");
        doctorUser.setUserPassword("123456");
        doctorUser.setRoleId(3);
        doctorUser.setUserName("张医生");
        doctorUser.setDepartmentId(1);

        List<User> users = new ArrayList<>();
        users.add(doctorUser);

        when(userService.getUserByLoginname("doctor001")).thenReturn(users);
        when(departmentService.getDepartmentById(anyInt())).thenReturn(testDepartment);
        when(roleService.findRoleByID(anyInt())).thenReturn(testRole);

        ResultDTO result = loginController.findUser("doctor001", "123456");

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
    }
}
