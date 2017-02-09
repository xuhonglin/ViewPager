package com.zhuoxin.mytreasure.treasure.hide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.baidu.mapapi.model.LatLng;
import com.zhuoxin.mytreasure.R;
import com.zhuoxin.mytreasure.commons.ActivityUtils;
import com.zhuoxin.mytreasure.treasure.TreasureRepo;
import com.zhuoxin.mytreasure.user.UserPrefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HideTreasureActivity extends AppCompatActivity implements HideTreasureView {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_LOCATION = "key_location";
    private static final String KEY_LATLNG = "key_latlng";
    private static final String KEY_ALTITUDE = "key_altitude";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_description)
    EditText mEtDescription;
    private ProgressDialog mDialog;
    private ActivityUtils mActivityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_treasure);

    }

    //对外提供一个跳转的方法
    public static void open(Context context, String title, String address, LatLng latLng, double altitude) {

        Intent intent = new Intent(context, HideTreasureActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_LOCATION, address);
        intent.putExtra(KEY_LATLNG, latLng);
        intent.putExtra(KEY_ALTITUDE, altitude);
        context.startActivity(intent);

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mActivityUtils = new ActivityUtils(this);

        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(KEY_TITLE));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    //处理toolbar的返回箭头
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //点击的时候上传宝藏到服务器
    @OnClick(R.id.hide_send)
    public void onClick() {

        //网络请求的数据上传

        //取出传递的数据
        Intent intent = getIntent();
        String title = intent.getStringExtra(KEY_TITLE);
        String address = intent.getStringExtra(KEY_LOCATION);
        double altitude = intent.getDoubleExtra(KEY_ALTITUDE, 0);
        LatLng latLng = intent.getParcelableExtra(KEY_LATLNG);

        //拿到用户登录的tokenid
        int tokenid = UserPrefs.getInstance().getTokenid();

        //输入的宝藏详情
        String string = mEtDescription.getText().toString();

        //上传的数据的实体类
        HideTreasure hideTreasure = new HideTreasure();
        hideTreasure.setTitle(title);//标题
        hideTreasure.setAltitude(altitude);//海拔
        hideTreasure.setDescription(string);//描述
        hideTreasure.setLatitude(latLng.latitude);//纬度
        hideTreasure.setLongitude(latLng.longitude);//经度
        hideTreasure.setLocation(address);//宝藏地址
        hideTreasure.setTokenId(tokenid);//tokenid

        //埋藏宝藏的网络请求的数据上传
        new HideTreasurePresenter(this).hideTreasure(hideTreasure);

    }

    //---------------------视图接口里面的方法------------------------------
    @Override
    public void showProgress() {
        mDialog = ProgressDialog.show(this, "宝藏上传", "宝藏正在上传中");
    }

    @Override
    public void hideProgres() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void navigationToHome() {
        finish();

        //清除缓存：为了回到之前的页面重新请求数据，而不是再从缓存中取
        TreasureRepo.getInstance().clear();

    }

    //---------------------视图接口里面的方法-------------------------
}
