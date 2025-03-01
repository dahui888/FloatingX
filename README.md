# FloatingX



![image-20210810161316095](https://tva1.sinaimg.cn/large/008i3skNly1gtbrg85hlhj61040k80ui02.jpg)

[![](https://jitpack.io/v/Petterpx/FloatingX.svg)](https://jitpack.io/#Petterpx/FloatingX) [![Scan with Detekt](https://github.com/Petterpx/FloatingX/actions/workflows/detekt-analysis.yml/badge.svg)](https://github.com/Petterpx/FloatingX/actions/workflows/detekt-analysis.yml) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/) 

**FloatingX** 一个灵活且强大的 `免权限` 悬浮窗解决方案。

[中文文档](https://github.com/Petterpx/FloatingX/wiki)

## 👏 特性 

- 单例持有浮窗view
- 支持各项回调监听
- 链式调用，无感插入
- 支持自定义是否保存历史位置及还原
- 支持插入 `ViewGroup` , `Fragment` , `Activity`
- 允许自定义悬浮窗各项指标，自定义隐藏显示动画
- 支持 越界回弹，多指触摸，小屏适配，屏幕旋转
- 支持自定义位置方向,自带辅助定位显示坐标
- 完善的 `kotlin` 构建扩展,及对 `Java` 的友好兼容
- 支持显示位置[强行修复],应对特殊机型(需要单独开启)
- 完善的日志系统，打开即可看到不同级别的Fx运行过程,更利于发现问题
- ...

## 👨‍💻‍ 依赖方式

#### Gradle

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```groovy
dependencies {
	        implementation 'com.github.Petterpx:FloatingX:1.0-rc07'
}
```



## 🏄‍♀️ 效果图

| 全屏,activity,fragment,单view                                | 小屏展示                                                     | 非正常比例缩放屏幕                                           |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![效果-展示1](https://github.com/Petterpx/FloatingX/blob/master/image/fx-api-simple.gif) | ![演示-小屏](https://github.com/Petterpx/FloatingX/blob/master/image/fx-small-gif.gif) | ![非正常比例缩放](https://github.com/Petterpx/FloatingX/blob/master/image/fx-view-deformed-simple.gif) |

| 屏幕旋转                                                     | 功能演示                                                     |      |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ---- |
| ![演示-旋转](https://github.com/Petterpx/FloatingX/blob/master/image/fx-rotate-simple.gif) | ![演示-局部功能](https://github.com/Petterpx/FloatingX/blob/master/image/fx-api-simple.gif) |      |

### 完善的日志-查看器

开启日志查看器，将看到Fx整个运行轨迹，更便于发现问题以及追踪解决。同时支持自定义日志tag

| App                                                          | Activity                                                     | ViewGroup                                                    |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![image-20210808123000851](https://tva1.sinaimg.cn/large/008i3skNly1gtbk1ujkqfj31160s8444.jpg) | ![image-20210808123414921](https://tva1.sinaimg.cn/large/008i3skNly1gt99vralyqj313o0r4jwk.jpg) | ![image-20210808123553402](https://tva1.sinaimg.cn/large/008i3skNly1gt99xfpfwgj311y0jctc8.jpg) |



## 👨‍🔧‍ 使用方式

### 全局悬浮窗管理

**kt**

```kotlin
FloatingX.init {
        setContext(this@CustomApplication)
        setLayout(R.layout.item_floating_new)
  			addBlackClass(
                MainActivity::class.java,
                NewActivity::class.java,
                ImmersedActivity::class.java
         )
  			//只有调用了show,才会监听app-lifecycle,后续会自动插入activity中
        show()
}
```

**Java**

```java
AppHelper helper = AppHelper.builder()
        .setContext(application)
        .setLayout(R.layout.item_floating)
        .build();
FloatingX.init(helper);
```



### 局部悬浮窗管理

#### 通用创建方式

**kt**

```kotlin
ScopeHelper.builder {
  setLayout(R.layout.item_floating)
}.toControl(activity)
```

**kt && java**

```kotlin
ScopeHelper.builder()
            .setLayout(R.layout.item_floating)
            .build()
            .toControl(activity)
            .toControl(fragment)
            .toControl(viewgroup)
```

#### 对kt的扩展支持

##### activity创建悬浮窗

```kotlin
private val activityFx by activityToFx(activity) {
    setLayout(R.layout.item_floating)
}
```

##### fragment创建悬浮窗

```kotlin
private val fragment by fragmentToFx(fragment) {
    setLayout(R.layout.item_floating)
}
```

##### viewGroup创建悬浮窗

```kotlin
private val viewFx by createFx({
        init(viewGroup)
    }) {
        setLayout(R.layout.item_floating)
        setEnableLog(true, "main_fx")
    }
```

##### 快速创建任意作用域悬浮窗

```kotlin
private val customCreateFx by createFx {
    setLayout(R.layout.item_floating)
    build().toControl(activity)
    build().toControl(fragment)
    build().toControl(viewgroup)
}
```

## 🤔 技术实现

> **App** 级别悬浮窗 基于 `DecorView` 的的实现方案，全局持有一个单独的悬浮窗 `View` ,通过 `AppLifecycle` 监听 `Activity` 生命周期，并在相应时机 插入到 `DecorView` 上 ;
>
> **View** 级别悬浮窗，基于给定的 `ViewGroup` ;
>
> **Fragment** 级别，基于其对应的 `rootView` ;
>
> **Acrtivity** 级别,基于 `DecorView` 内部的 `R.id.content` ;

具体如下：

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gr20ks7780j30rc0i5dim.jpg" alt="Activity-setContentView"  />

具体见我的博客：[源码分析 | Activity-setContentView](https://juejin.cn/post/6897453195342610445) 

Ps: 为什么App级别悬浮窗 要插入到 `DecorView` ,而不是 **R.id.content** -> `FrameLayout` ?

> 插入到 `DecorView` 可以最大程度控制悬浮窗的自由度，即悬浮窗可以真正意义上[`全屏`]拖动。
>
> 插入到 `content` 中,其拖动范围其实为 **应用视图范围** ,即摆放位置 受到 **状态栏** 和 **底部导航栏** 以及 默认的 `AppBar` 影响, 比如当用户隐藏了状态栏或者导航栏，相对应的视图大小会发生改变，将影响悬浮窗的位置摆放。



## 👍 感谢

基础 **悬浮窗View** 源自 [EnFloatingView](https://github.com/leotyndale/EnFloatingView) 的 [FloatingMagnetView](https://github.com/leotyndale/EnFloatingView/blob/master/floatingview/src/main/java/com/imuxuan/floatingview/FloatingMagnetView.java) 实现方式，并在其基础上增加了一些改进。

对于导航栏的测量部分代码来自，wenlu,并在其之上增加了更多适配，已覆盖市场95%机型，可以说是目前能搜到的唯一可以准确测量的工具。
