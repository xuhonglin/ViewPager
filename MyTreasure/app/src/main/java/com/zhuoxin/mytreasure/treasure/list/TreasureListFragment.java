package com.zhuoxin.mytreasure.treasure.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuoxin.mytreasure.R;
import com.zhuoxin.mytreasure.treasure.TreasureRepo;

/**
 * Created by Administrator on 2017/1/12.
 */

//宝藏列表
public class TreasureListFragment extends Fragment {

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //创建RecyclerView
        mRecyclerView = new RecyclerView(container.getContext());

        //一定要设置它以哪种方式展示：设置布局管理器
        //LinearLayout，GridLayout，StaggerGridLayout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        //设置动画效果
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //设置背景
        mRecyclerView.setBackgroundResource(R.mipmap.scale_bg);

        return mRecyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //设置适配器和数据
        TreasureListAdapter adapter = new TreasureListAdapter();
        mRecyclerView.setAdapter(adapter);

        //数据：从缓存的数据里面拿到
        adapter.addItemData(TreasureRepo.getInstance().getTreasure());

    }
}
