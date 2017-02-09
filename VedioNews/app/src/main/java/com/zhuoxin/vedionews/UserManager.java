package com.zhuoxin.vedionews;

/**
 * 很简单的用户管理类。
 */
public class UserManager {

    private static UserManager sInstance;

    public static UserManager getInstance() {
        if (sInstance == null) {
            sInstance = new UserManager();
        }
        return sInstance;
    }

    private String username;
    private String objectId;
    private boolean isPlay;//因为viewpager有缓存机制，需要控制视频的停止

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    private UserManager() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isOffline() {
        return username == null || objectId == null;
    }

    public void clear() {
        username = null;
        objectId = null;
    }
}
