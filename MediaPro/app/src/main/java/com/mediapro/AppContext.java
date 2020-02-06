package com.mediapro;

import android.app.Application;
import com.mediamodule.app.MediaConfig;
import com.mediamodule.media.SpeechHelper;

/**
 * Title:自定义application
 * description:
 * autor:pei
 * created on 2020/1/8
 */
public class AppContext extends Application {

    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //初始化mediaLibrary
        MediaConfig.getInstance().init(this)
                .setDebug(true);//是否开启本库内部log打印
        //语音合成初始化
        SpeechHelper.getInstance().initSpeech()
                .setVoicer(5)//设置发音人(index范围[0-17],默认index=5,"vixy")
                .setCompoundSpeed(50)//设置语速(参数0-100,默认50)
                .setCompoundTones(50)//设置音调(参数0-100,默认50)
                .setCompoundVoice(SpeechHelper.MAX_VALUE);//设置音量(参数0-100,默认50)
    }
}
