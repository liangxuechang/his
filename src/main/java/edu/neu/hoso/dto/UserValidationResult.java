package edu.neu.hoso.dto;

import edu.neu.hoso.model.User;

public class UserValidationResult {
    private boolean valid;
    private User user;
    private String message;

    public UserValidationResult(boolean valid, User user, String message) {
        this.valid = valid;
        this.user = user;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
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
