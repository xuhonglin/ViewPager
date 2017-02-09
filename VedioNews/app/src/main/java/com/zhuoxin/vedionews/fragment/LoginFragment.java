package com.zhuoxin.vedionews.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.zhuoxin.vedionews.R;
import com.zhuoxin.vedionews.bombapi.BombClient;
import com.zhuoxin.vedionews.api.UserApi;
import com.zhuoxin.vedionews.entity.ErrorResult;
import com.zhuoxin.vedionews.entity.UserResult;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gqq on 2016/12/7.
 */

public class LoginFragment extends DialogFragment {

    private Unbinder mUnbinder;

    @BindView(R.id.etUsername)
    EditText mEtUsername;
    @BindView(R.id.etPassword)
    EditText mEtPassword;
    @BindView(R.id.btnLogin)
    Button mBtnLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //无标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_login, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btnLogin)
    public void onClick() {
        final String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        //用户名密码能不为空
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //显示进度条
        mBtnLogin.setVisibility(View.GONE);

        //登录网络请求
        UserApi userApi = BombClient.getInstance().getUserApi();
        Call<UserResult> call = userApi.login(username,password);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                //隐藏进度条
                mBtnLogin.setVisibility(View.VISIBLE);
                //登录未成功
                if (!response.isSuccessful()){
                    try {
                        String error = response.errorBody().string();
                        ErrorResult errorResult = new Gson().fromJson(error,ErrorResult.class);
                        Toast.makeText(getContext(), "登录失败" + errorResult.getError(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                //登录成功
                UserResult userResult = response.body();
                listener.loginSuccess(username, userResult.getObjectId());
                Toast.makeText(getContext(),"登录成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                //隐藏进度条
                mBtnLogin.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),t.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    //当登录成功之后触发的方法
    public interface OnLoginSuccessListener {
        /**
         * 当登录成功时，将来调用
         */
        void loginSuccess(String username, String objectId);
    }

    private OnLoginSuccessListener listener;

    public void setListener(@NonNull OnLoginSuccessListener listener) {
        this.listener = listener;
    }
}
