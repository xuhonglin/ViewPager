package com.zhuoxin.vedionews.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import io.vov.vitamio.widget.MediaController;

/**
 * Created by Administrator on 2016/12/19.
 */

public class VideoController extends MediaController {
    private MediaPlayerControl mediaPlayerControl;//视频播放接口,用于获取视频的播放进度
    private AudioManager audioManager;//音频管理
    private Window window;//用于视频亮度管理
    private int maxVolume;//最大音量
    private int currentVolume;//当前音量
    private float currentBrightness;//当前亮度

    public VideoController(Context context) {
        super(context);
        //音频管理
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //最大音量
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //用于视频亮度管理
        window = ((Activity) context).getWindow();
    }

    //通过从写此方法，来自定义layout
    @Override
    protected View makeControllerView() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_custom_video_controller, this);
        initView(view);
        return view;
    }


    //拿到自定义视频控制器
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        super.setMediaPlayer(player);
        mediaPlayerControl = player;
    }

    //初始化视图,设置一些监听
    private void initView(View view) {
        ImageButton btnFastForward = (ImageButton) findViewById(R.id.btnFastForward);
        ImageButton btnFastRewind = (ImageButton) findViewById(R.id.btnFastRewind);
        //快进一次10秒
        btnFastForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                long position = mediaPlayerControl.getCurrentPosition();
                position += 10000;
                if (position > mediaPlayerControl.getDuration()) {
                    position = mediaPlayerControl.getDuration();
                }
                mediaPlayerControl.seekTo(position);
            }
        });
        //后退一次10秒
        btnFastRewind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                long position = mediaPlayerControl.getCurrentPosition();
                position -= 10000;
                if (position < 0) {
                    position = 0;
                }
                mediaPlayerControl.seekTo(position);
            }
        });
        //调整视图（左边调整亮度，右边调整音量）
        final View adjustView = findViewById(R.id.adjustView);
        //依赖GestureDetector（手势识别类）,来进行划屏调整音量和亮度的手势处理
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float width = adjustView.getWidth();//调整视图的宽
                float height = adjustView.getHeight();//调整视图的高
                float percent = (e1.getY() - e2.getY()) / height;//高度滑动的百分比
                //左侧：调整亮度(在屏幕左侧的1/5)
                if (e1.getX() < width / 5) {
                    adjustBrightness(percent);//调整亮度的方法
                }
                //右侧：调整音量(在屏幕右侧的1/5)
                if (e1.getX() > width * 4 / 5) {
                    adjustVolume(percent);//调整音量的方法
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        //对adjustView（调整视图）进行touch监听
        //但是，我们自己不去判断处理各种touch动用了什么，都交给gestureDetector去做
        adjustView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //当用户摁下的时候
                if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    //拿到当前音量
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    //拿到当前亮度
                    currentBrightness = window.getAttributes().screenBrightness;
                }
                gestureDetector.onTouchEvent(motionEvent);
                //在调整过程中，一直显示
                show();
                return true;
            }
        });
    }

    private void adjustVolume(float percent) {
        int volume = (int) (currentVolume + percent * maxVolume);
        volume = volume > maxVolume ? maxVolume : volume;
        volume = volume < 0 ? 0 : volume;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
    }
    //亮度0.0f~1.0f
    private void adjustBrightness(float percent) {
        float brightness = currentBrightness + percent;
        brightness = brightness > 1.0f ? 1.0f  : brightness;
        brightness = brightness < 0 ? 0 : brightness;
        WindowManager.LayoutParams layoutParams=window.getAttributes();
        layoutParams.screenBrightness=brightness;
        window.setAttributes(layoutParams);
    }
}