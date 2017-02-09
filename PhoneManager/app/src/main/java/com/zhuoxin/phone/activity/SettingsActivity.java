package com.zhuoxin.phone.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.ActionBarActivity;

public class SettingsActivity extends ActionBarActivity implements View.OnClickListener {
    RelativeLayout rl_start;
    RelativeLayout rl_notification;
    RelativeLayout rl_push;
    RelativeLayout rl_help;
    RelativeLayout rl_aboutus;
    ToggleButton tb_start;
    ToggleButton tb_notification;
    ToggleButton tb_push;
    //设置一个成员变量的环境,在设置tb_notification监听时使用
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initActionBar(true, "系统设置", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        initData();
        //初始化一下
        mContext = this;
    }

    private void initView() {
        rl_start = (RelativeLayout) findViewById(R.id.rl_start);
        rl_notification = (RelativeLayout) findViewById(R.id.rl_notification);
        rl_push = (RelativeLayout) findViewById(R.id.rl_push);
        rl_help = (RelativeLayout) findViewById(R.id.rl_help);
        rl_aboutus = (RelativeLayout) findViewById(R.id.rl_aboutus);
        tb_start = (ToggleButton) findViewById(R.id.tb_start);
        //状态改变时改变选中的状态
        tb_start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //把状态保存到config文件中
                getSharedPreferences("config", MODE_PRIVATE).edit().putBoolean("startWhenBootComplete", tb_start.isChecked()).commit();

            }
        });
        tb_notification = (ToggleButton) findViewById(R.id.tb_notification);
        tb_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //状态选中时，显示通知栏消息；未选中时，清空通知栏消息
                if (isChecked) {
                    Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new NotificationCompat.Builder(SettingsActivity.this)
                            .setContentTitle("虚假通知")
                            .setContentText("这是一条我捏造的虚假通知")
                            .setSmallIcon(R.drawable.item_arrow_right)
                            .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.item_arrow_right))
                            .setAutoCancel(true)//自动取消
                            .setContentIntent(pendingIntent)//点击内容跳转
                            .build();
                    NotificationManager manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(0, notification);
                } else {
                    NotificationManager manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                    //和if里面的编号一致
                    manager.cancel(0);
                }
            }
        });
        tb_push = (ToggleButton) findViewById(R.id.tb_push);

        //设置单击监听事件
        rl_start.setOnClickListener(this);
        rl_notification.setOnClickListener(this);
        rl_push.setOnClickListener(this);
        rl_help.setOnClickListener(this);
        rl_aboutus.setOnClickListener(this);
    }

    private void initData() {
        boolean startWhenBootComplete = getSharedPreferences("config", MODE_PRIVATE).getBoolean("startWhenBootComplete", false);
        tb_start.setChecked(startWhenBootComplete);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_start:
                //开机启动按钮，去设置对应的togglebutton，并且把开机启动的状态进行保存
                //切换状态
                tb_start.setChecked(!tb_start.isChecked());
                break;
            case R.id.rl_notification:
                //切换状态
                tb_notification.setChecked(!tb_notification.isChecked());
                break;
            case R.id.rl_push:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("这是一个进度条对话框");
                dialog.show();
                Toast.makeText(this, "后续版本见", Toast.LENGTH_LONG).show();
                break;
            case R.id.rl_help:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFromSettings", true);
                startActivity(GuideActivity.class, bundle);
                break;
            case R.id.rl_aboutus:
                Toast.makeText(this, "现在还未推出", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
