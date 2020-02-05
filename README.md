# MediaLibrary
媒体工具库

[![](https://jitpack.io/v/ShaoqiangPei/MediaLibrary.svg)](https://jitpack.io/#ShaoqiangPei/MediaLibrary)

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

