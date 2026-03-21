package edu.neu.hoso.controller;

import edu.neu.hoso.dto.ResultDTO;
import edu.neu.hoso.model.Role;
import edu.neu.hoso.model.User;
import edu.neu.hoso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {
    @Autowired
    UserService userService;

    private static final Integer ADMIN_FUNCTION_ID = 2;

    @RequestMapping("/insert")
    public ResultDTO<User> insert(String username, String password, @RequestBody User user){
        /**
         *@title: insert
         *@description: 插入用户（需要管理员权限验证）
         *@author: Mike
         *@date: 2019-06-19 11:04
         *@param: [username, password, user]
         *@return: edu.neu.hoso.dto.ResultDTO<edu.neu.hoso.model.User>
         *@throws:
         */
        ResultDTO resultDTO = new ResultDTO();
        try {
            ResultDTO<User> verifyResult = userService.verifyUser(username, password);
            if (!"OK".equals(verifyResult.getStatus())) {
                resultDTO.setStatus("ERROR");
                resultDTO.setMsg("用户验证失败：" + verifyResult.getMsg());
                return resultDTO;
            }
            
            User operator = verifyResult.getData();
            Role operatorRole = operator.getRole();
            if (operatorRole == null || !ADMIN_FUNCTION_ID.equals(operatorRole.getFunctionId())) {
                resultDTO.setStatus("ERROR");
                resultDTO.setMsg("权限不足：只有管理员才能添加用户");
                return resultDTO;
            }
            
            String encodedPassword = userService.encodePassword(user.getUserPassword());
            user.setUserPassword(encodedPassword);
            
            userService.insert(user);
            resultDTO.setData(user);
            resultDTO.setStatus("OK");
            resultDTO.setMsg("插入用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("插入用户失败！");
        }
        return resultDTO;
    }

    @RequestMapping("/delete")
    public ResultDTO<User> delete(String username, String password, Integer id){
        /**
         *@title: delete
         *@description: 删除用户 经id（需要管理员权限验证）
         *@author: Mike
         *@date: 2019-06-19 11:04
         *@param: [username, password, id]
         *@return: edu.neu.hoso.dto.ResultDTO<edu.neu.hoso.model.User>
         *@throws:
         */
        ResultDTO resultDTO = new ResultDTO();
        try {
            ResultDTO<User> verifyResult = userService.verifyUser(username, password);
            if (!"OK".equals(verifyResult.getStatus())) {
                resultDTO.setStatus("ERROR");
                resultDTO.setMsg("用户验证失败：" + verifyResult.getMsg());
                return resultDTO;
            }
            
            User operator = verifyResult.getData();
            Role operatorRole = operator.getRole();
            if (operatorRole == null || !ADMIN_FUNCTION_ID.equals(operatorRole.getFunctionId())) {
                resultDTO.setStatus("ERROR");
                resultDTO.setMsg("权限不足：只有管理员才能删除用户");
                return resultDTO;
            }
            
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

    @RequestMapping("/update")
    public ResultDTO<User> update(@RequestBody User user){
        /**
         *@title: update
         *@description: 更新用户
         *@author: Mike
         *@date: 2019-06-19 11:04
         *@param: [user]
         *@return: edu.neu.hoso.dto.ResultDTO<edu.neu.hoso.model.User>
         *@throws:
         */
        ResultDTO resultDTO = new ResultDTO();
        try {
            userService.update(user);
            resultDTO.setStatus("OK");
            resultDTO.setMsg("更新用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("更新用户失败！");
        }
        return resultDTO;
    }

    @RequestMapping("/getUserById")
    public ResultDTO<User> getUserById(Integer id){
        /**
         *@title: getUserById
         *@description: 查询用户 经id
         *@author: Mike
         *@date: 2019-06-19 11:04
         *@param: [id]
         *@return: edu.neu.hoso.dto.ResultDTO<edu.neu.hoso.model.User>
         *@throws:
         */
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO.setData(userService.getUserById(id));
            resultDTO.setStatus("OK");
            resultDTO.setMsg("查询用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("查询用户失败！");
        }
        return resultDTO;
    }

    @RequestMapping("/getUserByRole")
    public ResultDTO<User> getUserByRole(Integer roleId){

        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO.setData(userService.getUserByRole(roleId));
            resultDTO.setStatus("OK");
            resultDTO.setMsg("查询用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("查询用户失败！");
        }
        return resultDTO;
    }

    @RequestMapping("/getAllUser")
    public ResultDTO<User> getAllUser(){
        /**
         *@title: getAllUser
         *@description: 展示所有用户
         *@author: Mike
         *@date: 2019-06-19 11:03
         *@param: []
         *@return: edu.neu.hoso.dto.ResultDTO<edu.neu.hoso.model.User>
         *@throws:
         */
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO.setData(userService.getAllUser());
            resultDTO.setStatus("OK");
            resultDTO.setMsg("展示用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("展示用户失败！");
        }
        return resultDTO;
    }

    @RequestMapping("/getAllUserWithRole")
    public ResultDTO<User> getAllUserWithRole(){
        /**
         *@title: getAllUserWithRole
         *@description: 查询所有用户 附带role,department
         *@author: Mike
         *@date: 2019-06-30 23:36
         *@param: []
         *@return: edu.neu.hoso.dto.ResultDTO<edu.neu.hoso.model.User>
         *@throws:
         */
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO.setData(userService.getAllUserWithRole());
            resultDTO.setStatus("OK");
            resultDTO.setMsg("展示用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("展示用户失败！");
        }
        return resultDTO;
    }

    @RequestMapping("/getAllRole")
    public ResultDTO<User> getAllRole(){
        /**
         *@title: getAllRole
         *@description: 查询所有角色
         *@author: Mike
         *@date: 2019-06-30 23:42
         *@param: []
         *@return: edu.neu.hoso.dto.ResultDTO<edu.neu.hoso.model.User>
         *@throws:
         */
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO.setData(userService.getAllRole());
            resultDTO.setStatus("OK");
            resultDTO.setMsg("展示用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatus("ERROR");
            resultDTO.setMsg("展示用户失败！");
        }
        return resultDTO;
    }
}
