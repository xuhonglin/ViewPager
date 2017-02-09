package com.zhuoxin.phone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.zhuoxin.phone.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/18.
 */

public class CleanCircleView extends View {
    //旋转的角度
    int currentAngle = 90;
    int width;
    int height;
    RectF oval;
    boolean isBack;
    public static boolean isRunning;

    public CleanCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (width < height) {
            height = width;
        } else {
            width = height;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        if (Build.VERSION.SDK_INT >= 23) {
            paint.setColor(getResources().getColor(R.color.bluePrimaryColor, null));
        }
        oval = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawArc(oval, -90, currentAngle, true, paint);

    }

    public void setTargetAngle(final int targetAngle) {
        isBack = true;
        //先倒退，定义计时器来执行
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (isBack) {
                    if (currentAngle > 0) {
                        currentAngle -= 4;
                    } else {
                        currentAngle = 0;
                        isBack = false;
                    }
                    postInvalidate();
                } else {
                    if (currentAngle < targetAngle) {
                        currentAngle += 4;
                    } else {
                        currentAngle = targetAngle;
                        isRunning = false;
                        timer.cancel();
                    }
                    postInvalidate();
                }
            }
        };
        timer.schedule(task, 40, 40);
    }
}
