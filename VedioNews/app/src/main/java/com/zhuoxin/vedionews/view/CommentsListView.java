package com.zhuoxin.vedionews.view;

import android.content.Context;
import android.util.AttributeSet;

import com.zhuoxin.vedionews.base.BaseResourceView;
import com.zhuoxin.vedionews.bombapi.BombConst;
import com.zhuoxin.vedionews.entity.CommentsEntity;
import com.zhuoxin.vedionews.entity.InQuery;
import com.zhuoxin.vedionews.entity.QueryResult;

import retrofit2.Call;

/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class CommentsListView extends BaseResourceView<CommentsEntity,CommentsItemView> {
    public CommentsListView(Context context) {
        super(context);
    }

    public CommentsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String newsId;

    public void setNewsId(String newsId){
        this.newsId = newsId;
    }

    @Override
    protected Call<QueryResult<CommentsEntity>> queryData(int limit, int skip) {
        InQuery where = new InQuery(BombConst.FIELD_NEWS, BombConst.TABLE_NEWS,newsId);
        return newsApi.getComments(limit,skip,where);
    }

    @Override
    protected int getLimit() {
        return 20;
    }

    @Override
    protected CommentsItemView createItemView() {
        return new CommentsItemView(getContext());
    }
}
