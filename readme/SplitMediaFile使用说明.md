## SplitMediaFile使用说明

### 概述
`SplitMediaFile`是一个用于将`mp4的有声文件`处理成`mp3`格式的音频文件和`mp4`格式的无声文件的帮助类，它可以完成视频文件的分离及音频文件的提取。

### 使用说明
#### 一.用户权限
涉及到文件读写问题，先是要在你项目的`androidmainfast.xml`中添加读写权限，如下：
```
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```
然后需要添加手动权限库，这里我使用的是`PermissionsDispatcher`，手动库的添加大家可以参考以下文章：
[PermissionsDispatcher动态权限申请kotlin版](https://www.jianshu.com/p/c3da2f4aff34)
接着需要添加`FileProvider`相关处理，大家可参考以下文章
[SpUtil多样加密存储，兼容android9.0](https://www.jianshu.com/p/bbf057ccbcff)
#### 二. SplitMediaFile方法简介
`SplitMediaFile`作为一个`视频分离帮助类`,拥有以下方法：
```
//初始化
SplitMediaFile()

    /****
     * 分离视频的方法
     *
     * @param sourceFilePath 要分离成音频和无声视频的视频源文件地址
     *                       路径中含文件后缀。
     *
     * @param videoFilePath 要存储的无声视频文件地址,为 null 表示不分离出无声视频文件
     *                      路径中不含文件后缀。(生成新的视频文件格式为 MP4)
     *
     * @param audioFilePath 要存储的音频文件地址,为 null 表示不分离出音频文件
     *                      路径中不含文件后缀。(生成新的视频文件格式为 MP3)
     */
    public void split(String sourceFilePath,String videoFilePath,String audioFilePath) 
```
需要在注意的是，`split(String sourceFilePath,String videoFilePath,String audioFilePath) `方法再生成 `音/视 频`的调用如下：
```
                //仅分离出音频(mp3格式)
                mSplitMediaFile.split(path,null,audioFilePath);
                //仅分离出视频(无声MP4)
                mSplitMediaFile.split(path,videoFilePath,null);
                //分离出视频和音频
                mSplitMediaFile.split(path,videoFilePath,audioFilePath);
```
#### 三.SplitMediaFile 在 Activity 中的使用
在`Activity`中使用示例如下：
```
                String path="/data/user/0/com.testdemo/cache/test.mp4";
                LogUtil.i("========path="+path);
                //设置分离文件路径
                String tempPath=path.substring(0,path.lastIndexOf("/")+1);
                //视频文件路径
                String videoFilePath=tempPath+"test_video";
                //音频文件路径
                String audioFilePath=tempPath+"test_audio";

//                //仅分离出音频(mp3格式)
//                mSplitMediaFile.split(path,null,audioFilePath);
//                //仅分离出视频(无声MP4)
//                mSplitMediaFile.split(path,videoFilePath,null);
                //分离出视频和音频
                mSplitMediaFile.split(path,videoFilePath,audioFilePath);
```


