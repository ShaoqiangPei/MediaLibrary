package com.mediamodule.app;

import android.app.Application;
import com.mediamodule.util.MediaLog;

/**
 * Title:库初始化类
 * description:
 * autor:pei
 * created on 2019/12/17
 */
public class MediaConfig {

    private Application mApplication;
    private boolean mDebug=false;//是否开启打印(默认不开启)

    private MediaConfig(){}

    private static class Holder {
        private static MediaConfig instance = new MediaConfig();
    }

    public static MediaConfig getInstance() {
        return Holder.instance;
    }

    /**初始化赋值(在项目的自定义Application中初始化)**/
    public MediaConfig init(Application application){
        this.mApplication=application;
        return MediaConfig.this;
    }

    /**
     * 是否打开Log打印
     * Log打印,tag="Media"
     *
     * @param print true:打开调试log,  false:关闭调试log
     * @return
     */
    public void setDebug(boolean print){
        this.mDebug=print;
        //设置自定义打印开关
        MediaLog.setDebug(mDebug);
    }

    /**获取项目上下文**/
    public Application getApplication() {
        if(mApplication==null){
            throw new NullPointerException("====MediaPro库需要初始化：MediaConfig.getInstance.init(Application application)===");
        }
        return mApplication;
    }

    /**
     * 获取log打印开关
     * @return true:打开调试log,  false:关闭调试log
     */
    public boolean isDebug() {
        return mDebug;
    }

}
