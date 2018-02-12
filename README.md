### ForMusic

这是一款支持Android系统的本地音乐播放器

### 界面
![主页面](https://gitee.com/uploads/images/2018/0212/142529_a8150762_1193276.png "0115_6-171651-o_1c3sdfpgi1rhpo0d195b3nb3m716-uid-1181704@1080x1920.png")
![播放页面(https://gitee.com/uploads/images/2018/0212/142950_88dd74c2_1193276.png "0115_4-171651-o_1c3sdglbeicuibdf8g9qe12fs1c-uid-1181704@1080x1920.png")
![歌单页面](https://gitee.com/uploads/images/2018/0212/142929_0057ceea_1193276.png "0115_14-171651-o_1c3sdgsj916kf14v61mc9kh01a8d1o-uid-1181704@1080x1920.png")
![主题皮肤页面](https://gitee.com/uploads/images/2018/0212/142824_60828147_1193276.png "0115_5-171651-o_1c3sdgptk1e368ve1qfl18k731i-uid-1181704@1080x1920.png")

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













