package edu.neu.hoso.controller;

import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @Before
    public void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserLoginname("testuser");
        testUser.setUserPassword("123456");
        testUser.setRoleId(3);
        testUser.setUserName("测试用户");
        testUser.setDepartmentId(1);
        testUser.setUserTitleId(9);
        testUser.setUserGender("男");
        testUser.setUserStatus("ACTIVE");
        testUser.setUserSchedulingLimitcount(50);
    }

    @Test
    public void testInsert_Success() {
        when(userService.insert(any(User.class))).thenReturn(1);

        ResultDTO<User> result = userController.insert(testUser);

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertEquals("插入用户成功！", result.getMsg());
        assertNotNull(result.getData());
        verify(userService, times(1)).insert(any(User.class));
    }

    @Test
    public void testInsert_Exception() {
        when(userService.insert(any(User.class))).thenThrow(new RuntimeException("数据库错误"));

        ResultDTO<User> result = userController.insert(testUser);

        assertNotNull(result);
        assertEquals("ERROR", result.getStatus());
        assertEquals("插入用户失败！", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    public void testDelete_Success() {
        doNothing().when(userService).deleteById(1);

        ResultDTO<User> result = userController.delete(1);

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertEquals("删除用户成功！", result.getMsg());
        verify(userService, times(1)).deleteById(1);
    }

    @Test
    public void testDelete_Exception() {
        doThrow(new RuntimeException("数据库错误")).when(userService).deleteById(anyInt());

        ResultDTO<User> result = userController.delete(1);

        assertNotNull(result);
        assertEquals("ERROR", result.getStatus());
        assertEquals("删除用户失败！", result.getMsg());
    }

    @Test
    public void testDelete_NullId() {
        ResultDTO<User> result = userController.delete(null);

        assertNotNull(result);
    }

    @Test
    public void testGetUserById_Success() {
        when(userService.getUserById(1)).thenReturn(testUser);

        ResultDTO<User> result = userController.getUserById(1);

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertEquals("查询用户成功！", result.getMsg());
        assertNotNull(result.getData());
        assertEquals("testuser", result.getData().getUserLoginname());
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userService.getUserById(999)).thenReturn(null);

        ResultDTO<User> result = userController.getUserById(999);

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertNull(result.getData());
    }

    @Test
    public void testGetAllUser_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userService.getAllUser()).thenReturn(users);

        ResultDTO<User> result = userController.getAllUser();

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertNotNull(result.getData());
    }

    @Test
    public void testUpdate_Success() {
        doNothing().when(userService).update(any(User.class));

        ResultDTO<User> result = userController.update(testUser);

        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertEquals("更新用户成功！", result.getMsg());
    }

    @Test
    public void testUpdate_Exception() {
        doThrow(new RuntimeException("数据库错误")).when(userService).update(any(User.class));

        ResultDTO<User> result = userController.update(testUser);

        assertNotNull(result);
        assertEquals("ERROR", result.getStatus());
        assertEquals("更新用户失败！", result.getMsg());
    }
}
