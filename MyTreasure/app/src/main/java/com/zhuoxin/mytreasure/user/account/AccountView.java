package com.zhuoxin.mytreasure.user.account;

/**
 * Created by Administrator on 2017/1/13.
 */

public interface AccountView {

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void updatePhoto(String photoUrl);

}
