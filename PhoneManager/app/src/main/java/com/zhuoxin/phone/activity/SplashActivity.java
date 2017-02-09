package com.zhuoxin.phone.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.BaseActivity;
import com.zhuoxin.phone.db.DBManager;

import java.io.File;

public class SplashActivity extends BaseActivity {
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv = (ImageView) findViewById(R.id.iv_logo);
        startAnimation();
        copyAssets();
    }

    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_set);
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Toast.makeText(SplashActivity.this, "动画结束，正在进行跳转", Toast.LENGTH_SHORT).show();
                startActivity(PhoneActivity.class);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        animation.setAnimationListener(animationListener);
        iv.startAnimation(animation);
    }

    private void copyAssets() {
        //获取手机中的应用的存储位置
        File file = this.getFilesDir();
        File targetFile = new File(file, "commonnum.db");
        if (!DBManager.isExists(targetFile)) {
            DBManager.copyAssetsFileToFile(this, "commonnum.db", targetFile);
            //连接手机无法看文件是否复制成功，加一个吐司提示一下
            Toast.makeText(SplashActivity.this, "文件复制成功，文件大小为：" + targetFile.length() + "字节", Toast.LENGTH_LONG).show();
        }
    }
}
