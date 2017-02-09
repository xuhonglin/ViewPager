package com.zhuoxin.phone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.db.DBManager;

import java.io.File;
import java.util.List;

import com.zhuoxin.phone.adapter.TelNumberAdapter;

import entity.TelNumberInfo;

public class PhoneNumberActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    final int PERMISSION_REQUEST_CODE = 0;
    List<TelNumberInfo> dataList;
    ListView lv_numberlist;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        initView();
        initData();
    }

    private void initData() {
        //取出标题，设置标题
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String title = bundle.getString("title");
        int idx = bundle.getInt("idx", 1);
        //从数据库中读取telnumber的信息
        File targetFile = new File(getFilesDir(), "commonnum.db");
        dataList = DBManager.readTelNumberList(targetFile, idx);
        //对listview设置adapter
        TelNumberAdapter adapter = new TelNumberAdapter(dataList, this);
        lv_numberlist.setAdapter(adapter);
        //对listview设置监听事件
        lv_numberlist.setOnItemClickListener(this);
        initActionBar(true, title, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        lv_numberlist = (ListView) findViewById(R.id.lv_numberList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //单击对应条目的时候，应该获取到相应的电话号码
        number = dataList.get(position).number;
        //需要判断当前的版本号是否为6.0（23）版本
        if (Build.VERSION.SDK_INT >= 23) {
            //如果用到了敏感权限，需要动态申请runtime运行时权限
            //1、先检查自己需要的权限是否已经申请了
            int hasGot = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);
            if (hasGot == PackageManager.PERMISSION_GRANTED) {
                //权限已经具备，创建隐式intent，启动拨号程序
                call(number);
            } else {
                //2、没有权限，需要申请
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            call(number);
        }
    }

    //3、编写权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //直接处理
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限申请成功
                    call(number);
                } else {
                    //权限申请失败
                    AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("权限提示").setMessage("您可以跳转到系统界面进行权限分配").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //确认按钮的单击事件，跳转到系统的app界面
                            startActivity(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    alertDialog.show();
                    Toast.makeText(this, "权限申请失败，请您重新获取权限", Toast.LENGTH_LONG).show();
                }
                break;
        }


    }

    private void call(final String number) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("拨号提示").setMessage("请确认您是否要拨打：" + number).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确认按钮的单击事件，跳转到拨号界面
                startActivity(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        alertDialog.show();

    }
}
