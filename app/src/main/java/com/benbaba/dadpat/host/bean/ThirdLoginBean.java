package com.benbaba.dadpat.host.bean;

public class ThirdLoginBean {

    private String token;

    private User user;

    @Override
    public String toString() {
        return "ThirdLoginBean{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
