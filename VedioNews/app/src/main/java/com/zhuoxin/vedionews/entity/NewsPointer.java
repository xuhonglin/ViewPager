package com.zhuoxin.vedionews.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/27.
 */
public class NewsPointer {
    @SerializedName("__type")
    private String type;
    private String className;
    private String objectId;

    public NewsPointer(String newsId) {
        type = "Pointer";
        className = "News";
        this.objectId = newsId;
    }
}
