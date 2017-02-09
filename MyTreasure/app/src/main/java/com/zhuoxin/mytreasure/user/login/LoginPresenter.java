package com.zhuoxin.mytreasure.user.login;

import android.os.AsyncTask;

import com.zhuoxin.mytreasure.net.NetClient;
import com.zhuoxin.mytreasure.user.User;
import com.zhuoxin.mytreasure.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/3.
 */

// 登录的业务类
public class LoginPresenter {

    private LoginView mLoginView;

    public LoginPresenter(LoginView loginView) {
        mLoginView = loginView;
    }

    public void login(User user) {
        mLoginView.showProgress();
        Call<LoginResult> loginResultCall = NetClient.getInstances().getTreasureApi().login(user);
        loginResultCall.enqueue(mCallback);
    }

    private Callback<LoginResult> mCallback = new Callback<LoginResult>() {

        @Override
        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
            mLoginView.hideProgress();
            if (response.isSuccessful()) {
                LoginResult loginResult = response.body();
                if (loginResult == null) {
                    mLoginView.showMessage("未知的错误");
                    return;
                }
                if (loginResult.getCode() == 1) {
                    // 真正的登录成功了
                    //保存头像和tokenid
                    UserPrefs.getInstance().setPhoto(NetClient.BASE_URL + loginResult.getHeadpic());
                    UserPrefs.getInstance().setTokenid(loginResult.getTokenid());
                    mLoginView.navigationToHome();
                }
                mLoginView.showMessage(loginResult.getMsg());
            }
        }

        @Override
        public void onFailure(Call<LoginResult> call, Throwable t) {
            mLoginView.hideProgress();
            mLoginView.showMessage("请求失败：" + t.getMessage());
        }
    };
}
