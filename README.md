# MediaLibrary

[![](https://jitpack.io/v/ShaoqiangPei/MediaLibrary.svg)](https://jitpack.io/#ShaoqiangPei/MediaLibrary)

## 简介
MediaLibrary是一个媒体工具库

## 使用说明
### 一. 库依赖
在你`project`对应的`buid.gradle`中添加如下代码：
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
在你要使用的`module`对应的`buid.gradle`中添加如下代码(以0.0.1版本为例)：
```
	dependencies {
	        implementation 'com.github.ShaoqiangPei:MediaLibrary:0.0.1'
	}
```
在你项目的自定义Application类中初始化本库：
```
  //初始化mediaLibrary
  MediaConfig.getInstance().init(this)
          .setDebug(true);//是否开启本库内部log打印(默认false,不开启)
```
### 二. 主要功能类
[SpeechHelper](https://github.com/ShaoqiangPei/MediaLibrary/blob/master/readme/SpeechHelper%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ———— 语音合成工具类,主要用于将文字转语音  


