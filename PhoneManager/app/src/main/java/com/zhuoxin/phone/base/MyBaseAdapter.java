package com.zhuoxin.phone.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    //1、创建List和LayoutInflater
    List<T> dataList = new ArrayList<T>();
    public LayoutInflater inflater;
    public Context context;

    //2、构造函数初始化数据
    public MyBaseAdapter(List<T> dataList, Context context) {
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    //3、添加数据
    public void setDataList(List<T> dataList) {
        //要先清空数据，再添加数据
        this.dataList.clear();
        this.dataList.addAll(dataList);
    }

    //获得数据
    public List<T> getDataList() {
        return dataList;
    }

    //4、重写四大方法，前三个方法在这写清楚，最后一个方法定义为抽象方法
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
