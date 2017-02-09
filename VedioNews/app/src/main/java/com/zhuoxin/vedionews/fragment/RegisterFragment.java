package com.zhuoxin.vedionews.fragment;

import android.os.Bundle;
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
import com.zhuoxin.vedionews.entity.UserInfo;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/12/21.
 */

public class RegisterFragment extends DialogFragment {
    View view;
    @BindView(R.id.etUsername)
    EditText mEtUsername;
    @BindView(R.id.etPassword)
    EditText mEtPassword;
    @BindView(R.id.btnRegister)
    Button mBtnRegister;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
        view = inflater.inflate(R.layout.dialog_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btnRegister)
    public void onClick() {
        final String userName = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //显示进度条
        mBtnRegister.setVisibility(View.INVISIBLE);

//        Call call = BombClient.getInstance().register(userName,password);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("username", userName);
//            jsonObject.put("password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody requestBody = RequestBody.create(null, jsonObject.toString());
        // 网络模块，注册请求
        UserApi userApi =BombClient.getInstance().getUserApi();
        UserInfo userInfo = new UserInfo(userName, password);
        Call<UserResult> call = userApi.register(userInfo);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                mBtnRegister.setVisibility(View.VISIBLE);
                if (!response.isSuccessful()) {
                    try {
                        String error = response.errorBody().string();
                        ErrorResult errorResult = new Gson().fromJson(error, ErrorResult.class);
                        Toast.makeText(getContext(), "注册失败" + errorResult.getError(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    UserResult userResult = response.body();
                    listener.registerSuccess(userName, userResult.getObjectId());
                    Toast.makeText(getContext(),"注册成功", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                mBtnRegister.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //当注册成功会触发的方法
    public interface OnRegisterSuccessListener {
        //当注册成功时，来调用
        void registerSuccess(String username, String objectId);
    }

    private OnRegisterSuccessListener listener;

    public void setListener(OnRegisterSuccessListener listener) {
        this.listener = listener;
    }
}
