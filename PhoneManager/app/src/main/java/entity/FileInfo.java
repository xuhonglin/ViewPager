package entity;

import java.io.File;

/**
 * 创建自定义的FileInfo，作为一个bean
 * Created by Administrator on 2016/11/22.
 */

public class FileInfo {
    private File file;//原始文件
    private String iconName;//文件的图标资源
    private String fileType;//文件类型，MP3  MP4
    private boolean isSelect;//添加标记，是否被选择、删除

    /**
     * 构造函数
     *
     * @param file
     * @param iconName
     * @param fileType
     */
    public FileInfo(File file, String iconName, String fileType) {
        this.file = file;
        this.iconName = iconName;
        this.fileType = fileType;
        this.isSelect = false;
    }

    //封装方法
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
