package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.MyBaseAdapter;

import java.util.List;

import entity.AppInfo;

/**
 * Created by Administrator on 2016/11/11.
 */

public class SoftwareAdapter extends MyBaseAdapter<AppInfo> {
    public SoftwareAdapter(List<AppInfo> dataList, Context context) {
        super(dataList, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_softwarelist, null);
            holder = new ViewHolder();
            holder.iv_appicon = (ImageView) convertView.findViewById(R.id.iv_appicon);
            holder.tv_appname = (TextView) convertView.findViewById(R.id.tv_appname);
            holder.tv_packageName = (TextView) convertView.findViewById(R.id.tv_packageName);
            holder.tv_appversion = (TextView) convertView.findViewById(R.id.tv_appversion);
            holder.cb_appdelete = (CheckBox) convertView.findViewById(R.id.cb_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.cb_appdelete.setTag(position);
        holder.iv_appicon.setImageDrawable(getItem(position).appicon);
        holder.tv_appname.setText((CharSequence) getItem(position).appname);
        holder.tv_packageName.setText((CharSequence) getItem(position).packageName);
        holder.tv_appversion.setText(getItem(position).appversion);
        //如果是系统的app，禁用checkbox
        if (getItem(position).isSystem) {
            holder.cb_appdelete.setClickable(false);
        } else {
            holder.cb_appdelete.setClickable(true);
        }
        holder.cb_appdelete.setChecked(getItem(position).isDelete);
        holder.cb_appdelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int index = (int) holder.cb_appdelete.getTag();
                getItem(index).isDelete = isChecked;
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_appicon;
        TextView tv_appname;
        TextView tv_packageName;
        TextView tv_appversion;
        CheckBox cb_appdelete;
    }
}
