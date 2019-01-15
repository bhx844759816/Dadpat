package com.benbaba.dadpat.host.http;

/**
 * 统一处理网络错误
 * Created by Administrator on 2018/2/10.
 */
public class ApiException extends RuntimeException {
    public static final String CODE_SMS_CODE_ERROR = "SMS_CODE_ERROR";
    public static final String CODE_USER_MOBILE_ALREADY_EXIST = "USER_MOBILE_ALREADY_EXIST";//注册得时候手机号已注册
    public static final String CODE_USER_NOT_FOUND = "USER_NOT_FOUND";//注册得时候手机号已注册
    public static final String CODE_USER_ERROR_PASSWORD = "USER_ERROR_PASSWORD";//注册得时候手机号已注册
    public static final String CODE_USER_EMPTY_AUTHENTICATION = "USER_EMPTY_AUTHENTICATION";//授权信息为空
    public static final String CODE_USER_ALREADY_EXIST = "USER_ALREADY_EXIST";
    public static final String CODE_NETWORK_ERROR = "CODE_NETWORK_ERROR";

    private String code;

    private String message;

    public ApiException(String code) {
        this.code = code;
        message = getApiExceptionMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     *
     * @return
     */
    private String getApiExceptionMessage() {
        String message;
        if (CODE_USER_ALREADY_EXIST.equals(code)) {
            message = "用户已经存在，请登陆";
        } else if (CODE_USER_ERROR_PASSWORD.equals(code)) {
            message = "用户密码错误";
        } else if (CODE_USER_NOT_FOUND.equals(code)) {
            message = "未发现用户请注册";
        } else if (CODE_SMS_CODE_ERROR.equals(code)) {
            message = "验证码错误";
        } else if (CODE_USER_MOBILE_ALREADY_EXIST.equals(code)) {
            message = "手机号已被注册";
        } else if (CODE_USER_EMPTY_AUTHENTICATION.equals(code)) {
            message = "授权失败";
        } else if (CODE_NETWORK_ERROR.equals(code)) {
            message = "网络连接错误，请检测网络稍后重试";
        } else {
            message = "未知错误";
        }
        return message;
    }


}
