package com.benbaba.dadpat.host.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * user对象
 * Created by Administrator on 2017/12/18.
 */
public class User implements Serializable {

    //    结果：{userId:用户ID,loginName:登录名,userName:用户名,userMobile:手机号,headImg:头像URL}
//    userId:用户ID,
//    loginName:登录名,
//    userName:用户名,
//    userMobile:手机号,
//    userGender:0(女)/1(男),
//    userBirthday:2017-01-01,
//    headImg:头像URL
//    {"success":true,"data":{"userId":"738a11e88fec4beccfa196c3ec4c89dc","userName":"dadpajj",
//            "loginName":"15713693569","userMobile":"15713693569","userEmail":null,"userPhone":null,
//            "createDate":"2018-06-19 14:35:14","userOrder":9999,"userType":"GAME_USER","userSex":null,
//            "userBirthday":"2018-6-7",
//            "headImg":"upload/20180620/745311e8905d17b9e426e8cbaf23d9c1.jpeg","orgId":null}}

//    {"userId":"af2411e8a97fa5a62794e4be0707df55","userName":null,
//            "loginName":"dadpat_20180903_ycO6","userMobile":"15713692536",
//            "userPhone":null,"createDate":"2018-09-03 10:49:50","userOrder":9999,
//            "userType":"DEFAULT_USER","userEmail":null,"userSex":null,
//            "userBirthday":null,"headImg":null,"orgId":null}
    private String userId;
    private String loginName;
    private String userName;
    private String userMobile;
    private String userSex;
    private String userBirthday;
    private String headImg;

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", userMobile='" + userMobile + '\'' +
                ", userSex=" + userSex +
                ", userBirthday='" + userBirthday + '\'' +
                ", headImg='" + headImg + '\'' +
                '}';
    }
}
