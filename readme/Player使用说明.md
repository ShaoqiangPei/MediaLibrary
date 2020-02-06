## Player使用说明

### 简介
Player 主要用于播放音乐。其实现是基于`mediaplayer`的一个封装。使用简单，具备设置多种播放源的特点。  
由于`Player`由``封装而成，所以其支持播放的音乐文件格式与``相同。支持：AAC、AMR、FLAC、MP3、MIDI、OGG、PCM等格式。

### 使用说明
#### 一. Player播放流程
```
   初始化Player对象 -----> 设置播放源 -----> 开始播放音乐 -----> ......   -----> 音乐播放完毕释放资源
```
#### 二. Player初始化
在使用`Player`之前，你需要初始化：
```
    //声明Player
    private Player mPlayer;
    
    //初始化mPlayer
    mPlayer=new Player();
```
#### 三. 设置播放源
`Player`设置播放源又以下几种方式：
```
    1. setDataByAssets(String fileName)  ----设置播放源为Assets文件夹
    2. setDataByRaw(int rawId)  ----设置播放源为Raw文件夹
    3. setDataBySdcardPath(String filePath)  ----设置播放源为sdcard文件路径
    4. setDataByUrl(String url)  ----设置播放源为网络url链接
```
下面就以上几种设置播放源的方式来讲述播放。
##### 3.1 播放Assets下文件
播放 `main/assets/`文件夹下的音乐文件。  
若音乐文件存放路径为：`main/assets/order_tip.mp3`,则播放示例代码如下：
```
    /**播放Assets下文件**/
    private void playByAssets(){
        mPlayer.setDataByAssets("order_tip.mp3");
        mPlayer.start(null);
    }
```
##### 3.2 播放Raw文件夹下音乐文件
播放`res/raw/`文件夹下音乐文件。   
若音乐文件存放路径为：`res/raw/order_tip.mp3`,则播放示例代码如下：
```
    /**播放Raw文件夹下音乐文件**/
    private void playByRaw(){
        mPlayer.setDataByRaw(R.raw.order_tip);
        mPlayer.start(null);
    }
```
##### 3.3 播放sdcard下音乐文件
主要用于处理根据文件路径进行播放的情况。涉及到文件读写权限问题。  
本库已经在内置`manifast.xml`中声明的文件读写权限。但使用者仍需要在项目中处理`Android 6.0+手动获取权限`及`Fileprovider文件读写`权限问题。   
若你项目中尚未添加`Android 6.0+手动获取权限库`及`Fileprovider文件读写`相关代码。你可以参考以下文章进行处理。 
[PermissionsDispatcher动态权限申请](https://www.jianshu.com/p/3864d5b9f267)  
[Android增量更新(三)—代码实现](https://www.jianshu.com/p/c62340688942)中关于`Fileprovider文件读写`的描述  
ok，读写权限具备以后，你可以像下面这样播放sdcard下音乐文件:
```
    /**播放sdcard下音乐文件**/
    private void playBySdcardPath(){
        String path=getCacheDir().getAbsolutePath()+ File.separator+"order_tip.mp3";
        LogUtil.i("=====path======"+path);
        mPlayer.setDataBySdcardPath(path);
        //播放
        mPlayer.start(null);
    }
```
##### 3.4 播放url链接音乐
注意，只有在手机联网情况下，才能正常播放音乐。本库已经在内置`manifast.xml`中声明联网权限。  
播放网络音乐，你可以像这样：
```
    /**播放url链接音乐**/
    private void playByUrl(){
        String url="https://demo.dj63.com//2016/%E4%B8%B2%E7%83%A7%E8%88%9E%E6%9B%B2/20161108/[%E7%94%B7%E4%BA%BA%E5%A3%B0%E7%BA%BF]%E5%85%A8%E5%9B%BD%E8%AF%AD%E9%9F%B3%E4%B9%90%E7%83%AD%E6%92%AD%E6%83%85%E6%AD%8C%E6%AD%8C%E6%9B%B2%E8%BF%9E%E7%89%88%E4%B8%B2%E7%83%A7.mp3";
        boolean prepare=mPlayer.setDataByUrl(url);
        if(prepare) {
            //播放
            mPlayer.start(null);
        }else{
            LogUtil.i("=====网络未连接,不能播放网络音乐文件=======");
        }
    }
```
这里需要注意的是，播放代码一定要`prepare==true`时才执行，否则在没开启手机网络时，程序会崩溃，并抛出异常`请先设置播放源(播放文件)`.
##### 3.5 获取网络音乐测试链接
这里以 MP3音乐外链为例。若要测试网络音乐播放，你可以使用以下url链接:
```
      String url="https://demo.dj63.com//2016/%E4%B8%B2%E7%83%A7%E8%88%9E%E6%9B%B2/20161108/[%E7%94%B7%E4%BA%BA%E5%A3%B0%E7%BA%BF]%E5%85%A8%E5%9B%BD%E8%AF%AD%E9%9F%B3%E4%B9%90%E7%83%AD%E6%92%AD%E6%83%85%E6%AD%8C%E6%AD%8C%E6%9B%B2%E8%BF%9E%E7%89%88%E4%B8%B2%E7%83%A7.mp3";        
```
若以上链接失效，你可在以下网址中查找有效链接：
```
       https://www.dj63.com/dj/96321.html
```
#### 四. 音乐播放完毕的监听
若你不需要监听音乐播放完毕的动作，你可以这样播放音乐:
```
       //播放
       mPlayer.start(null);
```
若你要监听音乐播放完毕的动作，你可以这样播放音乐:
```
        mPlayer.start(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //处理音乐播放完毕后的逻辑
                //......
            }
        });
```
#### 五. 播放器资源回收
在音乐播放器使用完毕后,需要释放播放器资源：
```
        //释放播放资源
        mPlayer.release();
```
#### 六. Player其他方法
```
    /**暂停**/
    public void pause()
    
    /**是否在播放**/
    public boolean isPlaying()
    
    /**停止**/
    public void stop()
```

