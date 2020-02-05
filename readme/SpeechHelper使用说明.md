## SpeechHelper使用说明

### 简介
SpeechHelper 是一个语音合成工具类。主要功能是将文字转成语音。

### 使用说明
#### 一.权限
SpeechHelper采用的是在线语音合成，所以需要网络权限。本库内已经在`androidManifast.xml`中添加了
```
    <!--网络权限-->
    <uses-permission android:name="android.permission.INTERNET"/>
```
故在使用本功能的时候，请务必保持手机是联网状态。
#### 二.初始化及参数设置
在你项目的自定义Application中初始化，你可以这样：
```
        //语音合成初始化
        SpeechHelper.getInstance().initSpeech(null)//为null时使用默认appid初始化。也可以设置自己项目的appid
```
此处`initSpeech(String appid)`中的`appid`,你可以使用自己在`科大讯飞官网`上注册的语音合成功能的`appid`,也可以直接设置为`null`。
当设置为`null`时jian，将使用本项目默认的`appid`。  
若你要对语音合成做些基本的参数设置，你可以在你界面的初始化中做如下设置：
```
        //语音合成参数设置
        SpeechHelper.getInstance().setVoicer(5)//设置发音人(index范围[0-17],默认index=5,"vixy")
                        .setCompoundSpeed(50)//设置语速(参数0-100,默认50)
                        .setCompoundTones(50)//设置音调(参数0-100,默认50)
                        .setCompoundVoice(SpeechHelper.MAX_VALUE);//设置音量(参数0-100,默认50)
```
若你项目中关于`语音合成`功能参数的设置只需一次，那么你可以直接在你项目的自定义Application中与SpeechHelper的初始化一起设置完，示例如下：  
```
        //语音合成初始化及参数设置
        SpeechHelper.getInstance().initSpeech(null)//为null时使用默认appid初始化。也可以设置自己项目的appid
                .setVoicer(5)//设置发音人(index范围[0-17],默认index=5,"vixy")
                .setCompoundSpeed(50)//设置语速(参数0-100,默认50)
                .setCompoundTones(50)//设置音调(参数0-100,默认50)
                .setCompoundVoice(SpeechHelper.MAX_VALUE);//设置音量(参数0-100,默认50)
```
#### 三.语音合成功能的使用
当你需要对播报参数做设置的时候，你可以在界面中类似这样：
```
        //语音合成参数设置
        SpeechHelper.getInstance().setVoicer(5)//设置发音人(index范围[0-17],默认index=5,"vixy")
                        .setCompoundSpeed(50)//设置语速(参数0-100,默认50)
                        .setCompoundTones(50)//设置音调(参数0-100,默认50)
                        .setCompoundVoice(SpeechHelper.MAX_VALUE);//设置音量(参数0-100,默认50)
```
当然，也可以对单个参数进行设置，如我要设置语速为20，你可以直接这样：
```
        SpeechHelper.getInstance().setCompoundSpeed(20);//设置语速(参数0-100,默认50)
```
当你需要播报文字时，你可以这样(以播报"我是谁"为例)：
```
        //播报
        SpeechHelper.getInstance().speak("我是谁");
```
判断当前是否正在播报：
```
        //正在播报返回true,否则返回false
        SpeechHelper.getInstance().isSpeaking();
```
当不再需要使用语音播报功能或app退出时，需要注销语音合成功能，注销你可以像下面这样操作：
```
        //销毁语音播报
        SpeechHelper.getInstance().destroy();
```
#### 四.语音播报需要注意的问题
当你调用以下代码：
```
SpeechHelper.getInstance().speak("130");
```
也许你希望它播报的是“一三零”，但遗憾的是它可能播报成“一百三十”，这是一件很尴尬的事，那么当你需要它播成“一三零”的话，你需要在“130”中做下处理，将每个数字间增加空格，及“130”写成“1 3 0”，这样就能播报“一三零”了。

