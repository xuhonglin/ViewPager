package com.zhuoxin.phone.activity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.BaseActivity;
import com.zhuoxin.phone.db.DBManager;
import com.zhuoxin.phone.service.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zhuoxin.phone.adapter.PagerGuideAdapter;

public class GuideActivity extends BaseActivity {
    ViewPager vp_guide;
    TextView tv_skip;
    //小红点的初始padding_left为0
    ImageView iv_circle_red;
    //定义移动的宽度
    float pixelWidth;
    //判断是否是从SettingsActivity界面过来的值
    boolean isFromSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        //获取bundle中的数据
        Bundle bundle = getIntent().getBundleExtra("bundle");//如果从桌面启动，获取不到bundle
        //加一个非空判断就好了
        if (bundle != null) {
            isFromSettings = bundle.getBoolean("isFromSettings", false);
        }
        boolean isFirstRun = getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFromSettings) {
            initView();
            startService(MusicService.class);
        } else if (isFirstRun) {
            initView();
            startService(MusicService.class);
        } else {
            startActivity(HomeActivity.class);
            finish();
        }

    }

    @Override
    public void finish() {
        stopService(MusicService.class);
        super.finish();
    }

    private void initView() {
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
        iv_circle_red = (ImageView) findViewById(R.id.iv_circle_red);
        pixelWidth = 40 * getDensity();
        final List<Integer> idList = new ArrayList<Integer>();
        idList.add(R.drawable.pager_guide1);
        idList.add(R.drawable.pager_guide2);
        idList.add(R.drawable.pager_guide3);
        PagerGuideAdapter pagerGuideAdapter = new PagerGuideAdapter(this, idList);
        vp_guide.setAdapter(pagerGuideAdapter);
        //设置滑动侦听事件
        vp_guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //滑动中回调
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //向后翻转，操作当前页百分比从0.0~0.999
                //向前翻转，操作前一页百分比从0.999~0.0
                iv_circle_red.setPadding((int) (0 + position * pixelWidth + positionOffset * pixelWidth), 0, 0, 0);
            }

            //页面被选中
            @Override
            public void onPageSelected(int position) {
                //判断是否为最后一页。最后一页要显示出来tv_skip
                if (position == idList.size() - 1) {
                    tv_skip.setVisibility(View.VISIBLE);
                } else {
                    tv_skip.setVisibility(View.INVISIBLE);
                }
            }

            //滑动状态的改变
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //对skip的单击监听事件
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存是否是第一次运行程序
                getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
                copyAssets();
                if (isFromSettings) {
                    finish();
                } else {
                    startActivity(HomeActivity.class);
                    finish();
                }
            }
        });
    }

    //屏幕适配
    private float getDensity() {
        //新建尺寸信息
        DisplayMetrics metrics = new DisplayMetrics();
        //获取当前手机界面的尺寸信息
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //获取密度并返回
        return metrics.density;//密度 1.0  1.5  2.0 。。。
    }

    private void copyAssets() {
        //获取手机中的应用的存储位置
        File file = this.getFilesDir();
        File targetFile = new File(file, "commonnum.db");
        if (!DBManager.isExists(targetFile)) {
            DBManager.copyAssetsFileToFile(this, "commonnum.db", targetFile);
            //连接手机无法看文件是否复制成功，加一个吐司提示一下
            //Toast.makeText(SplashActivity.this, "文件复制成功，文件大小为：" + targetFile.length() + "字节", Toast.LENGTH_LONG).show();
        }
    }
}
