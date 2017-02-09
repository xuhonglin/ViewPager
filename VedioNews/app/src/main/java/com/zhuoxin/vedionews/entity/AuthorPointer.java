package com.zhuoxin.vedionews.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/27.
 */
public class AuthorPointer {
    @SerializedName("__type")
    private String type;
    private String className;
    private String objectId;

    public AuthorPointer(String userId) {
        type = "Pointer";
        className = "_User";
        this.objectId = userId;
    }
}
