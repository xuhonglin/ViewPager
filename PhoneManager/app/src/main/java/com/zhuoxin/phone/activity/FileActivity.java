package com.zhuoxin.phone.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.adapter.FileAdapter;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.biz.FileManager;
import com.zhuoxin.phone.utils.FileTypeUtil;

import java.util.ArrayList;
import java.util.List;

import entity.FileInfo;

public class FileActivity extends ActionBarActivity {
    //String files[] = {"所有文件", "文档文件", "视频文件", "音频文件", "图像文件", "压缩文件", "apk文件"};
    String fileType;
    //获取数据
    ListView lv_file;
    FileAdapter fileAdapter;
    List<FileInfo> fileInfoList;
    FileManager fm = FileManager.getFileManager();

    //删除按钮
    Button btn_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        fileType = getIntent().getBundleExtra("bundle").getString("fileType");
        initActionBar(true, fileType, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_file = (ListView) findViewById(R.id.lv_file);
        btn_file = (Button) findViewById(R.id.btn_file);
        getFileList();
        fileAdapter = new FileAdapter(fileInfoList, this);
        lv_file.setAdapter(fileAdapter);
        //点击一个文件出现对应的操作
        lv_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取到文件类型，根据文件类型的MIME（识别码）进行跳转。跳转操作
                String mime = FileTypeUtil.getMIMEType(fileInfoList.get(position).getFile());
                //通过隐式跳转来打开对应的文件
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(fileInfoList.get(position).getFile()), mime);
                startActivity(intent);
            }
        });
        //快速滑动侦听
        lv_file.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //状态改变
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //停止滑动，让adapter加载图片数据
                        fileAdapter.isScroll = false;
                        fileAdapter.notifyDataSetChanged();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        //开始滑动，在滑动过程中，暂时使用默认
                        fileAdapter.isScroll = true;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除文件
                List<FileInfo> tempList = new ArrayList<FileInfo>();
                tempList.addAll(fileAdapter.getDataList());
                for (FileInfo f : tempList) {
                    if (f.isSelect()) {
                        //从所有文件列表中删除
                        fm.getAnyFileList().remove(f);
                        //从对应的文件列表中删除
                        switch (f.getFileType()) {
                            case FileTypeUtil.TYPE_TXT:
                                fm.getTxtFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_VIDEO:
                                fm.getVideoFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_AUDIO:
                                fm.getAudioFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_IMAGE:
                                fm.getImageFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_ZIP:
                                fm.getZipFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_APK:
                                fm.getApkFileList().remove(f);
                                break;
                        }
                        //改变大小
                        long totalSize = fm.getAnyFileSize();
                        fm.setAnyFileSize(totalSize -= f.getFile().length());
                        f.getFile().delete();
                    }

                }
                fileAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getFileList() {
        //根据传进来的fileType，获取文件的数据
        switch (fileType) {
            case "所有文件":
                fileInfoList = FileManager.getFileManager().getAnyFileList();
                break;
            case "文档文件":
                fileInfoList = FileManager.getFileManager().getTxtFileList();
                break;
            case "视频文件":
                fileInfoList = FileManager.getFileManager().getVideoFileList();
                break;
            case "音频文件":
                fileInfoList = FileManager.getFileManager().getAudioFileList();
                break;
            case "图像文件":
                fileInfoList = FileManager.getFileManager().getImageFileList();
                break;
            case "压缩文件":
                fileInfoList = FileManager.getFileManager().getZipFileList();
                break;
            case "apk文件":
                fileInfoList = FileManager.getFileManager().getApkFileList();
                break;
        }
    }
}
