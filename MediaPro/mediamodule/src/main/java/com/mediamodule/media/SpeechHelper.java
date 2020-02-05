package com.mediamodule.media;

import android.os.Bundle;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.mediamodule.app.MediaConfig;
import com.mediamodule.util.MediaLog;
import com.mediamodule.util.StringUtil;

/**
 * Title:语音合成帮助类
 * Description:
 * <p>
 * Created by pei
 * Date: 2018/4/8
 */
public class SpeechHelper {

    //默认appid
    private static final String APPID="5afcdbda";

    private String[] mCloudVoicersEntries={"小燕—女青、中英、普通话","小宇—男青、中英、普通话","凯瑟琳—女青、英",
            "亨利—男青、英","玛丽—女青、英","小研—女青、中英、普通话",
            "小琪—女青、中英、普通话","小峰—男青、中英、普通话","小梅—女青、中英、粤语",
            "小莉—女青、中英、台湾普通话","小蓉—女青、中、四川话","小芸—女青、中、东北话",
            "小坤—男青、中、河南话","小强—男青、中、湖南话","小莹—女青、中、陕西话",
            "小新—男童、中、普通话","楠楠—女童、中、普通话","老孙—男老、中、普通话"};
    private String[] mCloudVoicersValue={"xiaoyan","xiaoyu","catherine","henry","vimary","vixy",
            "xiaoqi","vixf","xiaomei","xiaolin","xiaorong","xiaoqian",
            "xiaokun","xiaoqiang","vixying","xiaoxin","nannan","vils"};

    public static int MIN_VALUE=0;//最小阈值
    public static int MAX_VALUE=100;//最大阈值
    private static String COMPOUND_SPEED="50";//设置合成语速(参数0-100)
    private static String COMPOUND_TONES="50";//设置合成音调(参数0-100)
    private static String COMPOUND_VOICE="50";//设置合成音量(参数0-100)
    private static String VOICE_STREAM_TYPE="3";//音频六类型

    //语速(参数0-100,默认50)
    private int mCompoundSpeed=Integer.valueOf(COMPOUND_SPEED);
    //音调(参数0-100,默认50)
    private int mCompoundTones=Integer.valueOf(COMPOUND_TONES);
    //音量(参数0-100,默认50)
    private int mCompoundVoice=Integer.valueOf(COMPOUND_VOICE);

    //语音播报的appId
    private String mAppId=APPID;
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 发音人(下标0-17,默认下标5)
    private String mVoicer = mCloudVoicersValue[5];//"vixy"
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private boolean mSpeaking;//是否正在朗读

    private SpeechHelper() {}

    private static class Holder {
        private static SpeechHelper instance = new SpeechHelper();
    }

    public static SpeechHelper getInstance() {
        return Holder.instance;
    }

    /**
     * 自定义application中调用
     * @param appid 为null时使用默认appid初始化。也可以设置自己项目的appid
     * @return
     */
    public SpeechHelper initSpeech(String appid){
        if(StringUtil.isNotEmpty(appid)){
            mAppId=appid;
        }
        SpeechUtility.createUtility(MediaConfig.getInstance().getApplication(), "appid=" + mAppId);
//        //以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
//        Setting.setShowLog(false);
        return SpeechHelper.this;
    }

    /**设置发音人(index范围[0-17],默认index=5)**/
    public SpeechHelper setVoicer(int index){
        if(0<=index&&index<=mCloudVoicersValue.length-1){
            mVoicer=mCloudVoicersValue[index];
        }
        return SpeechHelper.this;
    }

    /**设置语速(参数0-100,默认50)**/
    public SpeechHelper setCompoundSpeed(int speed){
        if(MIN_VALUE<=speed&&speed<=MAX_VALUE){
            this.mCompoundSpeed=speed;
        }
        return SpeechHelper.this;
    }

    /**设置音调(参数0-100,默认50)**/
    public SpeechHelper setCompoundTones(int tones){
        if(MIN_VALUE<=tones&&tones<=MAX_VALUE){
            this.mCompoundTones=tones;
        }
        return SpeechHelper.this;
    }

    /**设置音量(参数0-100,默认50)**/
    public SpeechHelper setCompoundVoice(int voice){
        if(MIN_VALUE<=voice&&voice<=MAX_VALUE){
            this.mCompoundVoice=voice;
        }
        return SpeechHelper.this;
    }

    /**开始读**/
    public void speak(String message){
        //开始播报
        if(StringUtil.isNotEmpty(message)){
            if(mTts==null){
                // 初始化合成对象
                mTts = SpeechSynthesizer.createSynthesizer(MediaConfig.getInstance().getApplication(), mTtsInitListener);
            }
            if(mTts!=null){
                mSpeaking=true;
                //设置
                setParam();
                //开始播报
                int code=mTts.startSpeaking(message, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.ico";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);
                if (code != ErrorCode.SUCCESS) {
                    MediaLog.e("语音合成失败,错误码: " + code);
                }
            }else{
                MediaLog.i("======初始化失败======");
            }
        }else{
            MediaLog.e("=====播报内容不能为空==========");
        }
    }

    public void destroy(){
        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
            MediaLog.i("======语音播报销毁========");
        }
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            MediaLog.i("InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                MediaLog.i("初始化失败,错误码： " + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };


    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
//            MediaLog.i("开始播放");
        }

        @Override
        public void onSpeakPaused() {
//            MediaLog.i("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
//            MediaLog.i("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
//            MediaLog.i("======合成进度======缓冲进度="+mPercentForBuffering+"   播放进度:"+mPercentForPlaying);
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
//            MediaLog.i("======播放进度======缓冲进度="+mPercentForBuffering+"   播放进度:"+mPercentForPlaying);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
//                MediaLog.i("播放完成");
            } else if (error != null) {
                MediaLog.i(error.getPlainDescription(true));
            }
            mSpeaking=false;
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**是否正在播报**/
    public boolean isSpeaking(){
        return mSpeaking;
    }


    /**
     * 参数设置
     * @return
     */
    private void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, mVoicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, String.valueOf(mCompoundSpeed));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, String.valueOf(mCompoundTones));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, String.valueOf(mCompoundVoice));
        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE,VOICE_STREAM_TYPE);
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

}
