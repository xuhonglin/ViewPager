package com.zhuoxin.mytreasure.net;

import com.zhuoxin.mytreasure.treasure.Area;
import com.zhuoxin.mytreasure.treasure.Treasure;
import com.zhuoxin.mytreasure.treasure.detail.TreasureDetail;
import com.zhuoxin.mytreasure.treasure.detail.TreasureDetailResult;
import com.zhuoxin.mytreasure.treasure.hide.HideTreasure;
import com.zhuoxin.mytreasure.treasure.hide.HideTreasureResult;
import com.zhuoxin.mytreasure.user.User;
import com.zhuoxin.mytreasure.user.account.Update;
import com.zhuoxin.mytreasure.user.account.UpdateResult;
import com.zhuoxin.mytreasure.user.account.UploadResult;
import com.zhuoxin.mytreasure.user.login.LoginResult;
import com.zhuoxin.mytreasure.user.register.RegisterResult;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Administrator on 2017/1/9.
 */

// 请求构建的接口
public interface TreasureApi {

    // 登录的请求
    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);

    // 注册的请求
    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult> register(@Body User user);

    //获取区域内宝藏数据的请求
    @POST("/Handler/TreasureHandler.ashx?action=show")
    Call<List<Treasure>> getTreasureInArea(@Body Area area);

    //宝藏详情的请求
    @POST("/Handler/TreasureHandler.ashx?action=tdetails")
    Call<List<TreasureDetailResult>> getTreasureDetail(@Body TreasureDetail treasureDetail);

    //埋藏宝藏的请求
    @POST("/Handler/TreasureHandler.ashx?action=hide")
    Call<HideTreasureResult> hideTreasure(@Body HideTreasure hideTreasure);

    // 关于头像（文件）上传的两种方式（多部分请求）
    /*@Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part("file\";filename=\"image.png\"") RequestBody body);
*/
    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part MultipartBody.Part part);

    //更新的请求
    @POST("/Handler/UserHandler.ashx?action=update")
    Call<UpdateResult> update(@Body Update update);

}
