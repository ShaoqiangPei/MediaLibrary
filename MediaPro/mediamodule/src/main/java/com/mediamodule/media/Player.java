package com.mediamodule.media;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import com.mediamodule.app.MediaConfig;
import com.mediamodule.util.FileUtil;
import com.mediamodule.util.MediaLog;
import com.mediamodule.util.NetUtil;
import com.mediamodule.util.StringUtil;
import java.io.File;
import java.io.IOException;

/**
 * Title:播放器
 * Description:
 * <p>
 * Created by pei
 * Date: 2017/11/6
 */
public class Player {

    private static final int NO_DATA=0;//未设置播放源
    private static final int ASSETS_DATA=1;//设置播放源为Assets文件夹
    private static final int RAW_DATA=2;//设置播放源为Raw文件夹
    private static final int SDCARD_DATA=3;//设置播放源为sdcard文件路径
    private static final int URL_DATA=4;//设置播放源为网络url链接

    private MediaPlayer mediaPlayer;
    private int mDataType=NO_DATA;//播放源类型

    /**
     * 设置播放源为Assets文件夹
     *
     * @param fileName :若播放文件路径为 main/assets/order_tip.mp3,则 fileName= "order_tip.mp3"
     *                 fileName为直接放到Assets文件夹下的播放文件名
     */
    public void setDataByAssets(String fileName){
        if(StringUtil.isEmpty(fileName)){
            throw new NullPointerException("播放文件名不能为空");
        }
        //判断某个文件是否存在于Assets文件夹中
        if(!FileUtil.isExistInAssets(fileName)){
            throw new SecurityException("==Assets文件夹中不存在"+fileName+"文件===");
        }
        //释放资源
        release();
        //重新创建mediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = MediaConfig.getInstance().getApplication().getAssets().openFd(fileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mDataType=ASSETS_DATA;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            //设置播放源失败的处理
            mDataType=NO_DATA;
        }  catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            //设置播放源失败的处理
            mDataType=NO_DATA;
        }
    }

    /**
     * 设置播放源为Raw文件夹
     * @param rawId:若播放文件路径为 res/raw/audio.mp3,则 rawId= R.raw.audio
     */
    public void setDataByRaw(int rawId){
        //释放资源
        release();
        //直接创建，不需要设置setDataSource
        mediaPlayer=MediaPlayer.create(MediaConfig.getInstance().getApplication(), rawId);
        mDataType=RAW_DATA;
    }

    /**
     * 设置播放源为 Sdcard 路径
     *
     * @param filePath 播放文件路径,如：getCacheDir().getAbsolutePath()+ File.separator+"order_tip.mp3"
     * 注:需要设置手动文件读写权限 和 provider文件读写权限
     */
    public void setDataBySdcardPath(String filePath){
        if(StringUtil.isEmpty(filePath)){
            throw new NullPointerException("播放文件路径不能为空");
        }
        //判断播放文件是否存在
        File file=new File(filePath);
        if(!file.exists()){
            throw new SecurityException("播放文件不存在");
        }
        //释放资源
        release();
        //重新创建mediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath) ;
            mDataType=SDCARD_DATA;
        } catch (IOException e) {
            e.printStackTrace();
            //设置播放源失败的处理
            mDataType=NO_DATA;
        }
    }

    /**
     * 设置播放源为网络url链接
     *
     * @param url: "http://..../xxx.mp3"
     * 用于测试的url(雨一直下)："http://np01.sycdn.kuwo.cn/7591a48f2fddfd3e8ab64601e133d2fe/5e3adec5/resource/n1/28/66/1638975979.mp3"
     * 若测试url失效，可在网址(https://www.dj63.com/dj/96321.html)上寻找可运行的MP3外链
     */
    public boolean setDataByUrl(String url){
        if(StringUtil.isEmpty(url)){
            throw new NullPointerException("播放url不能为空");
        }
        //判断网络
        if(!NetUtil.isNetworkConnected()){
            MediaLog.i("=====网络未连接,不能播放网络音乐文件=======");
            return false;
        }
        //释放资源
        release();
        //重新创建mediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url) ;
            mDataType=URL_DATA;
        } catch (IOException e) {
            e.printStackTrace();
            //设置播放源失败的处理
            mDataType=NO_DATA;
            MediaLog.i("=====设置播放源为网络url链接失败======="+e.getMessage());
        }
        return true;
    }

    /** 开始播放 **/
    public void start(MediaPlayer.OnCompletionListener onCompletionListener) {
        MediaLog.i("=======播放源类型==mDataType="+mDataType);
        //检测是否已经设置播放源
        if(mediaPlayer==null||mDataType==NO_DATA){
            //设置播放源的方法有：
            //1. setDataByAssets(String fileName)  ----设置播放源为Assets文件夹
            //2. setDataByRaw(int rawId)  ----设置播放源为Raw文件夹
            //3. setDataBySdcardPath(String filePath)  ----设置播放源为sdcard文件路径
            //4. setDataByUrl(String url)  ----设置播放源为网络url链接
            throw new SecurityException("请先设置播放源(播放文件)");
        }
        if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
            //根据不同播放源类型进行分类处理播放
            switch (mDataType) {
                case ASSETS_DATA://设置播放源为Assets文件夹
                    MediaLog.i("====播放源来自Assets文件夹=====");
                    //同步缓冲
                    bufferPlayer(false);
                    break;
                case RAW_DATA://设置播放源为Raw文件夹
                    MediaLog.i("====播放源来自Raw文件夹=====");
                    //同步缓冲
                    bufferPlayer(false);
                    break;
                case SDCARD_DATA://设置播放源为sdcard文件路径
                    MediaLog.i("====播放源来自sdcard文件路径=====");
                    //同步缓冲
                    bufferPlayer(false);
                    break;
                case URL_DATA://设置播放源为网络url链接
                    MediaLog.i("====播放源来自网络url链接=====");
                    //同步缓冲
                    bufferPlayer(false);
                    break;
                default:
                    break;
            }
            // 开始播放
            mediaPlayer.start();
            MediaLog.i("=======开始播放======");
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        }else{
            MediaLog.i("=======播放失败=====mediaPlayer="+mediaPlayer);
            if(null!=mediaPlayer){
                MediaLog.i("=======播放失败=====mediaPlayer.isPlaying()="+mediaPlayer.isPlaying());
            }
        }
    }


    /***
     * 缓冲播放器
     *
     * @param async 是否异步缓冲(true=异步缓冲,false=同步缓冲)
     */
    private void bufferPlayer(boolean async){
        MediaLog.i("======缓冲==1====");
        try {
            MediaLog.i("======缓冲==2====");
            if(async){
                MediaLog.i("======缓冲(异步)==3====");
                //异步缓冲
                mediaPlayer.prepareAsync() ;
                MediaLog.i("======缓冲(异步)==4====");
            }else{
                MediaLog.i("======缓冲(同步)==5====");
                //同步缓冲
                mediaPlayer.prepare();
                MediaLog.i("======缓冲(同步)==6====");
            }
            MediaLog.i("======缓冲==7====");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MediaLog.i("=====缓冲播放器失败==1========"+e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MediaLog.i("=====缓冲播放器失败==2========"+e.getMessage());
        }
    }

    /**暂停**/
    public void pause() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**是否在播放**/
    public boolean isPlaying(){
        if(mediaPlayer!=null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**停止**/
    public void stop() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**释放资源**/
    public void release() {
        if (null != mediaPlayer) {
            mDataType=NO_DATA;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            MediaLog.i("=====mediaPlayer释放资源==========");
        }
    }

}
