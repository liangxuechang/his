package edu.neu.hoso.dto;

import edu.neu.hoso.model.User;

public class UserAuthDTO {
    private boolean authenticated;
    private User user;
    private String message;

    public UserAuthDTO() {
    }

    public UserAuthDTO(boolean authenticated, User user, String message) {
        this.authenticated = authenticated;
        this.user = user;
        this.message = message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
