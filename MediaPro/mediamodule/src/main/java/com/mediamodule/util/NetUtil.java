package com.mediamodule.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mediamodule.app.MediaConfig;

/**
 * Title:网络检测工具类
 * description:
 * autor:pei
 * created on 2019/12/17
 */
public class NetUtil {

    /**
     * 检查网络是否可用
     */
    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MediaConfig.getInstance().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

}
