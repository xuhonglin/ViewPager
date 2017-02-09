package com.zhuoxin.mytreasure.user.account;

import com.zhuoxin.mytreasure.net.NetClient;
import com.zhuoxin.mytreasure.user.UserPrefs;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/13.
 */

//头像处理的业务类
public class AccountPresenter {

    private AccountView mAccountView;

    public AccountPresenter(AccountView accountView) {
        mAccountView = accountView;
    }

    public void uploadPhoto(File file) {

        //进度显示
        mAccountView.showProgress();

        //构建上传的图片文件的部分
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", "photo.png", RequestBody.create(null, file));

        //上传的请求
        Call<UploadResult> uploadResultCall = NetClient.getInstances().getTreasureApi().upload(part);
        uploadResultCall.enqueue(mResultCallback);

    }

    private Callback<UploadResult> mResultCallback = new Callback<UploadResult>() {

        @Override
        public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {


            if (response.isSuccessful()) {
                UploadResult body = response.body();
                if (body == null) {
                    //提示一下
                    mAccountView.showMessage("未知的错误");
                    return;
                }

                //提示
                mAccountView.showMessage(body.getMsg());
                if (body.getCount() != 1) {
                    return;
                }

                String photoUrl = body.getUrl();

                //拿到的头像地址存储到用户仓库里面
                UserPrefs.getInstance().setPhoto(NetClient.BASE_URL + photoUrl);

                //更新个人页面的头像展示
                mAccountView.updatePhoto(NetClient.BASE_URL + photoUrl);

                //更新信息！！！重新在个人信息上加载、保存到用户信息中等
                String substring = photoUrl.substring(photoUrl.lastIndexOf("/") + 1, photoUrl.length());
                Update update = new Update(UserPrefs.getInstance().getTokenid(), substring);
                Call<UpdateResult> resultCall = NetClient.getInstances().getTreasureApi().update(update);
                resultCall.enqueue(mUpdateResultCallback);

            }

        }

        @Override
        public void onFailure(Call<UploadResult> call, Throwable t) {

            //提示
            mAccountView.hideProgress();
            mAccountView.showMessage("请求失败" + t.getMessage());
        }
    };

    private Callback<UpdateResult> mUpdateResultCallback = new Callback<UpdateResult>() {
        @Override
        public void onResponse(Call<UpdateResult> call, Response<UpdateResult> response) {

            mAccountView.hideProgress();
            if (response.isSuccessful()) {
                UpdateResult result = response.body();
                if (result == null) {
                    mAccountView.showMessage("未知的错误");
                    return;
                }
                mAccountView.showMessage(result.getMsg());
                if (result.getCode() != 1) {
                    return;
                }
            }

        }

        @Override
        public void onFailure(Call<UpdateResult> call, Throwable t) {

            mAccountView.hideProgress();
            mAccountView.showMessage("更新失败" + t.getMessage());

        }
    };

}
