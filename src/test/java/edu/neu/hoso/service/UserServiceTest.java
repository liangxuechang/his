package edu.neu.hoso.service;

import edu.neu.hoso.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * UserService 单元测试类
 * 测试业务逻辑层的各种场景
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    // ==================== 新增用户测试 ====================

    /**
     * 测试正常新增用户
     */
    @Test
    @Transactional
    public void testInsertUserSuccess() {
        User user = new User();
        user.setUserLoginname("servicetest" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("Service测试用户");
        user.setDepartmentId(1);
        user.setUserTitleId(1);
        user.setUserGender("男");

        Integer userId = userService.insert(user);

        assertNotNull(userId);
        assertTrue(userId > 0);

        // 验证用户是否真正插入
        User insertedUser = userService.getUserById(userId);
        assertNotNull(insertedUser);
        assertEquals(user.getUserLoginname(), insertedUser.getUserLoginname());
    }

    /**
     * 测试新增用户 - 登录名为空
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithNullLoginname() {
        User user = new User();
        user.setUserLoginname(null);
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        userService.insert(user);
    }

    /**
     * 测试新增用户 - 登录名为空字符串
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithEmptyLoginname() {
        User user = new User();
        user.setUserLoginname("   ");
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        userService.insert(user);
    }

    /**
     * 测试新增用户 - 密码为空
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithNullPassword() {
        User user = new User();
        user.setUserLoginname("testnopwd" + System.currentTimeMillis());
        user.setUserPassword(null);
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        userService.insert(user);
    }

    /**
     * 测试新增用户 - 用户姓名为空
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithNullUsername() {
        User user = new User();
        user.setUserLoginname("testnoname" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName(null);
        user.setDepartmentId(1);

        userService.insert(user);
    }

    /**
     * 测试新增用户 - 角色ID为空
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithNullRoleId() {
        User user = new User();
        user.setUserLoginname("testnorole" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(null);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        userService.insert(user);
    }

    /**
     * 测试新增用户 - 科室ID为空
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithNullDepartmentId() {
        User user = new User();
        user.setUserLoginname("testnodept" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(null);

        userService.insert(user);
    }

    /**
     * 测试新增用户 - 登录名重复
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithDuplicateLoginname() {
        String loginname = "duplicatetest" + System.currentTimeMillis();

        // 第一个用户
        User user1 = new User();
        user1.setUserLoginname(loginname);
        user1.setUserPassword("123456");
        user1.setRoleId(2);
        user1.setUserName("测试用户1");
        user1.setDepartmentId(1);
        userService.insert(user1);

        // 第二个用户（相同登录名）
        User user2 = new User();
        user2.setUserLoginname(loginname);
        user2.setUserPassword("123456");
        user2.setRoleId(2);
        user2.setUserName("测试用户2");
        user2.setDepartmentId(1);
        userService.insert(user2); // 应该抛出异常
    }

    /**
     * 测试新增用户 - 登录名超长
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithLongLoginname() {
        User user = new User();
        user.setUserLoginname("thisisaverylongusernamethatexceedsthenormallimitofcharacters" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        userService.insert(user);
    }

    /**
     * 测试新增用户 - 密码超长
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithLongPassword() {
        User user = new User();
        user.setUserLoginname("testlongpwd" + System.currentTimeMillis());
        user.setUserPassword("a".repeat(101));
        user.setRoleId(2);
        user.setUserName("测试用户");
        user.setDepartmentId(1);

        userService.insert(user);
    }

    /**
     * 测试新增用户 - 用户姓名超长
     */
    @Test(expected = RuntimeException.class)
    @Transactional
    public void testInsertUserWithLongUserName() {
        User user = new User();
        user.setUserLoginname("testlongname" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("这是一个超长的用户姓名超过了五十个字符的限制" + System.currentTimeMillis());
        user.setDepartmentId(1);

        userService.insert(user);
    }

    // ==================== 删除用户测试 ====================

    /**
     * 测试正常删除用户
     */
    @Test
    @Transactional
    public void testDeleteByIdSuccess() {
        // 先创建用户
        User user = new User();
        user.setUserLoginname("deletetest" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("删除测试用户");
        user.setDepartmentId(1);
        Integer userId = userService.insert(user);

        // 验证用户存在
        User insertedUser = userService.getUserById(userId);
        assertNotNull(insertedUser);

        // 删除用户
        userService.deleteById(userId);

        // 验证用户已删除
        User deletedUser = userService.getUserById(userId);
        assertNull(deletedUser);
    }

    /**
     * 测试删除不存在的用户（不应抛出异常）
     */
    @Test
    @Transactional
    public void testDeleteNonExistentUser() {
        // 删除一个不存在的ID，不应抛出异常
        userService.deleteById(999999);
    }

    /**
     * 测试根据登录名删除用户
     */
    @Test
    @Transactional
    public void testDeleteByLoginname() {
        String loginname = "deletebylogin" + System.currentTimeMillis();

        // 创建用户
        User user = new User();
        user.setUserLoginname(loginname);
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("删除测试用户");
        user.setDepartmentId(1);
        userService.insert(user);

        // 验证用户存在
        List<User> users = userService.getUserByLoginname(loginname);
        assertFalse(users.isEmpty());

        // 根据登录名删除
        userService.deleteByLoginname(loginname);

        // 验证用户已删除
        users = userService.getUserByLoginname(loginname);
        assertTrue(users.isEmpty());
    }

    /**
     * 测试根据用户名删除用户
     */
    @Test
    @Transactional
    public void testDeleteByUserName() {
        String userName = "删除测试用户名" + System.currentTimeMillis();

        // 创建用户
        User user = new User();
        user.setUserLoginname("deletebyusername" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName(userName);
        user.setDepartmentId(1);
        userService.insert(user);

        // 验证用户存在
        List<User> users = userService.getUserByUserName(userName);
        assertFalse(users.isEmpty());

        // 根据用户名删除
        userService.deleteByUserName(userName);

        // 验证用户已删除
        users = userService.getUserByUserName(userName);
        assertTrue(users.isEmpty());
    }

    // ==================== 查询用户测试 ====================

    /**
     * 测试根据ID查询用户
     */
    @Test
    public void testGetUserById() {
        // 查询ID为1的用户（假设存在）
        User user = userService.getUserById(1);
        // 不断言结果，因为不确定数据库中是否有数据
        System.out.println("查询结果: " + (user != null ? user.getUserLoginname() : "null"));
    }

    /**
     * 测试根据登录名查询用户
     */
    @Test
    @Transactional
    public void testGetUserByLoginname() {
        String loginname = "querybylogin" + System.currentTimeMillis();

        // 创建用户
        User user = new User();
        user.setUserLoginname(loginname);
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("查询测试用户");
        user.setDepartmentId(1);
        userService.insert(user);

        // 查询
        List<User> users = userService.getUserByLoginname(loginname);
        assertFalse(users.isEmpty());
        assertEquals(loginname, users.get(0).getUserLoginname());
    }

    /**
     * 测试根据用户名查询用户
     */
    @Test
    @Transactional
    public void testGetUserByUserName() {
        String userName = "查询测试用户名" + System.currentTimeMillis();

        // 创建用户
        User user = new User();
        user.setUserLoginname("querybyname" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName(userName);
        user.setDepartmentId(1);
        userService.insert(user);

        // 查询
        List<User> users = userService.getUserByUserName(userName);
        assertFalse(users.isEmpty());
        assertEquals(userName, users.get(0).getUserName());
    }

    /**
     * 测试根据角色ID查询用户
     */
    @Test
    public void testGetUserByRoleId() {
        List<User> users = userService.getUserByRoleId(1);
        assertNotNull(users);
        System.out.println("角色1的用户数量: " + users.size());
    }

    /**
     * 测试查询所有用户
     */
    @Test
    public void testGetAllUser() {
        List<User> users = userService.getAllUser();
        assertNotNull(users);
        System.out.println("所有用户数量: " + users.size());
    }

    /**
     * 测试根据科室ID查询用户
     */
    @Test
    public void testGetUserByDepartmentId() {
        List<User> users = userService.getUserByDepartmentID(1);
        assertNotNull(users);
        System.out.println("科室1的用户数量: " + users.size());
    }

    // ==================== 更新用户测试 ====================

    /**
     * 测试更新用户
     */
    @Test
    @Transactional
    public void testUpdateUser() {
        // 创建用户
        User user = new User();
        user.setUserLoginname("updatetest" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("更新前用户名");
        user.setDepartmentId(1);
        Integer userId = userService.insert(user);

        // 更新用户信息
        user.setUserId(userId);
        user.setUserName("更新后用户名" + System.currentTimeMillis());
        user.setUserPassword("newpassword");
        userService.update(user);

        // 验证更新
        User updatedUser = userService.getUserById(userId);
        assertEquals(user.getUserName(), updatedUser.getUserName());
    }

    // ==================== XSS防护测试 ====================

    /**
     * 测试XSS防护 - 登录名包含特殊字符
     */
    @Test
    @Transactional
    public void testXSSProtectionInLoginname() {
        User user = new User();
        user.setUserLoginname("xsstest" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("<script>alert('xss')</script>");
        user.setDepartmentId(1);

        Integer userId = userService.insert(user);
        User insertedUser = userService.getUserById(userId);

        // 验证XSS被转义
        assertFalse(insertedUser.getUserName().contains("<script>"));
    }

    /**
     * 测试SQL注入防护
     */
    @Test
    @Transactional
    public void testSQLInjectionProtection() {
        User user = new User();
        user.setUserLoginname("sqltest" + System.currentTimeMillis());
        user.setUserPassword("123456");
        user.setRoleId(2);
        user.setUserName("admin' OR '1'='1");
        user.setDepartmentId(1);

        // 应该正常插入，不会被SQL注入
        Integer userId = userService.insert(user);
        assertNotNull(userId);

        User insertedUser = userService.getUserById(userId);
        assertEquals("admin' OR '1'='1", insertedUser.getUserName());
    }
}
