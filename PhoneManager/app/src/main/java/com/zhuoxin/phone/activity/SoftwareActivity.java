package com.zhuoxin.phone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.ActionBarActivity;

import java.util.ArrayList;
import java.util.List;

import com.zhuoxin.phone.adapter.SoftwareAdapter;

import entity.AppInfo;

import static com.zhuoxin.phone.activity.SoftManagerActivity.softTitle;

public class SoftwareActivity extends ActionBarActivity {
    //ListView、List<AppInfo>、Adapter
    ListView lv_software;
    List<AppInfo> appInfoList = new ArrayList<AppInfo>();
    SoftwareAdapter adapter;
    String appType;
    //定义加载中的进度条
    ProgressBar pb_softmgr_loading;
    //添加删除cb和btn
    CheckBox cb_deleteall;
    Button btn_delete;
    //广播接收者
    BroadcastReceiver receiver;
    //在主线程中定义一个Handle
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //处理逻辑，根据不同的msg进行处理
            int flag = msg.what;
            switch (flag) {//根据不同的标记处理不同的消息
                case 1:
                    //设置加载完数据后的进度条状态
                    pb_softmgr_loading.setVisibility(View.GONE);
                    lv_software.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    break;
            }
            return false;//如果不想让其他的handler处理，传true
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software);
        initActionBar(true, softTitle, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        //动态创建receiver，动态注册必须反注册
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //重新获取app数据并保存
                saveAppInfos();
                adapter.notifyDataSetChanged();
            }
        };
        //获取隐式跳转的数据，和Intent关系不大
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        //注册
        registerReceiver(receiver, filter);
    }

    //反注册一般放在onDestroy方法里
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initView() {
        appType = getIntent().getBundleExtra("bundle").getString("appType", "all");
        //找到加载中的进度条
        pb_softmgr_loading = (ProgressBar) findViewById(R.id.pb_softmgr_loading);
        lv_software = (ListView) findViewById(R.id.lv_software);
        cb_deleteall = (CheckBox) findViewById(R.id.cb_deleteall);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        //初始化adapter并和lv_software关联
        adapter = new SoftwareAdapter(appInfoList, this);
        lv_software.setAdapter(adapter);
        //获取手机中的信息，并存入appInfoList中
        saveAppInfos();
        cb_deleteall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //通过for循环，把数据的状态修改掉
                for (int i = 0; i < appInfoList.size(); i++) {
                    if (appType.equals("all")) {
                        if (!appInfoList.get(i).isSystem) {
                            appInfoList.get(i).isDelete = isChecked;
                        }
                    } else if (appType.equals("system")) {
                        appInfoList.get(i).isDelete = false;
                    } else {
                        appInfoList.get(i).isDelete = isChecked;
                    }

                }
                //把最新的数据给adapter，并刷新界面
                adapter.notifyDataSetChanged();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //循环取出列表中的app，如果是isDelete，就调用删除的方法，删除
                for (AppInfo info : appInfoList) {
                    if (info.isDelete) {
                        if (!info.packageName.equals(getPackageName())) {
                            //调用删除的方法
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:" + info.packageName));
                            startActivity(intent);
                        }

                    }
                }
            }
        });
    }

    private void saveAppInfos() {
        //设置访问数据时的进度条状态
        pb_softmgr_loading.setVisibility(View.VISIBLE);
        lv_software.setVisibility(View.INVISIBLE);
        //因为访问数据（文件、网络）是耗时操作。开辟子线程，避免ANR（application not responding）现象产生
        new Thread(new Runnable() {
            @Override
            public void run() {
                //上面有重新获取数据，这里要清空数据
                appInfoList.clear();
                //获取几乎所有的安装包
                List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
                //循环获取所有软件信息
                for (PackageInfo packageInfo : packageInfoList) {
                    //建立ApplicationInfo
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    //创建apptype
                    boolean apptype;
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 || (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        apptype = true;
                    } else {
                        apptype = false;
                    }
                    //创建appicon
                    Drawable appicon = getPackageManager().getApplicationIcon(applicationInfo);
                    //appname
                    String appname = (String) getPackageManager().getApplicationLabel(applicationInfo);
                    String packageName = packageInfo.packageName;
                    String appversion = packageInfo.versionName;
                    //判断当前页面要展示的数据，把数据存到appInfoList
                    if (SoftwareActivity.this.appType.equals("all")) {
                        AppInfo info = new AppInfo(appicon, appname, apptype, packageName, appversion, false);
                        appInfoList.add(info);
                    } else if (SoftwareActivity.this.appType.equals("system")) {
                        if (apptype) {
                            AppInfo info = new AppInfo(appicon, appname, apptype, packageName, appversion, false);
                            appInfoList.add(info);
                        }
                    } else {
                        if (!apptype) {
                            AppInfo info = new AppInfo(appicon, appname, apptype, packageName, appversion, false);
                            appInfoList.add(info);
                        }
                    }
                }
                //1.runOnUIThread   view.post()
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                cb_deleteall.post(new Runnable() {
                    @Override
                    public void run() {
                        cb_deleteall.setChecked(true);
                    }
                });*/
                //2.Handler机制（处理者），在子线程中设置标记，再将标记发送到主线程中
                Message message = handler.obtainMessage();
                message.what = 1;//设置标记
                //message.arg1 ;传输数据
                //message.setData();
                //message.setTarget();传输目标handler，与new Handler新建一个handler配合使用
                handler.sendMessage(message);
                //3.AsyncTask
            }
        }).start();

    }
}
