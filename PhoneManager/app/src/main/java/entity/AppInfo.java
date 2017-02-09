package entity;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/11/11.
 */

public class AppInfo {
    public Drawable appicon;
    public String appname;
    public boolean isSystem;
    public String packageName;
    public String appversion;
    public boolean isDelete;

    public AppInfo(Drawable appicon, String appname, boolean isSystem, String packageName, String appversion, boolean isDelete) {
        this.appicon = appicon;
        this.appname = appname;
        this.isSystem = isSystem;
        this.packageName = packageName;
        this.appversion = appversion;
        //如果是系统软件，不能被删除
        if (isSystem) {
            this.isDelete = false;
        } else {
            this.isDelete = isDelete;
        }
    }
}
