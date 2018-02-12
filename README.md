### ForMusic

这是一款支持Android系统的本地音乐播放器

### 界面
<img src="http://image.coolapk.com/apk_image/2018/0115/0115_6-171651-o_1c3sdfpgi1rhpo0d195b3nb3m716-uid-1181704@1080x1920.png.t.jpg" width = "270" height = "480" alt="主页面" align=center />
<img src="http://image.coolapk.com/apk_image/2018/0115/0115_4-171651-o_1c3sdglbeicuibdf8g9qe12fs1c-uid-1181704@1080x1920.png.t.jpg" width = "270" height = "480" alt="播放页面" align=center />
<img src="http://image.coolapk.com/apk_image/2018/0115/0115_14-171651-o_1c3sdgsj916kf14v61mc9kh01a8d1o-uid-1181704@1080x1920.png.t.jpg" width = "270" height = "480" alt="歌单页面" align=center />
<img src="http://image.coolapk.com/apk_image/2018/0115/0115_5-171651-o_1c3sdgptk1e368ve1qfl18k731i-uid-1181704@1080x1920.png.t.jpg" width = "270" height = "480" alt="主题皮肤页面" align=center />

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

### 实现方案
1. 音乐数据来源：扫描手机中的音乐数据
1. 音乐歌词来源：扫描本地歌词文件数据，暂时只支持从网易云音乐下载的音乐
1. 主页面：ViewPager + 4个Fragment实现
1. 播放页面：由之前的Activity改为PopupWindow实现，提高了加载效率，中间的转盘使用ViewPager加ImageView，音乐在播放且PopupWindow在显示时，转动ImageView
1. 播放音乐：使用Service在后台播放
1. 音乐播放次数统计排序：当前播放音乐总时长占当前音乐总时长的2/3，则代表音乐播放了一次，计数加1，下次打开App时按照音乐播放次数进行排序显示
1. 数据交互：
- Activity与Service之间数据交互：    
全部采用接口调用，bindService时传入ServiceConnection接口的实例对象，当成功绑定Service时，回调ServiceConnection接口中的onServiceConnected()，传入参数中有IBinder，即为PlayMusicService中onBind方法返回的IBinder对象。接着调用MyBinder类中的getService()获取PlayMusicService实例对象，因为PlayMusicService实现了IServiceDataTrans接口，所以后面可以通过mServiceDataTrans调用接口IServiceDataTrans中的方法。    
继续使用myBinder.setIServiceDataToActivity(mServiceDataToActivity); 传入IServiceDataToActivity接口在Activity中的实例对象mServiceDataToActivity，之后PlayMusicService可通过类中的IServiceDataToActivity接口对象mServiceDataToActivity调用在Activity中实现了本接口的方法，从而实现数据传递。

Activity中代码：
```
     /**
     * Activity和Service传递数据
     */
    public interface IServiceDataTrans{
        void initServiceData(ArrayList<Long> musicId);  //初始化Service的数据，音乐路径
        void playMusicFromClick(int position);              //用户点击播放，传入position
        void playOrPause();                                 //播放或暂停
        void replaceBackStageMusicList(ArrayList<Long> musicInfoList, int position);//修改后台播放列表，传入musicId,当前播放顺序
        int getMusicPlayTimeStamp();                        //获取播放进度，返回毫秒
        long getPlayingMusicId();           //获取当前播放的音乐id，查询数据，便于显示
        int getPlayPosition();              //获取播放位置position
        boolean isPlayingMusic();           //获取音乐播放状态，播放或者暂停
        void changePlayingTime(int time);    //计算好现在要开始播放的时间，并且将后台的正在播放的时间修改了
        void cancelNotification();      //关闭状态栏
    }

    /**
     * 初始化activity时启动服务，服务可能因为内存回收而自动关闭
     */
    public void startPlayMusicService(){
        Intent intent = new Intent(BaseActivity.this, PlayMusicService.class);
        bindService(intent, mServiceConnection,  Context.BIND_AUTO_CREATE);
    }

    /**
     * 后台播放音乐Service，使用bindService启动，方便传输数据，startService不方便传输数据
     * 当Service
     */
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //使用MyBinder类获取PlayMusicService对象，
            PlayMusicService.MyBinder myBinder = (PlayMusicService.MyBinder)service;
            mServiceDataTrans = myBinder.getService();
            //设置Service对Activity的监听回调
            myBinder.setIServiceDataToActivity(mServiceDataToActivity);

            Log.i(TAG, "onServiceConnected: initServiceData");
            //初始化Service的数据，使用接口回调
            try {
                initServiceData();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceConnected: transData33");
        }
    };
```        
Service中代码：    
```
    /**
     * 将Service的数据传给Activity
     */
    public interface IServiceDataToActivity {
        String getMusicFilePath(long pid);   //获取音乐文件路径
        void showLyricAtActivity(long pid);  //展示歌词,通过pid查询到文件路径，再解析歌词文件
        MusicData getPlayMusicData(long pid);   //获取MusicData,展示通知栏时需要获取专辑图片,音乐名和专辑名
        int getPositionFromDataOnPid(long pid);  //根据pid查询歌曲在歌单中的位置，第一次进入app时需要用pid查询到mPosition
        void refreshPlayPauseAnimation(boolean play);   //更新主页面的播放暂停动画
        void sendCompleteMsgToRefreshPop(int position);     //歌曲播放完成，向Activity发送通知，更新PopupWindow
        void calculateThisMusicIsAddCount(long playtime, long pid, int position);    //用于计数排序
        void exitApp();
    }

    // IBinder是远程对象的基本接口，是为高性能而设计的轻量级远程调用机制的核心部分。但它不仅用于远程
    // 调用，也用于进程内调用。这个接口定义了与远程对象交互的协议。
    // 不要直接实现这个接口，而应该从Binder派生。
    // Binder类已实现了IBinder接口
    public class MyBinder extends Binder {

        /** * 获取Service的方法 * @return 返回PlayerService */
        public PlayMusicService getService(){
            return PlayMusicService.this;
        }

        /**
         * 传递Activity的context,绑定监听对象
         * @param serviceDataToActivity serviceDataToActivity
         */
        public void setIServiceDataToActivity(IServiceDataToActivity serviceDataToActivity){
            mServiceDataToActivity = serviceDataToActivity;
        }
    }
```    
- Fragment与Activity之间：    
1、Fragment中可以通过getActivity()获取到Activity的实例之后强转成MainActivity，调用MainActivity中的方法    
2、Activity中之前创建Fragment时已经获取到了几个Fragment的实例化对象，可以直接用对象调用Fragment中的方法或者每个Fragment实现同一个接口，因为Activity有这些Fragment的实例化对象，所以可以直接强转为这个接口，再调用接口中的方法，我这里使用的是第二种    
       
- Fragment与Fragment之间：    
通过Activity中转

### 下载    
酷安应用市场：[点我下载](https://www.coolapk.com/apk/com.example.ningyuwen.music)    
    
    






