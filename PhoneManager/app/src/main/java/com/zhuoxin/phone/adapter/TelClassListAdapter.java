package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.MyBaseAdapter;

import java.util.List;

import entity.TelclassInfo;

/**
 * Created by Administrator on 2016/11/4.
 */

public class TelClassListAdapter extends MyBaseAdapter<TelclassInfo> {
    public TelClassListAdapter(List<TelclassInfo> dataList, Context context) {
        super(dataList, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            //1、填充布局到convertView中
            convertView = inflater.inflate(R.layout.item_classlist, null);
            //2、找到holder对应的控件
            holder = new ViewHolder();
            holder.tv_classInfo = (TextView) convertView.findViewById(R.id.tv_classInfo);
            //3、把holder保存到convertView中
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //获取电话信息，并且设置到textView中
        TelclassInfo info = getItem(position);
        holder.tv_classInfo.setText(info.name);
        //把convertView返回
        return convertView;
    }

    private static class ViewHolder {
        TextView tv_classInfo;
    }
}
