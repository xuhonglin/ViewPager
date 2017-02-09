package com.zhuoxin.mytreasure.treasure.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zhuoxin.mytreasure.R;
import com.zhuoxin.mytreasure.commons.ActivityUtils;
import com.zhuoxin.mytreasure.custom.TreasureView;
import com.zhuoxin.mytreasure.treasure.Area;
import com.zhuoxin.mytreasure.treasure.Treasure;
import com.zhuoxin.mytreasure.treasure.TreasureRepo;
import com.zhuoxin.mytreasure.treasure.detail.TreasureDetailActivity;
import com.zhuoxin.mytreasure.treasure.hide.HideTreasureActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/1/4.
 */

//宝藏页面：地图的展示和宝藏数据的展示
public class MapFragment extends Fragment implements MapMvpView {

    @BindView(R.id.map_frame)
    FrameLayout mMapFrame;
    @BindView(R.id.btn_HideHere)
    Button mBtnHideHere;
    @BindView(R.id.iv_scaleUp)
    ImageView mIvScaleUp;
    @BindView(R.id.iv_scaleDown)
    ImageView mIvScaleDown;
    @BindView(R.id.tv_located)
    TextView mTvLocated;
    @BindView(R.id.tv_satellite)
    TextView mTvSatellite;
    @BindView(R.id.tv_compass)
    TextView mTvCompass;
    @BindView(R.id.tv_currentLocation)
    TextView mTvCurrentLocation;
    @BindView(R.id.iv_toTreasureInfo)
    ImageView mIvToTreasureInfo;
    @BindView(R.id.et_treasureTitle)
    EditText mEtTreasureTitle;
    @BindView(R.id.cardView)
    CardView mCardView;
    @BindView(R.id.layout_bottom)
    FrameLayout mLayoutBottom;
    @BindView(R.id.treasureView)
    TreasureView mTreasureView;
    @BindView(R.id.centerLayout)
    RelativeLayout mCenterLayout;
    @BindView(R.id.hide_treasure)
    RelativeLayout mHideTreasure;

    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private static LatLng mCurrentLocation;
    private LatLng mCurrentStatus;
    private Marker mCurrentMarker;
    private Unbinder mUnbinder;
    private boolean isFirst = true;
    private MapView mMapView;

    private ActivityUtils mActivityUtils;
    private MapPresenter mMapPresenter;
    private GeoCoder mGeoCoder;
    private String mCurrentAddr;
    private static String mLocationAddr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivityUtils = new ActivityUtils(this);

        //初始化百度地图
        initMapView();

        mMapPresenter = new MapPresenter(this);

        //初始化定位相关
        initLocation();

