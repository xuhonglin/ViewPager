package com.zhuoxin.phone.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static android.R.attr.targetClass;

/**
 * Created by Administrator on 2016/11/10.
 */

public class BaseActivity extends AppCompatActivity {
    //重载一些startActivity的方法
    public void startActivity(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
    }

    public void startActivity(Class targetClass, Bundle bundle) {
        Intent intent = new Intent(this, targetClass);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    public void startActivity(String action, Uri data) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setData(data);
        startActivity(intent);
    }

    //开启服务
    public void startService(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startService(intent);
    }

    //关闭服务
    public void stopService(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        stopService(intent);
    }
}
