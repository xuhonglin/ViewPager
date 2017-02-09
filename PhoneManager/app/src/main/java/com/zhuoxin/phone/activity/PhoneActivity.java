package com.zhuoxin.phone.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.db.DBManager;

import java.io.File;
import java.util.List;

import com.zhuoxin.phone.adapter.TelClassListAdapter;

import entity.TelclassInfo;

public class PhoneActivity extends ActionBarActivity {
    ListView lv_classlist;
    List<TelclassInfo> telclassInfoList;
    TelClassListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        initData();
        initView();
        initActionBar(true, "电话大全", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        //获取手机中的应用的存储位置
        File file = this.getFilesDir();
        File targetFile = new File(file, "commonnum.db");
        telclassInfoList = DBManager.readTelClassList(this, targetFile);
        adapter = new TelClassListAdapter(telclassInfoList, this);
    }

    private void initView() {
        lv_classlist = (ListView) findViewById(R.id.lv_classlist);
        lv_classlist.setAdapter(adapter);
        //对listview设置单击事件
        lv_classlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //传入数据
                Bundle bundle = new Bundle();
                bundle.putInt("idx", telclassInfoList.get(position).idx);
                bundle.putString("title", telclassInfoList.get(position).name);
                startActivity(PhoneNumberActivity.class, bundle);
            }
        });
    }
}
