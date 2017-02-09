package com.zhuoxin.vedionews.api;

import com.zhuoxin.vedionews.entity.UserResult;
import com.zhuoxin.vedionews.entity.UserInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/12/22.
 */

public interface UserApi {
    @POST("1/users")
    Call<UserResult> register(@Body UserInfo userInfo);

    @GET("1/login")
    Call<UserResult> login(@Query("username") String username, @Query("password") String password);
}
