package com.zhuoxin.phone.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.base.MyBaseAdapter;

import java.util.List;

import entity.AppInfo;

/**
 * Created by Administrator on 2016/11/18.
 */

public class RocketAdapter extends MyBaseAdapter<ActivityManager.RunningAppProcessInfo> {
    public RocketAdapter(List<ActivityManager.RunningAppProcessInfo> dataList, Context context) {
        super(dataList, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_softwarelist, null);
            holder = new ViewHolder();
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_appicon);
            holder.tv_appname = (TextView) convertView.findViewById(R.id.tv_appname);
            holder.tv_packagename = (TextView) convertView.findViewById(R.id.tv_packageName);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb_delete);
            holder.tv_appversion = (TextView) convertView.findViewById(R.id.tv_appversion);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置显示状态
        holder.cb.setVisibility(View.GONE);
        holder.tv_appversion.setVisibility(View.GONE);
        //对控件进行赋值
        try {
            holder.iv_icon.setImageDrawable(context.getPackageManager().getApplicationIcon(getItem(position).processName));
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(getItem(position).processName, PackageManager.MATCH_UNINSTALLED_PACKAGES);
            holder.tv_appname.setText(context.getPackageManager().getApplicationLabel(applicationInfo));
            holder.tv_packagename.setText(getItem(position).processName);
        } catch (PackageManager.NameNotFoundException e) {
            //万一包名与进程的名字不相同，赋值一些默认的值
            holder.iv_icon.setImageResource(R.drawable.item_arrow_right);
            holder.tv_appname.setText("未知程序");
            holder.tv_packagename.setText(getItem(position).processName);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_appname;
        TextView tv_packagename;
        CheckBox cb;
        TextView tv_appversion;
    }
}
