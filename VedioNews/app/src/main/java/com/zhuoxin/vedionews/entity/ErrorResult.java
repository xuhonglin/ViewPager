package com.zhuoxin.vedionews.entity;

/**
 * Created by Administrator on 2016/12/22.
 */

public class ErrorResult {

    /**
     * code : 202
     * error : username 'yzg1' already taken.
     */

    private int code;
    private String error;

    @Override
    public String toString() {
        return "ErrorResult{" +
                "code=" + code +
                ", error='" + error + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
