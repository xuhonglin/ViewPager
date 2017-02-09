package com.zhuoxin.vedionews.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zhuoxin.vedionews.R;
import com.zhuoxin.vedionews.fragment.LikesFragment;
import com.zhuoxin.vedionews.fragment.LocalVideoFragment;
import com.zhuoxin.vedionews.fragment.NewsFragment;
import com.zhuoxin.vedionews.videoplayer.SimpleVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.btnLikes)
    Button btnLikes;
    @BindView(R.id.btnLocal)
    Button btnLocal;
    @BindView(R.id.btnNews)
    Button btnNews;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        viewPager.setAdapter(adapter);
        //viewPager监听->Button的切换
        viewPager.addOnPageChangeListener(listener);
        //首次进入默认选中在线新闻btn
        btnNews.setSelected(true);
    }

    //viewPager适配器
    private FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NewsFragment();
                case 1:
                    return new LocalVideoFragment();
                case 2:
                    return new LikesFragment();
                default:
                    throw new RuntimeException("未知错误");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    //viewPager监听->Button的切换,按钮的选中状态
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            btnNews.setSelected(position == 0);
            btnLocal.setSelected(position == 1);
            btnLikes.setSelected(position == 2);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    //button点击事件
    @OnClick({R.id.btnNews, R.id.btnLocal, R.id.btnLikes})
    public void chooseFragment(Button button) {
        switch (button.getId()) {
            //不要平滑效果，第二个参数传false
            case R.id.btnNews:
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.btnLocal:
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.btnLikes:
                viewPager.setCurrentItem(2, false);
                break;
            default:
                throw new RuntimeException("未知错误");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
