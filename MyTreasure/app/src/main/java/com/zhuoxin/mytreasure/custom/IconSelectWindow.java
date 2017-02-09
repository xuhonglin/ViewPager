package com.zhuoxin.mytreasure.custom;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.zhuoxin.mytreasure.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/12.
 */

//用户头像点击弹出的视图（视图窗口：从相册、从相机、取消）
public class IconSelectWindow extends PopupWindow {

    //接口：利用接口回调的方式实现布局里面相册相机两个按钮的事件
    public interface Listener {

        //到相册
        void toGallery();

        //到相机
        void toCamera();

    }

    private Activity mActivity;
    private Listener mListener;

    //构造方法填充布局
    public IconSelectWindow(@NonNull Activity activity, Listener listener) {
        super(activity.getLayoutInflater().inflate(R.layout.window_select_icon, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this, getContentView());

        mActivity = activity;
        mListener = listener;

        setFocusable(true);//获得焦点

        //一定要设置背景
        setBackgroundDrawable(new BitmapDrawable());

    }

    //对外提供展示的方法
    public void show() {
        //在哪个位置上展示
        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @OnClick({R.id.btn_gallery, R.id.btn_camera, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            //通过相册添加
            case R.id.btn_gallery:

                //跳转到相册去处理
                mListener.toGallery();

                break;
            //打开相机拍照
            case R.id.btn_camera:

                //打开相机
                mListener.toCamera();

                break;
            //取消
            case R.id.btn_cancel:
                break;
        }
        dismiss();//消失
    }
}
