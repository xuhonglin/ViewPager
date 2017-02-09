package com.zhuoxin.phone.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhuoxin.phone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class PagerGuideAdapter extends PagerAdapter {
    List<Integer> idList = new ArrayList<Integer>();
    LayoutInflater inflater;

    public PagerGuideAdapter(Context context, List<Integer> idList) {
        inflater = LayoutInflater.from(context);
        this.idList.addAll(idList);
    }

    //返回总数
    @Override
    public int getCount() {
        return idList.size();
    }

    //创建一个view，并且把它的view进行设置返回
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //创建一个view
        View view = inflater.inflate(R.layout.layout_pager_guide, null);
        ImageView iv_guide = (ImageView) view.findViewById(R.id.iv_guide);
        iv_guide.setImageResource(idList.get(position));
        //添加到容器container中，负责对view的判断和显示
        container.addView(view);
        //把当前的view作为key返回
        return view;
    }

    //判断当前页面的键值对是否相同
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //销毁项目
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
