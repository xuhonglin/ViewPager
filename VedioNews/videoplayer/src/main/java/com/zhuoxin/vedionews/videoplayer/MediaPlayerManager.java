package com.zhuoxin.vedionews.videoplayer;

import android.content.Context;
import android.view.Surface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * 用来管理列表视图上视频播放，共用一个MediaPlayer
 * 本类提供三对核心方法，给UI层调用
 * onResume和onPause，初始化和释放MediaPlayer（生命周期的保证）
 * startPlayer和stopPlayer,开始和停止视频播放（提供给视图层来触发业务）
 * addPlayerBackListener和removeAllListeners:添加和移除监听（与视图交互的接口）
 */

public class MediaPlayerManager {

    private static MediaPlayerManager mediaPlayerManager;
    private Context context;
    private List<OnPlaybackListener> onPlaybackListeners;
    private MediaPlayer mediaPlayer;
    private boolean needRelease = false;//是否需要释放（如果还没设置数据源的话，release可能出现空指针的情况）
    private String videoId;//视频ID（用来区分当前在操作谁）
    private long startTime;//用于避免用户频繁操作开关视频

    private MediaPlayerManager(Context context) {
        onPlaybackListeners = new ArrayList<>();
        this.context = context;
        Vitamio.isInitialized(context);
    }

    public static MediaPlayerManager getMediaPlayerManager(Context context) {
        if (mediaPlayerManager == null) {
            mediaPlayerManager = new MediaPlayerManager(context);
        }
        return mediaPlayerManager;
    }

    //获取videoID
    public String getVideoId() {
        return videoId;
    }

    //onResume,初始化MediaPlayer
    public void onResume() {
        mediaPlayer = new MediaPlayer(context);
        //准备监听->设置缓冲大小
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.setBufferSize(512 * 1024);//单位为B
                mediaPlayer.start();
            }
        });
        //播放完监听->停止播放并且通知UI
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlayer();
            }
        });
        //监听视频大小改变->更新UI
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (width == 0 || height == 0) {
                    return;
                }
                changeVideoSize(width, height);//改变视图大小的一个方法
            }
        });
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_FILE_OPEN_OK:
                        //vitamo要做音频处理
                        mediaPlayer.audioInitedOk(mediaPlayer.audioTrackInit());
                        return true;//返回true，这个回调函数才会起作用
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        startBuffering();//缓冲开始方法
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        endBuffering();//缓冲结束方法
                        return true;
                }
                return false;
            }
        });
    }

    //onPause,释放MediaPlater????
    public void onPause() {
        stopPlayer();
        if (needRelease) {
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }

    //startPlayer，开始播放，并且更新UI（通过接口callBack）
    public void startPlayer(Surface surface, String path, String videoId) {
        //避免过于频繁的操作开关
        if (System.currentTimeMillis() - startTime < 300) {
            return;
        }
        startTime = System.currentTimeMillis();
        //当前有其他视频存在
        if (this.videoId != null) {
            stopPlayer();
        }
        //更新当前videoID
        this.videoId = videoId;
        //通知UI更新
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.OnStartPlayer(videoId);
        }
        //准备播放
        try {
            mediaPlayer.setDataSource(path);
            needRelease = true;
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //stopPlayer，停止播放,并且更新UI（通过接口callBack）
    public void stopPlayer() {
        //判断有没有视频存在
        if (this.videoId == null) {
            return;
        }
        //通知UI更新
        for (OnPlaybackListener listener:onPlaybackListeners){
            listener.OnStopPlayer(videoId);
        }
        videoId=null;
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
    }

    //开始缓冲，并且更新UI（通过接口callBack）
    private void startBuffering() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        //通知UI更新
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.OnStartBuffer(videoId);
        }
    }

    //结束缓冲，并且更新UI（通过接口callBack）
    private void endBuffering() {
        mediaPlayer.start();
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.OnStopBuffer(videoId);
        }

    }

    ////调整更改视频尺寸
    private void changeVideoSize(int width, int height) {
        //通知UI更新
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.OnSizeMeasured(videoId, width, height);
        }
    }

    //添加播放处理的监听（UI层的callback）
    public void addPlaybackListener(OnPlaybackListener listener) {
        onPlaybackListeners.add(listener);
    }

    //移除监听
    public void removePlaybackListener() {
        onPlaybackListeners.clear();
    }

    // 视图接口
    // 在视频播放模块完成播放处理, 视图层来实现此接口, 完成视图层UI更新
    public interface OnPlaybackListener {
        void OnStartBuffer(String videoId);// 视频缓冲开始

        void OnStopBuffer(String videoId); // 视频缓冲结束

        void OnStartPlayer(String videoId); // 开始播放

        void OnStopPlayer(String videoId);// 停止播放

        void OnSizeMeasured(String videoId, int width, int height);// 大小更改
    }
}
