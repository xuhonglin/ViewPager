package com.zhuoxin.mytreasure.treasure.hide;

/**
 * Created by Administrator on 2017/1/12.
 */

public interface HideTreasureView {

    //宝藏上传中视图的交互
    void showProgress();

    void hideProgres();

    void showMessage(String msg);

    void navigationToHome();

}
