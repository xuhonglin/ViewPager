package com.zhuoxin.vedionews.entity;

/**
 * Created by Administrator on 2016/12/27.
 */

public class CollectResult {

    private boolean success;
    private String error;
    private NewsEntity data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public NewsEntity getData() {
        return data;
    }

    public void setData(NewsEntity data) {
        this.data = data;
    }
}
