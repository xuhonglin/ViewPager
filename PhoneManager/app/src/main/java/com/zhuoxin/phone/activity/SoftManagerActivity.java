package com.zhuoxin.phone.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.view.PiechartView;

import java.io.File;

public class SoftManagerActivity extends ActionBarActivity implements View.OnClickListener {
    PiechartView pv_softmgr;
    ProgressBar pb_softmgr;
    TextView tv_softmgr;
    RelativeLayout rl_allSoftware;
    RelativeLayout rl_systemSoftware;
    RelativeLayout rl_userSoftware;
    //设置一个title
    public static String softTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_manager);
        initView();
        initActionBar(true, "软件信息", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        requestPermissionAndShowMemory();
    }

    private void initView() {
        pv_softmgr = (PiechartView) findViewById(R.id.pv_softmgr);
        pb_softmgr = (ProgressBar) findViewById(R.id.pb_softmgr);
        tv_softmgr = (TextView) findViewById(R.id.tv_softmgr);
        rl_allSoftware = (RelativeLayout) findViewById(R.id.rl_allSoftware);
        rl_systemSoftware = (RelativeLayout) findViewById(R.id.rl_systemSoftware);
        rl_userSoftware = (RelativeLayout) findViewById(R.id.rl_userSoftware);
        //设置单击事件
        rl_allSoftware.setOnClickListener(this);
        rl_systemSoftware.setOnClickListener(this);
        rl_userSoftware.setOnClickListener(this);
    }

    private void requestPermissionAndShowMemory() {
        //动态申请权限
        int permissionState = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            permissionState = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionState == PackageManager.PERMISSION_GRANTED) {
                //获取完数据后展示信息
                showMemory();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //获取完数据后展示信息
            showMemory();
        } else {
            //没获取到，创建dialog，手动跳转一下
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage("请跳转到设置界面手动分配权限").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }).setNegativeButton("CANCEL", null).create();
            dialog.show();
        }

    }

    private void showMemory() {
        //获取手机中的SD卡
        File file = Environment.getExternalStorageDirectory();
        //获取总大小和已用大小
        long total = file.getTotalSpace();
        long used = total - file.getFreeSpace();
        //把数据展示出来
        int angle = (int) (360.0 * used / total);
        pv_softmgr.showPiechart(angle);
        pb_softmgr.setProgress((int) (100 * used / total));
        String totalStr = Formatter.formatFileSize(this, total);
        String freeStr = Formatter.formatFileSize(this, file.getFreeSpace());
        tv_softmgr.setText("可用空间：" + freeStr + "/" + totalStr);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.rl_allSoftware:
                softTitle = "所有软件";
                bundle.putString("appType", "all");
                startActivity(SoftwareActivity.class, bundle);
                break;
            case R.id.rl_systemSoftware:
                softTitle = "系统软件";
                bundle.putString("appType", "system");
                startActivity(SoftwareActivity.class, bundle);
                break;
            case R.id.rl_userSoftware:
                softTitle = "用户软件";
                bundle.putString("appType", "user");
                startActivity(SoftwareActivity.class, bundle);
                break;
        }
    }
}
