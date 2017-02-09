package com.zhuoxin.mytreasure.user.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.zhuoxin.mytreasure.MainActivity;
import com.zhuoxin.mytreasure.R;
import com.zhuoxin.mytreasure.commons.ActivityUtils;
import com.zhuoxin.mytreasure.commons.RegexUtils;
import com.zhuoxin.mytreasure.custom.AlertDialogFragment;
import com.zhuoxin.mytreasure.treasure.HomeActivity;
import com.zhuoxin.mytreasure.user.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_Username)
    EditText mEtUsername;
    @BindView(R.id.et_Password)
    EditText mEtPassword;
    @BindView(R.id.et_Confirm)
    EditText mEtConfirm;
    @BindView(R.id.btn_Register)
    Button mBtnRegister;
    private String mUsername;
    private String mPassword;

    private ActivityUtils mActivityUtils;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 会触发onContentChanged方法
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);

        mActivityUtils = new ActivityUtils(this);

        // toolbar的展示和返回箭头的监听
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {

            // 激活左上角的返回图标(内部使用选项菜单处理)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // 设置title
            getSupportActionBar().setTitle(R.string.register);
        }

        // EditText 的输入监听，监听文本的变化
        mEtUsername.addTextChangedListener(textWatcher);
        mEtPassword.addTextChangedListener(textWatcher);
        mEtConfirm.addTextChangedListener(textWatcher);
    }

    // 文本输入监听
    private TextWatcher textWatcher = new TextWatcher() {

        // 文本变化前
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        // 文本输入变化
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        // 文本输入之后，在这里面处理按钮的点击
        @Override
        public void afterTextChanged(Editable s) {
            // 处理文本输入之后的按钮事件
            mUsername = mEtUsername.getText().toString();
            mPassword = mEtPassword.getText().toString();
            String confirm = mEtConfirm.getText().toString();
            boolean canregister = !(TextUtils.isEmpty(mUsername) ||
                    TextUtils.isEmpty(mPassword) ||
                    TextUtils.isEmpty(confirm))
                    && mPassword.equals(confirm);
            mBtnRegister.setEnabled(canregister);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 处理ActionBar的返回箭头事件
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Register)
    public void onClick() {
        // 注册的视图和业务处理
        if (RegexUtils.verifyUsername(mUsername) != RegexUtils.VERIFY_SUCCESS) {

            // 显示一个错误的对话框：自定义一个
            AlertDialogFragment.getInstances(
                    getString(R.string.username_error),
                    getString(R.string.username_rules))
                    .show(getSupportFragmentManager(), "usernameError");
            return;
        }

        if (RegexUtils.verifyPassword(mPassword) != RegexUtils.VERIFY_SUCCESS) {
            // 显示一个错误的对话框
            AlertDialogFragment.getInstances(
                    getString(R.string.password_error),
                    getString(R.string.password_rules))
                    .show(getSupportFragmentManager(), "passwordError");

            return;
        }
        // 进行注册的功能
        new RegisterPresenter(this).register(new User(mUsername, mPassword));
    }

    // 跳转页面
    @Override
    public void navigationToHome() {
        mActivityUtils.startActivity(HomeActivity.class);
        finish();

        //发送本地广播去关闭页面
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    // 显示信息
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    // 隐藏进度
    @Override
    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    // 显示进度
    @Override
    public void showProgress() {
        mDialog = ProgressDialog.show(this, "注册", "正在注册，请稍后");
    }
}
