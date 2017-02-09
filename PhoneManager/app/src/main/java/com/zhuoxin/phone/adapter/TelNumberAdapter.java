package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.MyBaseAdapter;

import java.util.List;

import entity.TelNumberInfo;

/**
 * Created by Administrator on 2016/11/7.
 */

public class TelNumberAdapter extends MyBaseAdapter {
    public TelNumberAdapter(List dataList, Context context) {
        super(dataList, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            //1、填充布局到convertView中
            convertView = inflater.inflate(R.layout.item_numberlist, null);
            //2、找到holder对应的控件
            holder = new ViewHolder();
            holder.tv_telNumbername = (TextView) convertView.findViewById(R.id.tv_telNumbername);
            holder.tv_telNumber = (TextView) convertView.findViewById(R.id.tv_telNumber);

            //3、把holder保存到convertView中
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //获取电话信息，并且设置到textView中
        TelNumberInfo info = (TelNumberInfo) getItem(position);
        holder.tv_telNumbername.setText(info.name);
        holder.tv_telNumber.setText(info.number);

        //把convertView返回
        return convertView;
    }

    private static class ViewHolder {
        TextView tv_telNumbername;
        TextView tv_telNumber;
    }
}
