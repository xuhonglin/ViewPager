package com.zhuoxin.phone.biz;

import com.zhuoxin.phone.utils.FileTypeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import entity.FileInfo;

/**
 * 查找文件，使用了单例模式
 * Created by Administrator on 2016/11/22.
 */

public class FileManager {
    //设计单例模式，饿汉式
    private static FileManager fileManager = new FileManager();

    private FileManager() {

    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    //内置、外置SD卡路径
    public static File inSDCardDir = null;
    public static File outSDCardDir = null;

    //静态代码块初始化成员变量
    static {
        if (MemoryManager.getPhoneInSDCardPath() != null) {
            //判断是否有内置SD卡
            inSDCardDir = new File(MemoryManager.getPhoneInSDCardPath());
        }
        if (MemoryManager.getPhoneOutSDCardPath() != null) {
            //判断是否有外置SD卡
            outSDCardDir = new File(MemoryManager.getPhoneOutSDCardPath());
        }
    }

    //设置查找状态，判断是否停止
    public boolean isSearching = false;
    //建立所有文件的List列表，存储查到的文件，文件总大小
    List<FileInfo> anyFileList = new ArrayList<FileInfo>();
    long anyFileSize = 0;//所有文件的大小
    List<FileInfo> txtFileList = new ArrayList<FileInfo>();
    long txtFileSize = 0;//文本文件的大小
    List<FileInfo> videoFileList = new ArrayList<FileInfo>();
    long videoFileSize = 0;//视频文件的大小
    List<FileInfo> audioFileList = new ArrayList<FileInfo>();
    long audioFileSize = 0;//音频文件的大小
    List<FileInfo> imageFileList = new ArrayList<FileInfo>();
    long imageFileSize = 0;//图像文件的大小
    List<FileInfo> zipFileList = new ArrayList<FileInfo>();
    long zipFileSize = 0;//压缩文件的大小
    List<FileInfo> apkFileList = new ArrayList<FileInfo>();
    long apkFileSize = 0;//apk文件的大小

    public List<FileInfo> getAnyFileList() {
        return anyFileList;
    }

    public void setAnyFileList(List<FileInfo> anyFileList) {
        this.anyFileList = anyFileList;
    }

    public long getAnyFileSize() {
        return anyFileSize;
    }

    public void setAnyFileSize(long anyFileSize) {
        this.anyFileSize = anyFileSize;
    }

    public List<FileInfo> getTxtFileList() {
        return txtFileList;
    }

    public void setTxtFileList(List<FileInfo> txtFileList) {
        this.txtFileList = txtFileList;
    }

    public long getTxtFileSize() {
        return txtFileSize;
    }

    public void setTxtFileSize(long txtFileSize) {
        this.txtFileSize = txtFileSize;
    }

    public List<FileInfo> getVideoFileList() {
        return videoFileList;
    }

    public void setVideoFileList(List<FileInfo> videoFileList) {
        this.videoFileList = videoFileList;
    }

    public long getVideoFileSize() {
        return videoFileSize;
    }

    public void setVideoFileSize(long videoFileSize) {
        this.videoFileSize = videoFileSize;
    }

    public List<FileInfo> getAudioFileList() {
        return audioFileList;
    }

    public void setAudioFileList(List<FileInfo> audioFileList) {
        this.audioFileList = audioFileList;
    }

    public long getAudioFileSize() {
        return audioFileSize;
    }

    public void setAudioFileSize(long audioFileSize) {
        this.audioFileSize = audioFileSize;
    }

    public List<FileInfo> getImageFileList() {
        return imageFileList;
    }

    public void setImageFileList(List<FileInfo> imageFileList) {
        this.imageFileList = imageFileList;
    }

    public long getImageFileSize() {
        return imageFileSize;
    }

    public void setImageFileSize(long imageFileSize) {
        this.imageFileSize = imageFileSize;
    }

    public List<FileInfo> getZipFileList() {
        return zipFileList;
    }

    public void setZipFileList(List<FileInfo> zipFileList) {
        this.zipFileList = zipFileList;
    }

    public long getZipFileSize() {
        return zipFileSize;
    }

    public void setZipFileSize(long zipFileSize) {
        this.zipFileSize = zipFileSize;
    }

    public List<FileInfo> getApkFileList() {
        return apkFileList;
    }

    public void setApkFileList(List<FileInfo> apkFileList) {
        this.apkFileList = apkFileList;
    }

    public long getApkFileSize() {
        return apkFileSize;
    }

    public void setApkFileSize(long apkFileSize) {
        this.apkFileSize = apkFileSize;
    }

    private void initData() {
        isSearching = true;
        //建立所有文件的List列表，存储查到的文件，文件总大小
        anyFileList.clear();
        anyFileSize = 0;//所有文件的大小
        txtFileList.clear();
        txtFileSize = 0;//文本文件的大小
        videoFileList.clear();
        videoFileSize = 0;//视频文件的大小
        audioFileList.clear();
        audioFileSize = 0;//音频文件的大小
        imageFileList.clear();
        imageFileSize = 0;//图像文件的大小
        zipFileList.clear();
        zipFileSize = 0;//压缩文件的大小
        apkFileList.clear();
        apkFileSize = 0;//apk文件的大小
    }

    //查找文件
    public void searchSDCardFile() {
        //如果正在搜索，则返回
        if (isSearching) {
            return;//正在查询，则返回，不做
        } else {
            isSearching = true;
            //判断anyFileList是否有数据，清空重新查找
            if (anyFileList.size() > 0) {
                initData();

            }
            //调用查找的方法，分别查找内置、外置SD卡中的文件
            searchFile(inSDCardDir, true);
            searchFile(outSDCardDir, false);
        }
    }

    //根据指定文件夹来查找内容
    public void searchFile(File file, boolean endFlag) {
        //判断文件是否合适
        if (file == null || !file.canRead() || !file.exists()) {
            return;
        }
        //递归调用来查询，如果是文件夹，递归；如果是文件，执行操作
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            if (files != null || files.length > 0) {
                for (File f : files) {
                    searchFile(f, false);
                }
            } else {
                return;
            }
            //结束的时候调用
            if (endFlag) {
                isSearching = false;
                if (listener != null) {
                    listener.end(endFlag);
                }
            }

        } else {
            //判断文件类型和图标,0->图片名字，1->图片类型
            String iconAndType[] = FileTypeUtil.getFileIconAndTypeName(file);
            //new一个FileInfo，存储大小
            FileInfo fileInfo = new FileInfo(file, iconAndType[0], iconAndType[1]);
            //所有文件
            anyFileList.add(fileInfo);
            anyFileSize += file.length();
            //判断当前文件到底是什么类型，存储到对应类型的list中
            if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_TXT)) {
                txtFileList.add(fileInfo);
                txtFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_VIDEO)) {
                videoFileList.add(fileInfo);
                videoFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_AUDIO)) {
                audioFileList.add(fileInfo);
                audioFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_IMAGE)) {
                imageFileList.add(fileInfo);
                imageFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_ZIP)) {
                zipFileList.add(fileInfo);
                zipFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_APK)) {
                apkFileList.add(fileInfo);
                apkFileSize += file.length();
            }
            //如果有listner，则每查找到一个文件，就调用它的查找方法，用来更新UI界面
            if (listener != null) {
                listener.searching(anyFileSize);
            }
        }
    }

    //回调接口
    public interface SearchListener {
        void searching(long size);//每查找一个文件来调用一个方法

        void end(boolean isExceptionEnd);//查找完后也调用方法
    }

    //定义接口对象
    public SearchListener listener = null;

    //定义设置接口的方法
    public void setSearchListener(SearchListener listener) {
        this.listener = listener;
    }
}
