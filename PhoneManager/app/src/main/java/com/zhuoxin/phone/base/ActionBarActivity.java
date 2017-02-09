package com.zhuoxin.phone.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.view.ActionBarView;

/**
 * Created by Administrator on 2016/11/10.
 */

public class ActionBarActivity extends BaseActivity {
    //提取ActionBar
    ActionBarView actionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initActionBar(boolean hasBack, String title, boolean hasMenu, View.OnClickListener listener) {
        //这里要求继承自ActionBarActivity的子类布局中必须有R.id.actionbar，否则会崩溃
        actionBarView = (ActionBarView) findViewById(R.id.actionbar);
        actionBarView.initActionBar(hasBack, title, hasMenu, listener);
    }
}
