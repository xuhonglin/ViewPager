package com.zhuoxin.phone.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.zhuoxin.phone.R;
import com.zhuoxin.phone.adapter.FileAdapter;
import com.zhuoxin.phone.base.ActionBarActivity;
import com.zhuoxin.phone.biz.MemoryManager;
import com.zhuoxin.phone.db.DBManager;
import com.zhuoxin.phone.utils.FileTypeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import entity.FileInfo;

public class CleanActivity extends ActionBarActivity {
    @InjectView(R.id.lv_clean)
    ListView lv_clean;
    //获取路径数据
    List<String> filePathList = new ArrayList<String>();
    List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
    FileAdapter fileAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        ButterKnife.inject(this);
        initActionBar(true, "手机清理", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        //从数据库中读取文件信息
        File targetFile = new File(getFilesDir(), "clearpath.db");
        filePathList.addAll(DBManager.getFilePath(targetFile));
        //把文件路径拿出来，在我们的手机上查找。如果有就显示出来，放到adapter中
        fileAdapter = new FileAdapter(fileInfoList, this);
        for (String path : filePathList) {
            //sd卡路径+app路径
            File appFile = new File(MemoryManager.getPhoneInSDCardPath() + path);
            //判断该文件是否存在
            if (appFile.exists()) {
                FileInfo fileInfo = new FileInfo(appFile, FileTypeUtil.getFileIconAndTypeName(appFile)[0], FileTypeUtil.TYPE_ANY);
                fileAdapter.getDataList().add(fileInfo);
            }
        }
        lv_clean.setAdapter(fileAdapter);
    }

    @OnClick(R.id.btn_clean)
    public void onClick(View view) {
        //获取并克隆一份数据
        List<FileInfo> tempList = new ArrayList<FileInfo>();
        tempList.addAll(fileAdapter.getDataList());
        //从临时数据中读取每一个位置的文件，是否要删除。如果是，执行删除操作
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).isSelect()) {
                deleteFile(tempList.get(i).getFile());
                fileAdapter.getDataList().remove(i);
            }
        }
        fileAdapter.notifyDataSetChanged();
    }

    //删除文件（文件夹）
    private void deleteFile(File file) {
        //如果是文件，直接删除
        if (file.isFile()) {
            file.delete();
        } else {
            //如果是文件夹
            //取出文件夹中的数据，判断是否为空
            File files[] = file.listFiles();
            //如果为空，直接删除
            if (files != null) {
                if (files.length <= 0) {
                    file.delete();
                    return;
                } else {
                    //不为空，依次递归
                    for (File f : files) {
                        deleteFile(f);
                    }
                }
            }
            //删除空文件夹
            file.delete();
        }
    }
}
