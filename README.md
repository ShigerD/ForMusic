### ForMusic

这是一款支持Android系统的本地音乐播放器

### 界面
<img src="https://gitee.com/uploads/images/2018/0212/142529_a8150762_1193276.png" width = "450" height = "800" alt="主页面" align=center />
<img src="https://gitee.com/uploads/images/2018/0212/143127_1cfe49b0_1193276.png" width = "450" height = "800" alt="播放页面" align=center />
<img src="https://gitee.com/uploads/images/2018/0212/142929_0057ceea_1193276.png" width = "450" height = "800" alt="歌单页面" align=center />
<img src="https://gitee.com/uploads/images/2018/0212/142824_60828147_1193276.png" width = "450" height = "800" alt="主题皮肤页面" align=center />

### 第三方库
Bugly：用于统计闪退、ANR异常情况，以及更新软件
```
    //其中latest.release指代最新版本号，也可以指定明确的版本号，例如1.2.0
    compile 'com.tencent.bugly:crashreport_upgrade:latest.release'
    //其中latest.release指代最新版本号，也可以指定明确的版本号
    compile 'com.tencent.bugly:nativecrashreport:latest.release'
```
fastjson：用于Json解析
```
    // https://mvnrepository.com/artifact/com.alibaba/fastjson  解析json
    compile 'com.alibaba:fastjson:1.2.39'
```
Glide：图片加载框架
```
    //Glide
    compile 'com.github.bumptech.glide:glide:3.7.0'
    compile 'com.github.bumptech.glide:okhttp3-integration:1.5.0@aar'
```
OkHttp：网络框架
```
    compile 'com.squareup.okhttp3:okhttp:3.8.1'
```
PlayPauseView：按钮播放暂停动画
```
    //播放、暂停
    compile 'com.github.Lauzy:PlayPauseView:1.0.4'
```
android-image-cropper：用于图片裁剪
```
    //剪切
    compile 'com.theartofdev.edmodo:android-image-cropper:2.6.0'
```
极光统计平台
```
    compile 'cn.jiguang.sdk:janalytics:1.1.1'
    compile 'cn.jiguang.sdk:jcore:1.1.8'
```
ButterKnife：依赖注入框架
```
    compile 'com.jakewharton:butterknife:8.5.1'
```
circleImageView：圆形ImageView
```
    compile 'de.hdodenhof:circleimageview:2.2.0'
```













