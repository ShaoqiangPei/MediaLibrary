## VideoHelper使用说明

### 概述
`VideoHelper`是一个快速实现视频文件播放的工具类,其主要用于协助视频播放控件`VideoView`的使用。当开发者在使用`VideoView`控件时，结合此类使用
可快速实现视频播放。

### 用法
#### 一. VideoHelper使用流程介绍
`VideoHelper`用于快速实现视频播放。其使用主要包含两个阶段：设置播放源文件，在设置播放源成功的情况下，开始播放视频文件。
#### 二. VideoHelper主要方法介绍
`VideoHelper`作为`VideoView`的一个播放帮助类，具有以下一些主要方法：
```
    /**初始化**/
    public VideoHelper(VideoView videoView,Context context)

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
    public String getcopyAssetToCachePath(String fileName)

    /**删除由于 读取 assets文件夹下视频文件产生的缓存 **/
    public boolean deleteCacheFile(String filePath)

    /***
     * 根据文件路径设置播放文件
     *
     * @param path
     * @return true:文件源设置成功   false：文件源设置失败(可能文件不存在)
     */
    public boolean setVideoPath(String path)

    /***
     * 根据uri 设置播放路径
     *
     * @param uri
     * @return  true:文件源设置成功   false：文件源设置失败(可能文件不存在)
     */
    public boolean setVideoUri(Uri uri)

    /***
     * 根据 uri,headers 设置播放路径
     *
     * @param uri
     * @param headers
     * @return  true:文件源设置成功   false：文件源设置失败(可能文件不存在)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean setVideoUriMap(Uri uri, Map<String,String>headers)

    /***
     *  播放视频
     *
     * @param showController 是否显示默认控制器  true:显示   false：隐藏
     * @param listener 播放监听
     */
    public void playVideo(boolean showController,OnPlayListener listener)

    /**开始播放**/
    public void start()

    /**暂停**/
    public void pause() 

    /**恢复播放**/
    public void resume()

    /**快进**/
    public void seekTo(int msec)

    /****
     * 获取播放时长与总时长
     *
     * @param filePath 播放文件路径
     * @return getDurationArray[0]: 当前播放时长
     *         getDurationArray[1]: 播放文件总时长
     */
    public String[] getDurationArray(String filePath)
```
需要注意的是：`VideoHelper`设置播放视频的逻辑是先设置播放文件源，然后执行播放方法。
`VideoHelper`设置播放源的方法大致分为三种：
- 设置uri播放源。如`setVideoUri(Uri uri)`,`setVideoUriMap(Uri uri, Map<String,String>headers)
- 设置一般文件路径播放源(非`assets`文件夹路径)。如`setVideoPath(String path)`
- 设置`assets`文件夹路径播放源。如：`getcopyAssetToCachePath(String fileName)`

针对`设置`assets`文件夹路径播放源`的情况，我们先要用`getcopyAssetToCachePath(String fileName)`获取到播放文件的缓存路径，然后调用`setVideoPath(String path)`来设置播放源。
由于`getcopyAssetToCachePath(String fileName)`会产生缓存文件。为了优化内存，在不再需要播放的时候(一般为程序退出的时候)，我们需要调用`deleteCacheFile(String filePath)`方法来清理缓存文件。
当然，不要忘了，只有当设置播放源成功以后，我们才执行`playVideo(boolean showController,OnPlayListener listener)`来播放视频文件。
#### 三.VideoHelper 的使用
视频的播放会涉及到文件读写问题，当然，也有可能会涉及到网络权限的问题。
##### 3.1 用户权限
若涉及到播放网络视频流 或者 网络下载视频文件再播放的话，则需要在`Androidmanifast.xml`中添加网络权限：
```
    <uses-permission android:name="android.permission.INTERNET"/>
```
还会涉及到文件读写问题，先是要在你项目的`androidmainfast.xml`中添加读写权限，如下：
```
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```
然后需要添加手动权限库，这里我使用的是`PermissionsDispatcher`，手动库的添加大家可以参考以下文章：
[PermissionsDispatcher动态权限申请kotlin版](https://www.jianshu.com/p/c3da2f4aff34)
接着需要添加`FileProvider`相关处理，大家可参考以下文章
[SpUtil多样加密存储，兼容android9.0](https://www.jianshu.com/p/bbf057ccbcff)
##### 3.2  VideoView布局
`VideoHelper`是`VideoView`的一个帮助类，需要结合控件`VideoView`使用，所以你需要在你的布局文件中添加`VideoView`控件，类似如下：
```
    <VideoView
        android:id="@+id/mVideoView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="10dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/mBtnTest"
        app:layout_constraintBottom_toBottomOf="parent"/>
```
##### 3.3  VideoHelper在Activity中使用
下面贴出`VideoHelper`在`TempActivity`中使用代码(读写权限相关代码省略)：
```
public class TempActivity extends AppCompatActivity{

    private TextView mTvTest;
    private Button mBtnTest;
    private Button mBtnTest2;
    private VideoView mVideoView;

    private VideoHelper mVideoHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        //初始化控件
        initView();
        //初始化数据
        initData();
        //控件监听
        setListener();
    }

    /**初始化控件**/
    private void initView(){
        mTvTest=findViewById(R.id.mTvTest);
        mBtnTest=findViewById(R.id.mBtnTest);
        mBtnTest2=findViewById(R.id.mBtnTest2);
        mVideoView=findViewById(R.id.mVideoView);
    }

    private void initData(){
        mVideoHelper=new VideoHelper(mVideoView,this);

    }

    /**控件监听**/
    private void setListener() {
        //测试播放
        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除播放的缓存文件
                File dataFile=new File(getCacheDir(),"test.mp4");
                LogUtil.i("===deletePath="+dataFile.getAbsolutePath());
                boolean deleteFlag=mVideoHelper.deleteCacheFile(dataFile.getAbsolutePath());
                LogUtil.i("===deleteFlag="+deleteFlag);

                //获取 assets文件夹下视频文件路径
                String path=mVideoHelper.getcopyAssetToCachePath("test.mp4");
                //设置并播放
                if(mVideoHelper.setVideoPath(path)){
                    mVideoHelper.playVideo(true, new VideoHelper.OnPlayListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            LogUtil.i("========播放完毕====");
                        }

                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            LogUtil.i("========播放发生错误====");
                            return false;
                        }
                    });
                }else{
                    ToastUtil.shortShow("===播放文件不存在====");
                }

            }
        });

        //播放进度
        mBtnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dataFile=new File(getCacheDir(),"test.mp4");

                String durationArray[]=mVideoHelper.getDurationArray(dataFile.getAbsolutePath());
                ToastUtil.shortShow("播放进度: "+durationArray[0]+"/"+durationArray[1]);

            }
        });
    }

}
```