        //初始化地理编码相关
        initGeoCoder();
    }

    //初始化地理编码相关
    private void initGeoCoder() {

        //初始化：创建出一个地理编码查询的对象
        mGeoCoder = GeoCoder.newInstance();

        //给查询的对象设置结果的监听：编码的监听
        mGeoCoder.setOnGetGeoCodeResultListener(mGeoCoderResultListener);

    }

    //给查询的对象设置结果的监听：编码的监听
    private OnGetGeoCoderResultListener mGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        //得到地理编码的结果：地址——》经纬度
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        //得到反向地理编码的结果：经纬度——》地址
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            //当前是拿到结果以后给标题录入的卡片上面的文本设置上
            if (reverseGeoCodeResult == null) {
                mCurrentAddr = "未知的位置";
                return;
            }

            //拿到反向地理编码得到的位置信息
            mCurrentAddr = reverseGeoCodeResult.getAddress();

            //将地址信息给TextView设置上
            mTvCurrentLocation.setText(mCurrentAddr);

        }
    };

    //初始化定位相关
    private void initLocation() {

        //前置：激活定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //第一步：初始化LocationClient类
        mLocationClient = new LocationClient(getContext().getApplicationContext());

        //第二步：配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setCoorType("bd09ll");//设置百度坐标类型，默认gcj02会有偏差，bd09ll百度坐标类型，将无偏差
        option.setIsNeedAddress(true);//需要地址信息

        mLocationClient.setLocOption(option);

        //第三步：实现BDLocationListener接口
        mLocationClient.registerLocationListener(mBDLocationListener);

        //第四步：开始定位
        mLocationClient.start();
    }

    //定位监听
    private BDLocationListener mBDLocationListener = new BDLocationListener() {

        //获取定位结果
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            //如果没拿到结果，重新请求
            if (bdLocation == null) {
                mLocationClient.requestLocation();
                return;
            }

            //定位的经纬度
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            //定位的经纬度的类
            mCurrentLocation = new LatLng(latitude, longitude);

            //定位的地址
            mLocationAddr = bdLocation.getAddrStr();

            Log.e("TAG", "定位的位置" + mLocationAddr + "，经纬度为：" + latitude + "，" + longitude);

            //设置定位图层展示的数据
            MyLocationData data = new MyLocationData.Builder()
                    //定位数据展示的经纬度
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)//定位精度的大小
                    .build();

            //定位数据展示到地图上
            mBaiduMap.setMyLocationData(data);

            //移动到定位的地方，在地图上展示定位的信息：位置
            //做一个判断：第一次进入页面自动移动，其他时候点击按钮移动
            if (isFirst) {
                moveToLocation();
                isFirst = false;
            }
        }
    };

    //初始化百度地图
    private void initMapView() {

        //设置地图状态
        MapStatus mapStatus = new MapStatus.Builder()
                .zoom(19)//缩放级别（3—21：默认显示12）
                .overlook(0)//俯仰角度
                .rotate(0)//旋转角度
                .build();

        //设置地图数据（设置信息）
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)//指南针默认打开
                .zoomGesturesEnabled(true)//是否允许缩放手势
                .scaleControlEnabled(false)//不显示比例尺
                .zoomControlsEnabled(false)//不显示缩放的控件
                ;

        //创建
        mMapView = new MapView(getContext(), options);

        //在布局上添加地图控件，0代表第一位
        mMapFrame.addView(mMapView, 0);

        //拿到地图的操作类（控制器：操作地图等都是使用这个）
        mBaiduMap = mMapView.getMap();

        //设置地图状态的监听
        mBaiduMap.setOnMapStatusChangeListener(mStatusChangeListener);

        //设置地图上标注物的点击监听
        mBaiduMap.setOnMarkerClickListener(mMarkerClickListener);
    }

    //地图上标注物的点击监听
    private BaiduMap.OnMarkerClickListener mMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (mCurrentMarker != null) {
                if (mCurrentMarker != marker) {
                    mCurrentMarker.setVisible(true);
                }
                mCurrentMarker.setVisible(true);
            }
            mCurrentMarker = marker;

            //点击Marker展示InfoWindow，当前覆盖物不可见
            mCurrentMarker.setVisible(false);

            //创建一个InfoWindow
            InfoWindow infoWindow = new InfoWindow(dot_expand, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {

                // InfoWindow的监听
                @Override
                public void onInfoWindowClick() {
                    //切换回普通的视图
                    changeUIMode(UI_MODE_NORMAL);
                }
            });
            //地图上显示一个InfoWindow
            mBaiduMap.showInfoWindow(infoWindow);

            //宝藏信息的取出和展示
            int id = marker.getExtraInfo().getInt("id");
            Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
            mTreasureView.bindTreasure(treasure);

            //TreasureView展示出来
            //切换到宝藏选中视图
            changeUIMode(UI_MODE_SELECT);

            return false;
        }
    };

    //地图状态的监听
    private BaiduMap.OnMapStatusChangeListener mStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {

        //变化前
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        //变化中
        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        //变化结束后
        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

            //当前地图的位置
            LatLng target = mapStatus.target;

            //确实地图的状态发生变化
            if (target != MapFragment.this.mCurrentStatus) {

                //地图状态发生变化之后实时获取当前区域内的宝藏
                updateMapArea();

                //在埋藏宝藏的情况下才发起反地理编码的获取
                if (mUIMode == UI_MODE_HIDE) {

                    //设置反地理编码的位置信息
                    ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                    option.location(target);

                    //发起反地理编码
                    mGeoCoder.reverseGeoCode(option);
                }

                //地图位置的标记
                MapFragment.this.mCurrentStatus = target;

            }

        }
    };


    //卫星视图和普通视图的切换
    @OnClick(R.id.tv_satellite)
    public void switchMapType() {
        int mapType = mBaiduMap.getMapType();//获取当前的地图类型
        //切换类型
        mapType = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;
        //卫星和普通的文字的显示
        String msg = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? "卫星" : "普通";
        mBaiduMap.setMapType(mapType);
        mTvSatellite.setText(msg);
    }

    //指南针
    @OnClick(R.id.tv_compass)
    public void switchCompass() {
        //指南针有没有显示
        boolean compassEnabled = mBaiduMap.getUiSettings().isCompassEnabled();
        mBaiduMap.getUiSettings().setCompassEnabled(!compassEnabled);
    }

    //地图的放大和缩小
    @OnClick({R.id.iv_scaleUp, R.id.iv_scaleDown})
    public void scaleMap(View view) {
        switch (view.getId()) {
            case R.id.iv_scaleDown:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            case R.id.iv_scaleUp:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
        }
    }

    //定位的按钮：移动到定位的地方
    @OnClick(R.id.tv_located)
    public void moveToLocation() {

        //地图状态的设置：设置到定位的地方
        MapStatus mapStatus = new MapStatus.Builder()
                .target(mCurrentLocation)//目标位置
                .rotate(0)
                .overlook(0)
                .zoom(18)
                .build();

        //更新状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);

        //更新展示的地图的状态
        mBaiduMap.animateMapStatus(update);
    }

    //宝藏显示的卡片的点击事件
    @OnClick(R.id.treasureView)
    public void clickTreasureView() {

        //跳转到详情页
        int id = mCurrentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);

        TreasureDetailActivity.open(getContext(), treasure);

    }

    //点击宝藏标题录入的卡片，跳转到埋藏宝藏的详情页面
    @OnClick(R.id.hide_treasure)
    public void hideTreasure() {

        String title = mEtTreasureTitle.getText().toString();

        if (TextUtils.isEmpty(title)) {
            mActivityUtils.showToast("请输入宝藏标题");
            return;
        }

        //跳转到埋藏宝藏的详细页面上
        LatLng latLng = mBaiduMap.getMapStatus().target;
        HideTreasureActivity.open(getContext(), title, mCurrentAddr, latLng, 0);

    }

    //根据位置的变化，区域也发生变化
    private void updateMapArea() {

        //当前地图的状态
        MapStatus mapStatus = mBaiduMap.getMapStatus();

        //当前地图的经纬度
        double longitude = mapStatus.target.longitude;
        double latitude = mapStatus.target.latitude;

        //根据地图位置拿到一个区域
        Area area = new Area();
        //向上向下进行取整经纬度
        area.setMaxLat(Math.ceil(latitude));
        area.setMaxLng(Math.ceil(longitude));
        area.setMinLat(Math.floor(latitude));
        area.setMinLng(Math.floor(longitude));

        //根据区域获取宝藏数据
        mMapPresenter.getTreasure(area);
    }

    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_dot);
    private BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);

    //添加覆盖物
    private void addMarker(LatLng latLng, int treasureId) {

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);//覆盖物的位置
        options.icon(dot);//覆盖物的图标
        options.anchor(0.5f, 0.5f);//锚点位置：居中

        //将宝藏的ID信息保存到marker里面
        Bundle bundle = new Bundle();
        bundle.putInt("id", treasureId);
        options.extraInfo(bundle);

        //添加覆盖物
        mBaiduMap.addOverlay(options);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    //将定位的地址提供其他调用获取
    public static LatLng getMyLocation() {
        return mCurrentLocation;
    }

    public static String getLocationAddr() {
        return mLocationAddr;
    }

    private static final int UI_MODE_NORMAL = 0;//普通的视图
    private static final int UI_MODE_SELECT = 1;//宝藏选中的视图
    private static final int UI_MODE_HIDE = 2;//埋藏宝藏的视图

    private static int mUIMode = UI_MODE_NORMAL;

    //把所有视图的变化统一到一个方法里面：视图的切换是根据布局控件或其他（marker、infoWindow）的显示和隐藏来控制
    public void changeUIMode(int uiMode) {

        if (mUIMode == uiMode) {
            return;
        }
        mUIMode = uiMode;

        switch (uiMode) {
            case UI_MODE_NORMAL:
                if (mCurrentMarker != null) {
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mLayoutBottom.setVisibility(View.GONE);
                mCenterLayout.setVisibility(View.GONE);
                break;
            case UI_MODE_SELECT:
                mLayoutBottom.setVisibility(View.VISIBLE);
                mTreasureView.setVisibility(View.VISIBLE);
                mCenterLayout.setVisibility(View.GONE);
                mHideTreasure.setVisibility(View.GONE);
                break;
            case UI_MODE_HIDE:
                if (mCurrentMarker != null) {
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mCenterLayout.setVisibility(View.VISIBLE);
                mLayoutBottom.setVisibility(View.GONE);
                mBtnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLayoutBottom.setVisibility(View.VISIBLE);
                        mTreasureView.setVisibility(View.GONE);
                        mHideTreasure.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }

    //-----------------数据请求的视图方法-------------------------------------------------
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void setData(List<Treasure> list) {

        //再次网络请求拿到数据添加覆盖物之前，清理之前的覆盖物
        mBaiduMap.clear();//清空地图上所有的覆盖物和infoWindow

        for (Treasure treasure : list) {

            LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());
            addMarker(latLng, treasure.getId());
        }
    }

    //对外提供一个方法：可以退出了
    public boolean clickbackPressed() {

        //如果不是普通的视图，切换成普通的视图
        if (mUIMode != UI_MODE_NORMAL) {
            changeUIMode(UI_MODE_NORMAL);
            return false;
        }

        //是普通的视图，告诉HomeActivity可以退出了
        return true;

    }


}
