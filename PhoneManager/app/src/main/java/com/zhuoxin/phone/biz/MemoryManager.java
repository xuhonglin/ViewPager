package com.zhuoxin.phone.biz;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;

import com.zhuoxin.phone.process.ProcessManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/21.
 * 这个类是用来获取关于运行内存的相关信息
 * 工具类中一般为静态方法
 *
 * @author 许红琳
 * @version 1.0
 * @since 2016年11月21日10:26:31
 */

public class MemoryManager {
    /**
     * 获取MemoryInfo
     *
     * @param context
     * @return
     */
    private static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        //获取ActivityManager
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //创建MemoryInfo
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //从系统中获取数据，并保存到memoryInfo中
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    /**
     * 获取当前手机的总共运行内存大小，单位为bit位
     *
     * @param context 上下文环境
     * @return long类型的运行内存总大小
     */
    public static long totalRAMLong(Context context) {
        //取出数据
        return getMemoryInfo(context).totalMem;
    }

    /**
     * 获取当前手机的总共运行内存大小，单位为bit位
     *
     * @param context 上下文环境
     * @return String类型的运行内存总大小
     */
    public static String totalRAMString(Context context) {
        //取出数据
        return Formatter.formatFileSize(context, totalRAMLong(context));
    }

    /**
     * 获取当前手机的可用内存大小，单位为bit位
     *
     * @param context 上下文环境
     * @return long类型的可用内存总大小
     */
    public static long availableRAMLong(Context context) {
        return getMemoryInfo(context).availMem;
    }

    /**
     * 获取当前手机的可用内存大小，单位为bit位
     *
     * @param context 上下文环境
     * @return String类型的可用内存总大小
     */
    public static String availableRAMString(Context context) {
        return Formatter.formatFileSize(context, availableRAMLong(context));
    }

    /**
     * 获取当前手机的已用内存大小，单位为bit位
     *
     * @param context 上下文环境
     * @return long类型的已用内存总大小
     */
    public static long usedRAMLong(Context context) {
        return totalRAMLong(context) - availableRAMLong(context);
    }

    /**
     * 获取当前手机的已用内存大小，单位为bit位
     *
     * @param context 上下文环境
     * @return String类型的已用内存总大小
     */
    public static String usedRAMString(Context context) {
        return Formatter.formatFileSize(context, usedRAMLong(context));
    }

    /**
     * 获取当前手机已用内存占运行内存总大小的百分比
     *
     * @param context 上下文环境
     * @return 百分比
     */
    public static int usedPercent(Context context) {
        return (int) (100.0 * usedRAMLong(context) / totalRAMLong(context));
    }

    //获取进程
    public static List<ActivityManager.RunningAppProcessInfo> getRunning(Context context) {
        List<ActivityManager.RunningAppProcessInfo> rapi = ProcessManager.getRunningAppProcessInfo(context);
        List<ActivityManager.RunningAppProcessInfo> tempInfos = new ArrayList<ActivityManager.RunningAppProcessInfo>();
        for (int i = 0; i < rapi.size(); i++) {
            if (!rapi.get(i).processName.contains("android")) {
                Log.e("进程信息", rapi.get(i).processName);
                tempInfos.add(rapi.get(i));
            }
        }
        return tempInfos;
    }

    //杀死进程
    public static void killRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = getRunning(context);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).processName.equals(context.getPackageName())) {
                am.killBackgroundProcesses(list.get(i).processName);
            }
        }
    }

    //获取内置SD卡的路径
    public static String getPhoneInSDCardPath() {
        //1、获取SD卡状态、判断是否有SD卡
        String sdcardState = Environment.getExternalStorageState();
        //2、如果有，获取路径
        if (sdcardState.equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    //获取外置SD卡的路径
    public static String getPhoneOutSDCardPath() {
        //1、获取外置SD卡状态、判断是否有外置SD卡  "SECONDARY_STORAGE"
        Map<String, String> sdMap = System.getenv();//返回环境
        if (sdMap.containsKey("SECONDARY_STORAGE")) {//判断键是否存在（因为键是唯一的）
            //2、如果有，取出路径
            String paths = sdMap.get("SECONDARY_STORAGE");//取值，包括外置SD卡的信息，以“：”分割不同的信息
            String path = paths.split(":")[0];//取真正的值
            if (path == null) {
                return null;
            } else {
                return path;
            }
        } else {
            return null;
        }
    }
}
