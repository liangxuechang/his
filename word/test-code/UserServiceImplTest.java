package edu.neu.hoso.service.impl;

import edu.neu.hoso.example.UserExample;
import edu.neu.hoso.model.User;
import edu.neu.hoso.model.UserMapper;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

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
        when(userMapper.insert(any(User.class))).thenReturn(1);

        Integer result = userService.insert(testUser);

        assertNotNull(result);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    public void testInsert_WithNullUser() {
        when(userMapper.insert(null)).thenReturn(0);

        Integer result = userService.insert(null);

        assertNull(result);
    }

    @Test
    public void testDeleteById_Success() {
        doNothing().when(userMapper).deleteByPrimaryKey(anyInt());

        userService.deleteById(1);

        verify(userMapper, times(1)).deleteByPrimaryKey(1);
    }

    @Test
    public void testDeleteById_WithNullId() {
        assertDoesNotThrow(() -> {
            userService.deleteById(null);
        });
    }

    @Test
    public void testDeleteByLoginname_Success() {
        doNothing().when(userMapper).deleteByExample(any(UserExample.class));

        userService.deleteByLoginname("testuser");

        verify(userMapper, times(1)).deleteByExample(any(UserExample.class));
    }

    @Test
    public void testDeleteByUserName_Success() {
        doNothing().when(userMapper).deleteByExample(any(UserExample.class));

        userService.deleteByUserName("测试用户");

        verify(userMapper, times(1)).deleteByExample(any(UserExample.class));
    }

    @Test
    public void testUpdate_Success() {
        doNothing().when(userMapper).updateByPrimaryKeySelective(any(User.class));

        userService.update(testUser);

        verify(userMapper, times(1)).updateByPrimaryKeySelective(any(User.class));
    }

    @Test
    public void testGetUserById_Success() {
        when(userMapper.selectByPrimaryKey(1)).thenReturn(testUser);

        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUserLoginname());
        assertEquals("测试用户", result.getUserName());
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userMapper.selectByPrimaryKey(999)).thenReturn(null);

        User result = userService.getUserById(999);

        assertNull(result);
    }

    @Test
    public void testGetUserByLoginname_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(users);

        List<User> result = userService.getUserByLoginname("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUserLoginname());
    }

    @Test
    public void testGetUserByLoginname_NotFound() {
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(new ArrayList<>());

        List<User> result = userService.getUserByLoginname("notexist");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUserByUserName_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(users);

        List<User> result = userService.getUserByUserName("测试用户");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetUserByRoleId_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(users);

        List<User> result = userService.getUserByRoleId(3);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetUserByUserTitleId_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(users);

        List<User> result = userService.getUserByUserTitleId(9);

        assertNotNull(result);
    }

    @Test
    public void testGetUserByUserGender_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(users);

        List<User> result = userService.getUserByUserGender("男");

        assertNotNull(result);
    }

    @Test
    public void testGetAllUser_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        User user2 = new User();
        user2.setUserId(2);
        user2.setUserLoginname("testuser2");
        users.add(user2);
        
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(users);

        List<User> result = userService.getAllUser();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAllUser_Empty() {
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(new ArrayList<>());

        List<User> result = userService.getAllUser();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUserByDepartmentID_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(users);

        List<User> result = userService.getUserByDepartmentID(1);

        assertNotNull(result);
    }

    @Test
    public void testGetUserByRole_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userMapper.selectByExample(any(UserExample.class))).thenReturn(users);

        List<User> result = userService.getUserByRole(3);

        assertNotNull(result);
    }
}
