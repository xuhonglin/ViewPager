package com.zhuoxin.vedionews;

import android.app.Application;

import com.zhuoxin.vedionews.utils.ToastUtils;


/**
 * Created by Administrator on 2016/12/21.
 */

public class VideoNewsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化吐司工具类
        ToastUtils.init(this);
    }
}
