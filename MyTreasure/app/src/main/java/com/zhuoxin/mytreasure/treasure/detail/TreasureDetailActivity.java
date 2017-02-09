package com.zhuoxin.mytreasure.treasure.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.zhuoxin.mytreasure.R;
import com.zhuoxin.mytreasure.commons.ActivityUtils;
import com.zhuoxin.mytreasure.custom.TreasureView;
import com.zhuoxin.mytreasure.treasure.Treasure;
import com.zhuoxin.mytreasure.treasure.map.MapFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//宝藏的详情页
public class TreasureDetailActivity extends AppCompatActivity implements TreasureDetailView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.detail_treasure)
    TreasureView mTreasureView;
    @BindView(R.id.tv_detail_description)
    TextView mTvDetail;

    private ActivityUtils mActivitiUtils;

    private static final String KEY_TREASURE = "key_treasure";
    private Treasure mTreasure;
    private TreasureDetailPresenter mTreasureDetailPresenter = new TreasureDetailPresenter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_detail);
    }

    /**
     * 对外提供一个跳转到本页面的方法：
     * 1、规范一下传递的数据：需要什么数据就必须传入什么数据
     * 2、Key简练
     */
    public static void open(@NonNull Context context, Treasure treasure) {
        Intent intent = new Intent(context, TreasureDetailActivity.class);
        intent.putExtra(KEY_TREASURE, treasure);
        context.startActivity(intent);

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        ButterKnife.bind(this);

        mActivitiUtils = new ActivityUtils(this);

        //拿到传递过来的数据
        mTreasure = (Treasure) getIntent().getSerializableExtra(KEY_TREASURE);

        //toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTreasure.getTitle());
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        //地图和宝藏的展示
        initMapView();

        //宝藏卡片的视图展示
        mTreasureView.bindTreasure(mTreasure);

        //宝物详情：进行网络获取（MVP）
        TreasureDetail treasureDetail = new TreasureDetail(mTreasure.getId());
        mTreasureDetailPresenter.getTreasureDetail(treasureDetail);

    }

    //地图和宝藏的展示
    private void initMapView() {

        //经纬度
        LatLng latLng = new LatLng(mTreasure.getLatitude(), mTreasure.getLongitude());

        //地图的状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(latLng)
                .overlook(-20)
                .zoom(18)
                .rotate(0)
                .build();

        //设置地图，什么操作都不能做，只是展示
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(false)
                .scrollGesturesEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false)
                .scaleControlEnabled(false)
                .rotateGesturesEnabled(false);

        MapView mapView = new MapView(this, options);

        //填充到布局里
        mFrameLayout.addView(mapView);

        //拿到地图的操作类
        BaiduMap map = mapView.getMap();

        //添加地图的覆盖物
        BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .icon(dot_expand)
                .anchor(0.5f, 0.5f);

        map.addOverlay(marker);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //导航图标的点击事件
    @OnClick(R.id.iv_navigation)
    public void showPopupMenu(View view) {

        //展示一个PopupMenu

        //创建
        PopupMenu popupMenu = new PopupMenu(this, view);

        //菜单布局填充
        popupMenu.inflate(R.menu.menu_navigation);

        //设置菜单项的点击监听
        popupMenu.setOnMenuItemClickListener(mMenuItemClickListener);

        //显示
        popupMenu.show();

    }

    //菜单项的点击监听
    private PopupMenu.OnMenuItemClickListener mMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            //起点和终点的信息拿到
            //我们自己的位置和地址
            LatLng start = MapFragment.getMyLocation();
            String startAddr = MapFragment.getLocationAddr();

            //终点的位置和地址
            LatLng end = new LatLng(mTreasure.getLatitude(), mTreasure.getLongitude());
            String endAddr = mTreasure.getLocation();

            switch (item.getItemId()) {
                case R.id.walking_navi:

                    //开始步行导航
                    startWalkingNavi(start, startAddr, end, endAddr);

                    break;
                case R.id.biking_navi:

                    //开始骑行导航
                    startBikingNavi(start, startAddr, end, endAddr);

                    break;
            }

            return false;
        }
    };

    //开始步行导航
    public void startWalkingNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {

        //导航的起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        //开启导航
        boolean walkNavi = BaiduMapNavigation.openBaiduMapWalkNavi(option, this);

        //未开启成功
        if (!walkNavi) {
            showDialog();
            //startWebNavi(startPoint, startAddr, endPoint, endAddr);
        }

    }

    //开始骑行导航
    public void startBikingNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {

        //导航的起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        //开启导航
        boolean bikeNavi = BaiduMapNavigation.openBaiduMapBikeNavi(option, this);

        //未开启成功
        if (!bikeNavi) {
            showDialog();
            //startWebNavi(startPoint, startAddr, endPoint, endAddr);
        }
    }

    //开启网页导航
    private void startWebNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {
        //导航的起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        //开启导航
        BaiduMapNavigation.openWebBaiduMapNavi(option, this);

    }

    //显示一个对话框提示没有安装
    public void showDialog() {

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您为安装百度地图app或版本过低")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OpenClientUtil.getLatestBaiduMapApp(TreasureDetailActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();

    }

    //--------------------------------视图接口里面需要实现的方法----------------------------------------
    @Override
    public void showMessage(String msg) {

        mActivitiUtils.showToast(msg);

    }

    @Override
    public void setData(List<TreasureDetailResult> resultList) {

        //请求的数据有内容
        if (resultList.size() >= 1) {

            TreasureDetailResult result = resultList.get(0);
            mTvDetail.setText(result.description);
            return;

        }
        mTvDetail.setText("当前的宝藏没有详情信息");

    }
    //--------------------------------视图接口里面需要实现的方法-------------


}
