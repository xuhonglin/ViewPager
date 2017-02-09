package com.zhuoxin.mytreasure.treasure.map;

import com.zhuoxin.mytreasure.net.NetClient;
import com.zhuoxin.mytreasure.treasure.Area;
import com.zhuoxin.mytreasure.treasure.Treasure;
import com.zhuoxin.mytreasure.treasure.TreasureRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/10.
 */

//获取宝藏数据的业务类
public class MapPresenter {

    private MapMvpView mMapMvpView;
    private Area mArea;

    public MapPresenter(MapMvpView mapMvpView) {
        mMapMvpView = mapMvpView;
    }

    public void getTreasure(Area area) {

        if (TreasureRepo.getInstance().isCached(area)) {
            return;
        }

        this.mArea = area;
        Call<List<Treasure>> listCall = NetClient.getInstances().getTreasureApi().getTreasureInArea(area);
        listCall.enqueue(mListCallback);

    }

    private Callback<List<Treasure>> mListCallback = new Callback<List<Treasure>>() {

        //请求成功
        @Override
        public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {

            if (response.isSuccessful()) {
                List<Treasure> treasureList = response.body();
                if (treasureList == null) {
                    //友好的提示一下：吐司
                    mMapMvpView.showMessage("未知的错误");
                    return;
                }

                //做缓存
                TreasureRepo.getInstance().addTreasure(treasureList);
                TreasureRepo.getInstance().cache(mArea);

                //拿到数据了：给MapFragment，在地图上展示
                mMapMvpView.setData(treasureList);
            }

        }

        //请求失败
        @Override
        public void onFailure(Call<List<Treasure>> call, Throwable t) {
            //吐司说明一下
            mMapMvpView.showMessage("请求失败" + t.getMessage());
        }
    };

}
