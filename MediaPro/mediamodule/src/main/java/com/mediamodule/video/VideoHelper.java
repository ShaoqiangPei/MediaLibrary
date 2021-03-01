package com.mediamodule.video;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.annotation.RequiresApi;

import com.mediamodule.util.FileUtil;
import com.mediamodule.util.MediaLog;
import com.mediamodule.util.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

/**
 * Title:VideoView 控件视频播放工具类
 *
 * description:
 * autor:pei
 * created on 2021/3/1
 */
public class VideoHelper {

    private static boolean isError=false;//是否播报出错

    private Context mContext;
    private VideoView mVideoView;
    private OnPlayListener mOnPlayListener;

    /**初始化**/
    public VideoHelper(VideoView videoView, Context context){
        this.mContext=context;
        this.mVideoView=videoView;
    }

    /***
     * 根据 assets 文件夹下文件路径设置播放文件
     *
     * 原理：assets文件夹下文件无法通过直接设置文件路径读取播放
     *      (assets文件夹下文件路径为：file:///android_asset/文件名)
     *      需要将assets文件夹下文件拷贝出来,然后读取拷贝文件路径
     *      此处是将assets文件夹下文件拷贝到缓存中
     *
     * @param fileName
     * @return
     */
    public String getcopyAssetToCachePath(String fileName){
        if(mContext==null){
            throw new NullPointerException("===mContext为null，请先调用init(VideoView videoView,Context context) ====");
        }
        if(StringUtil.isNotEmpty(fileName)) {
            //将asset文件写入缓存
            try {
                File cacheDir = mContext.getCacheDir();
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                File outFile = new File(cacheDir, fileName);
                if (!outFile.exists()) {
                    boolean res = outFile.createNewFile();
                    if (!res) {
                        MediaLog.e("=======创建文件失败========");
                        return null;
                    }
                } else {
                    if (outFile.length() > 10) {//表示已经写入一次
                        return outFile.getAbsolutePath();
                    }
                }
                InputStream is = mContext.getAssets().open(fileName);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
                return outFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            MediaLog.e("=====文件名不能为null======");
        }
        return null;
    }

    /**删除由于 读取 assets文件夹下视频文件产生的缓存 **/
    public boolean deleteCacheFile(String filePath) {
        if(StringUtil.isNotEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                    return true;
                } else {
                    MediaLog.e("=====filePath=" + filePath + " 路径不是文件(可能是文件夹?)");
                }
            } else {
                MediaLog.e("=====filePath=" + filePath + " 文件不存在");
            }
        }else{
            MediaLog.e("=====filePath不能为null=====");
        }
        return false;
    }

    /***
     * 根据文件路径设置播放文件
     *
     * @param path
     * @return true:文件源设置成功   false：文件源设置失败(可能文件不存在)
     */
    public boolean setVideoPath(String path){
        if(mVideoView==null){
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        if(StringUtil.isNotEmpty(path)) {
            File file = new File(path);
            if (file.exists() && file.isFile() && file.length() > 0) {
                mVideoView.setVideoPath(path);
                return true;
            }
        }
        return false;
    }

    /***
     * 根据uri 设置播放路径
     *
     * @param uri
     * @return  true:文件源设置成功   false：文件源设置失败(可能文件不存在)
     */
    public boolean setVideoUri(Uri uri){
        if(mVideoView==null){
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        if(uri!=null){
            mVideoView.setVideoURI(uri);
            return true;
        }
        return false;
    }

    /***
     * 根据 uri,headers 设置播放路径
     *
     * @param uri
     * @param headers
     * @return  true:文件源设置成功   false：文件源设置失败(可能文件不存在)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean setVideoUriMap(Uri uri, Map<String,String>headers){
        if(mVideoView==null){
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        if(uri!=null&&uri!=headers){
            mVideoView.setVideoURI(uri, headers);
            return true;
        }
        return false;
    }

    /***
     *  播放视频
     *
     * @param showController 是否显示默认控制器  true:显示   false：隐藏
     * @param listener 播放监听
     */
    public void playVideo(boolean showController,OnPlayListener listener){
        if(mVideoView==null){
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        if(mContext==null){
            throw new NullPointerException("===mContext为null，请先调用init(VideoView videoView,Context context) ====");
        }
        this.mOnPlayListener=listener;
        //播放前默认播放错误的值为false,即播放正常
        isError=false;
        //是否显示控制器
        if(showController){
            //提供一个控制器，控制其暂停、播放……等功能
            mVideoView.setMediaController(new MediaController(mContext));
        }
        //播放异常的监听
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (mOnPlayListener!=null){
                    isError=true;
                    return mOnPlayListener.onError(mp,what,extra);
                }
                return false;
            }
        });
        //视频播放完毕触发的方法
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!isError&&mOnPlayListener!=null){
                    mOnPlayListener.onCompletion(mp);
                    MediaLog.i("=======播放完毕======");
                }
            }
        });
        //如果在播放,就重播
        if(mVideoView.isPlaying()){
            mVideoView.seekTo(0);
        }else{
            //开始播放
            start();
        }
    }

    /**开始播放**/
    public void start(){
        if(mVideoView==null){
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        mVideoView.start();
    }

    /**暂停**/
    public void pause() {
        if (mVideoView == null) {
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        mVideoView.pause();
    }

    /**恢复播放**/
    public void resume(){
        if (mVideoView == null) {
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        mVideoView.resume();
    }

    /**快进**/
    public void seekTo(int msec){
        if (mVideoView == null) {
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        mVideoView.seekTo(msec);
    }


    /****
     * 获取播放时长与总时长
     *
     * @param filePath 播放文件路径
     * @return getDurationArray[0]: 当前播放时长
     *         getDurationArray[1]: 播放文件总时长
     */
    public String[] getDurationArray(String filePath){
        if (mVideoView == null) {
            throw new NullPointerException("===mVideoView为null，请先调用init(VideoView videoView,Context context) ====");
        }
        //已播放时长
        String currentTime = stringForTime(mVideoView.getCurrentPosition());
        MediaLog.i("已播放时长是: "+currentTime);
        //文件总时长
        String totalTime=null;
        //文件播放时有大小,未播放时返回文件大小为-1
        int totalSize= mVideoView.getDuration();
        if(totalSize==-1&& FileUtil.fileExist(filePath)){
            MediaLog.i("===文件未播放或还未设置播放源===");
            //文件未播放或还未设置播放源
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(filePath);
            //播放时长单位为毫秒
            String duration=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            MediaLog.i("=====duration===duration="+duration);
            try {
                totalSize=Integer.valueOf(duration);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }else{
            MediaLog.i("===文件在播放(或已设置好播放源)===");
            //文件在播放(或已设置好播放源)
            //默认 mVideoView.getDuration() 值,此处不做处理
        }
        totalTime = stringForTime(totalSize);
        MediaLog.i("播放时间总时长是: "+totalTime);

        //currentTime:当前播放时长
        //totalTime: 播放文件总时长
        return new String[]{currentTime,totalTime};
    }

    /**将长度转换为时间**/
    private String stringForTime(int timeMs) {
        //将长度转换为时间
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /***
     * 视频播放监听
     *
     */
    public interface OnPlayListener {
        void onCompletion(MediaPlayer mp);
        boolean onError(MediaPlayer mp, int what, int extra);
    }

}
