package com.mediamodule.util;

import android.content.res.AssetManager;

import com.mediamodule.app.MediaConfig;

import java.io.IOException;

/**
 * Title:文件工具类
 * description:
 * autor:pei
 * created on 2020/2/5
 */
public class FileUtil {

    /***
     * 判断某个文件是否存在于Assets文件夹中
     *
     * @param fileName：Assets文件夹下文件名,如 order_tip.mp3
     * @return
     */
    public static boolean isExistInAssets(String fileName) {
        AssetManager assetManager = MediaConfig.getInstance().getApplication().getAssets();
        try {
            String names[] = assetManager.list("");
            for (int i = 0; i < names.length; i++) {
                if (null != names[i] && names[i].equals(fileName.trim())) {
                    MediaLog.i(fileName + "存在于Assets文件夹下");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            MediaLog.i(fileName + "不存在于Assets文件夹下");
            return false;
        }
        return false;
    }

}
