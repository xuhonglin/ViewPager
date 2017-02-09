package com.zhuoxin.vedionews.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhuoxin.vedionews.R;
import com.zhuoxin.vedionews.UserManager;
import com.zhuoxin.vedionews.activity.CommentsActivity;
import com.zhuoxin.vedionews.base.BaseItemView;
import com.zhuoxin.vedionews.entity.NewsEntity;
import com.zhuoxin.vedionews.utils.CommonUtils;
import com.zhuoxin.vedionews.videoplayer.MediaPlayerManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新闻列表的单项视图，将使用MediaPlayer播放视频，TextureView来显示视频
 */
public class NewsItemView extends BaseItemView<NewsEntity> implements TextureView.SurfaceTextureListener, MediaPlayerManager.OnPlaybackListener {

    @BindView(R.id.textureView)
    TextureView textureView; // 用来展现视频的TextureView
    @BindView(R.id.ivPreview)
    ImageView ivPreview;
    @BindView(R.id.tvNewsTitle)
    TextView tvNewsTitle;
    @BindView(R.id.tvCreatedAt)
    TextView tvCreatedAt;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ivPlay)
    ImageView ivPlay;

    private NewsEntity newsEntity;
    private MediaPlayerManager mediaPlayerManager;
    private Surface surface;

    public NewsItemView(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_news, this, true);
        ButterKnife.bind(this);
        //添加列表视频播放控制相关监听
        mediaPlayerManager = MediaPlayerManager.getMediaPlayerManager(getContext());
        mediaPlayerManager.addPlaybackListener(this);
        //textureView -> surface相关监听
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    protected void bindModel(NewsEntity newsEntity) {
        this.newsEntity = newsEntity;
        //初始化视图状态
        tvNewsTitle.setVisibility(View.VISIBLE);
        ivPreview.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.VISIBLE);
        //设置标题，创建时间，预览图
        tvNewsTitle.setText(newsEntity.getNewsTitle());
        tvCreatedAt.setText(CommonUtils.format(newsEntity.getCreatedAt()));
        //设置预览图像（Picasso）,服务器返回带中文的图片地址需要转换
        String url = CommonUtils.encodeUrl(newsEntity.getPreviewUrl());
        Picasso.with(getContext()).load(url).into(ivPreview);
    }

    //点击事件，跳转到评论页面
    @OnClick(R.id.tvCreatedAt)
    public void navigateToComments() {
        // 跳转到评论页面
        CommentsActivity.open(getContext(), newsEntity);
    }

    //点击预览图，开始播放
    @OnClick(R.id.ivPreview)
    public void startPlayer() {
        if (surface == null) return;
        //因为viewpager有缓存机制，需要控制视频的停止
        UserManager.getInstance().setPlay(true);

        String path = newsEntity.getVideoUrl();
        String videoId = newsEntity.getObjectId();
        if (mediaPlayerManager == null) {
            Log.e("aaa", "为空！");
        }
        mediaPlayerManager.startPlayer(surface, path, videoId);
    }

    //点击视频,停止播放
    @OnClick(R.id.textureView)
    public void stopPlayer() {
        mediaPlayerManager.stopPlayer();
    }

    // 判断是否操作当前的视频
    private boolean isCurrentVideo(String videoId) {
        if (videoId == null || newsEntity == null) return false;
        return videoId.equals(newsEntity.getObjectId());
    }

    //textureView -> surface相关监听
    //拿到Surface
    //当TextureView SurfaceTexture准备使用。
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = new Surface(surface);
    }

    //当SurfaceTexture的缓冲区大小改变。
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    //当surface销毁时，停止播放
    //当指定的SurfaceTexture即将被摧毁。
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        this.surface.release();
        this.surface = null;
        // 停止自己
        if (newsEntity.getObjectId().equals(mediaPlayerManager.getVideoId())) {
            mediaPlayerManager.stopPlayer();
        }
        return false;
    }

    //当指定SurfaceTexture通过updateTexImage()更新。
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }


    //添加列表视频播放控制相关监听
    @Override
    public void OnStartBuffer(String videoId) {
        if (isCurrentVideo(videoId)) {
            //将当前视频的prb显示出来
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnStopBuffer(String videoId) {
        if (isCurrentVideo(videoId)) {
            //将当前视频的prb隐藏
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void OnStartPlayer(String videoId) {
        if (isCurrentVideo(videoId)) {
            tvNewsTitle.setVisibility(View.INVISIBLE);
            ivPreview.setVisibility(View.INVISIBLE);
            ivPlay.setVisibility(View.INVISIBLE);
            // TODO: 2016/12/26 0026 !!!!!!!!!!!!!!!
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void OnStopPlayer(String videoId) {
        if (isCurrentVideo(videoId)) {
            tvNewsTitle.setVisibility(View.VISIBLE);
            ivPreview.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
            // TODO: 2016/12/26 0026 !!!!!!!!!!!!!!!
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void OnSizeMeasured(String videoId, int width, int height) {
        if (isCurrentVideo(videoId)) {
            //无需求，不做处理
        }
    }
}
