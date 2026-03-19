package edu.neu.hoso.service;

import edu.neu.hoso.dto.UserAuthDTO;
import edu.neu.hoso.model.Role;
import edu.neu.hoso.model.User;

import java.util.List;

/**
 * @title: UserService
 * @package edu.neu.hoso.service
 * @description: 用户类业务接口
 * @author: Mike
 * @date: 2019-06-11 11:07
 * @version: V1.0
*/
public interface UserService {
    /**
     * @description: 根据用户名和密码验证用户
     * @param username 用户名(userLoginname)
     * @param password 密码(明文)
     * @return 验证结果，包含是否验证成功和用户信息
     */
    UserAuthDTO authenticateUser(String username, String password);

    /**
     * @description: 检查用户是否为管理员角色(function_id=2)
     * @param user 用户信息
     * @return 是否为管理员
     */
    boolean isAdmin(User user);

    /**
     * @description: 插入用户，密码使用bcrypt加密
     * @param user 用户信息
     * @param operatorUsername 操作员用户名
     * @param operatorPassword 操作员密码
     * @return 用户ID
     */
    Integer insertWithAuth(User user, String operatorUsername, String operatorPassword);

    /**
     * @description: 删除用户，需要管理员权限
     * @param id 要删除的用户ID
     * @param operatorUsername 操作员用户名
     * @param operatorPassword 操作员密码
     */
    void deleteByIdWithAuth(Integer id, String operatorUsername, String operatorPassword);

    Integer insert(User user);
    void deleteById(Integer id);
    void deleteByLoginname(String loginname);
    void deleteByUserName(String userName);
    void update(User user);
    User getUserById(Integer id);
    List<User> getUserByLoginname(String loginname);
    List<User> getUserByUserName(String userName);
    List<User> getUserByRoleId(Integer roleId);
    List<User> getUserByUserTitleId(Integer userTitleId);
    List<User> getUserByUserGender(String userGender);
    List<User> getAllUser();
    List<User> getUserByDepartmentID(int departmentID);
    List<User> getUserByRole(Integer roleId);
    List<User> getAllUserWithRole();
    List<Role> getAllRole();
}
