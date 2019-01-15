package com.benbaba.dadpat.host.http;


import com.benbaba.dadpat.host.bean.NoticeBean;
import com.benbaba.dadpat.host.bean.PluginBean;
import com.benbaba.dadpat.host.bean.ThirdLoginBean;
import com.benbaba.dadpat.host.bean.TokenBean;
import com.benbaba.dadpat.host.bean.User;
import com.benbaba.dadpat.host.http.entry.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2017/12/18.
 */
public interface ApiService {

    //注册
    @FormUrlEncoded
    @POST("user/auth/register.do")
    Observable<HttpResult<User>> register(@FieldMap Map<String, String> map);

    //验证验证码
    @FormUrlEncoded
    @POST("user/sms/verify.do")
    Observable<HttpResult> verifySms(@FieldMap Map<String, String> map);

    //jwt方式登陆
    @FormUrlEncoded
    @POST("user/jwt/access.do")
    Observable<HttpResult<TokenBean>> doLogin(@FieldMap Map<String, String> map);

    //退出登陆
    @POST("user/auth/logout.do")
    Observable<HttpResult> doLogout();

    @FormUrlEncoded
    @POST("user/auth/updateCredentialBySms.do")
    Observable<HttpResult<String>> modifyPsd(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("favorite/save.do")
    Observable<HttpResult<String>> getEarthTest(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("user/auth/{accessType}/access.do")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<HttpResult<ThirdLoginBean>> loginThird(@Path("accessType") String accessType, @Field("userType") String userType,
                                                      @Field("data") String data);

    @GET("user/get.do")
    Observable<HttpResult> isTokenExpire();

    //获取用户信息
    @GET("user/get.do")
    Observable<HttpResult<User>> getUser();

    @FormUrlEncoded
    @POST("user/update.do")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<HttpResult> updateUserInfo(@FieldMap Map<String, String> names);

    //更新用户头像
    @Multipart
    @POST("user/saveHeadImg.do")
    Observable<HttpResult<String>> updateUserPhoto(@Part MultipartBody.Part file);

    @GET("apkType/getLastApkByOrder.do?order=2")
    Observable<HttpResult<List<PluginBean>>> getPluginList();

    @GET("apkType/getLastApkByOrder.do?order=1")
    Observable<HttpResult<List<PluginBean>>> getHostApp();

    @GET("notice/getList.do")
    Observable<HttpResult<List<NoticeBean>>> getNoticeList();

    //提交反馈意见
    @FormUrlEncoded
    @POST("advise/save.do")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<HttpResult<String>> postFeedBack(@FieldMap Map<String, String> map);

}
