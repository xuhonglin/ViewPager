package com.zhuoxin.mytreasure.user.login;

/**
 * Created by Administrator on 2017/1/3.
 */

//登录的视图接口
public interface LoginView {

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void navigationToHome();
}
