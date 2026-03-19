package edu.neu.hoso.dto;

import edu.neu.hoso.model.User;

public class UserRequestDTO {
    // 操作员用户名
    private String username;
    // 操作员密码
    private String password;
    // 要操作的用户信息（用于insert）
    private User user;
    // 要删除的用户ID（用于delete）
    private Integer userId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
