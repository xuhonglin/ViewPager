package com.zhuoxin.mytreasure.user.register;


import com.zhuoxin.mytreasure.net.NetClient;
import com.zhuoxin.mytreasure.user.User;
import com.zhuoxin.mytreasure.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Administrator on 2017/1/3.
 */

//注册的业务类
public class RegisterPresenter {
    /**
     * 视图的交互怎么处理
     * 1、RegisterActivity创建出来后调用方法（又持有对象，没有分离）
     * 2、接口回调
     * 接口的实例化和接口方法的实现
     * 让Activity实现视图接口
     */

    private RegisterView mRegisterView;

    public RegisterPresenter(RegisterView registerView) {
        mRegisterView = registerView;
    }

    public void register(User user) {
        mRegisterView.showProgress();
        Call<RegisterResult> resultCall = NetClient.getInstances().getTreasureApi().register(user);
        resultCall.enqueue(mResultCallback);
    }

    private Callback<RegisterResult> mResultCallback = new Callback<RegisterResult>() {
        //注册成功
        @Override
        public void onResponse(Call<RegisterResult> call, retrofit2.Response<RegisterResult> response) {

            mRegisterView.hideProgress();

            //相应成功
            if (response.isSuccessful()) {
                RegisterResult result = response.body();

                //响应体是不是为null
                if (result == null) {
                    mRegisterView.showMessage("发生了未知错误");
                    return;
                }
                //不为空
                if (result.getCode() == 1) {
                    //真正注册成功
                    //保存tokenid
                    UserPrefs.getInstance().setTokenid(result.getTokenId());
                    mRegisterView.navigationToHome();
                }
                mRegisterView.showMessage(result.getMsg());
            }

        }

        //注册失败
        @Override
        public void onFailure(Call<RegisterResult> call, Throwable t) {
            mRegisterView.hideProgress();
            mRegisterView.showMessage("请求失败" + t.getMessage());
        }
    };
}
