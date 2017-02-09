package com.zhuoxin.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zhuoxin.phone.service.MusicService;

public class RebootCompleteReceiver extends BroadcastReceiver {
    //接收到广播后，执行的操作
    @Override
    public void onReceive(Context context, Intent intent) {
        //只有选中了开机启动的选项时，才会执行以下操作
        boolean start = context.getSharedPreferences("config", context.MODE_PRIVATE).getBoolean("startWhenBootComplete", false);
        //我是外接了自己的华为手机，需要增加以下获取权限的操作，在清单文件中也有相应的操作
        final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
        if (start) {
            if (ACTION_BOOT.equals(intent.getAction())) {
                Toast.makeText(context, "重启成功", Toast.LENGTH_LONG).show();
                Intent musicIntent = new Intent();
                musicIntent.setClass(context, MusicService.class);
                context.startService(musicIntent);
            }
        }
    }
}
