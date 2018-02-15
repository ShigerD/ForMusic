package com.example.ningyuwen.music.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import java.util.ArrayList;
import java.util.Random;

/**
 * 后台服务，用于播放音乐
 * Created by ningyuwen on 17-9-26.
 */

public class PlayMusicService extends Service implements MainActivity.IServiceDataTrans, AudioManager.OnAudioFocusChangeListener {
    private String TAG = "testni";
    private MediaPlayer mMediaPlayer; // 媒体播放器对象
    private ArrayList<Long> mMusicIds;
    private BroadcastReceiver mReceiver;
    private int mCurrentTime;        //当前播放进度
    private int mPosition;
    private MyBinder myBinder = new MyBinder();             //MyBinder获取PlayMusicService
    private IServiceDataToActivity mServiceDataToActivity;  //接口，负责将数据传给Activity
    private NotificationCompat.Builder mNotificationBuilder;     //builder,用于通知栏数据准备
    private NotificationManager mNotificationManager;               //管理通知
    private String nowPlayMusicName;    //当前播放的音乐名
    private String nowPlayMusicAlbum;           //当前播放的专辑名
    private String nowPlayAlbumPic;             //专辑图片
    private long pid = 0;                           //此pid用于临时记录，第一次进入app时，数据未完全加载，但是有上一次播放结束时的pid
                                                //本次进入暂时 mPosition=0,当开始播放时，判断pid是否不为0,不为0,则说明需要找到真正的mPosition
    //记录一首音乐开始播放的时间，用于统计计数排序，当切换音乐时，用当前时间减去开始播放时间，如果时间大于音乐总长的2/3,则
    //计数加1。，下一次打开app时，在allmusicFragment按照播放次数排序
    private long mPlayMusicStartTime = 0;        //初始化为0

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMusicIds = new ArrayList<>();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    Log.i(TAG, "onCompletion: " + mediaPlayer.getCurrentPosition() + " "
                            + mMediaPlayer.getCurrentPosition() + " " + mServiceDataToActivity
                            .getPlayMusicData(mMusicIds.get(mPosition)).getMusicTime());
                    if (mediaPlayer.getCurrentPosition() < mServiceDataToActivity.getPlayMusicData(
                            mMusicIds.get(mPosition)).getMusicTime() - 5000) {
                        return;
                    }
                    //播放完成
                    calculateThisMusicIsAddCount(mPosition);
                    mPlayMusicStartTime = System.currentTimeMillis();

                    //随机播放还是单曲播放，列表循环的区别主要体现在播放完成时的下一曲 和 手动切换歌曲
                    if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_LIST) {
                        //列表循环
                        mPosition = (mPosition + 1) % mMusicIds.size();
                    } else if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_RANDOM) {
                        //随机播放
                        mPosition = new Random().nextInt(mMusicIds.size()) % (mMusicIds.size() + 1);
                    }

                    refreshNotification();  //通知栏
                    playMusic(0);
                    //这一首音乐播放完成，开始播放下一曲，刷新MainActivity或者PlayActivity
                    //这里用来刷新PopupWindow的信息,改为time为0,则发送消息过去
                    mServiceDataToActivity.sendCompleteMsgToRefreshPop(mPosition);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        handler.sendEmptyMessage(1);

        setBroadCastReceiver();
        //接听电话监听

        pid = getSharedPreferences("notes", MODE_PRIVATE).getLong("lastTimePlayPid", 0);

        nowPlayMusicName = getSharedPreferences("notes", MODE_PRIVATE).getString("lastPlayMusicName","");
        nowPlayMusicAlbum = getSharedPreferences("notes", MODE_PRIVATE).getString("lastPlayMusicAlbum","");
        nowPlayAlbumPic = getSharedPreferences("notes", MODE_PRIVATE).getString("lastPlayMusicPicPath","");
        StaticFinalUtil.SERVICE_PLAY_TYPE_NOW = getSharedPreferences("notes",MODE_PRIVATE).getInt("playType", StaticFinalUtil.SERVICE_PLAY_TYPE_LIST);

        showCustomView(false);
    }

    /**
     * 在切歌时判断此音乐是否需要计数加1
     * 用现在的时间减去开始时间，播放总时间是否大于音乐总长度的 2/3来判定
     * @param position 刚刚播放的那首音乐
     */
    private void calculateThisMusicIsAddCount(int position){
        try {
            mServiceDataToActivity.calculateThisMusicIsAddCount((System.currentTimeMillis() - mPlayMusicStartTime),
                    mMusicIds.get(position), position);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showCustomView(boolean isPlay) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.layout_music_notification);

        remoteViews.setTextViewText(R.id.music_name, nowPlayMusicName); //设置textview
        try {
            if (!"".equals(nowPlayAlbumPic)) {
                remoteViews.setImageViewUri(R.id.iv_music_pic, Uri.parse(nowPlayAlbumPic));   //专辑图
            }
        }catch (Exception e){
            e.printStackTrace();
            remoteViews.setImageViewResource(R.id.iv_music_pic, R.mipmap.ic_play_album);   //专辑图
        }
        remoteViews.setTextViewText(R.id.album_name, nowPlayMusicAlbum);    //专辑名
        if (isPlay){
            //播放
            remoteViews.setImageViewResource(R.id.ic_notification_play_music, R.mipmap.ic_notification_play_music);
        }else {
            //暂停
            remoteViews.setImageViewResource(R.id.ic_notification_play_music, R.mipmap.ic_notification_pause_music);
        }

        //设置按钮事件 -- 发送广播 --广播接收后进行对应的处理
        //播放、暂停
        Intent buttonPlayIntent = new Intent(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY); //----设置通知栏按钮广播
        PendingIntent pendButtonPlayIntent = PendingIntent.getBroadcast(this, 0, buttonPlayIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.ic_notification_play_music, pendButtonPlayIntent);//----设置对应的按钮ID监控

        //下一曲
        Intent buttonPlayIntent1 = new Intent(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT); //----设置通知栏按钮广播
        PendingIntent pendButtonPlayIntent1 = PendingIntent.getBroadcast(this, 0, buttonPlayIntent1, 0);
        remoteViews.setOnClickPendingIntent(R.id.ic_notification_next_music, pendButtonPlayIntent1);//----设置对应的按钮ID监控

        //关闭
        Intent buttonPlayIntent2 = new Intent(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_CLOSE); //----设置通知栏按钮广播
        PendingIntent pendButtonPlayIntent2 = PendingIntent.getBroadcast(this, 0, buttonPlayIntent2, 0);
        remoteViews.setOnClickPendingIntent(R.id.ic_notification_close_music, pendButtonPlayIntent2);//----设置对应的按钮ID监控

        //获取PendingIntent
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setContent(remoteViews).setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setTicker("music is playing")
                .setContentIntent(mainPendingIntent);

        assert mNotificationManager != null;
        mNotificationManager.notify(1, mNotificationBuilder.build());
    }

    /**
     * 监控在声音焦点监听器
     * @param focusChange 表示音频获取焦点的状态
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.e("moneychange", "onAudioFocusChange: "+focusChange  );
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_GAIN://已经获得了音频焦点
                Log.e("moneychange1", "onAudioFocusChange: "+focusChange  );
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    showCustomView(true);
                    mPlayMusicStartTime = System.currentTimeMillis();
                    playMusic(mCurrentTime);
                    //通知MainActivity更新播放暂停动画
                    mServiceDataToActivity.refreshPlayPauseAnimation(true);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS://已经失去音频焦点很长时间
                Log.e("moneychange2", "onAudioFocusChange: "+focusChange  );
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://暂时失去，很快会重新获得，可保持资源，可能很快会重新获得(电话拨打和接听)
                Log.e("moneychange3", "onAudioFocusChange: "+focusChange  );
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    showCustomView(false);
                    //通知MainActivity更新播放暂停动画
                    mServiceDataToActivity.refreshPlayPauseAnimation(false);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://暂时失去，但是可以小声播放（短信）
                Log.e("moneychange4", "onAudioFocusChange: "+focusChange  );
                mMediaPlayer.setVolume(0.1f, 0.1f);
                break;

        }

    }

    /**
     * 广播接收器
     */
    public class ServiceReceiver extends BroadcastReceiver {
        public static final String NOTIFICATION_ITEM_BUTTON_CLOSE
                = "com.example.notification.ServiceReceiver.last";//----通知栏上一首按钮
        public static final String NOTIFICATION_ITEM_BUTTON_PLAY
                = "com.example.notification.ServiceReceiver.play";//----通知栏播放按钮
        public static final String NOTIFICATION_ITEM_BUTTON_NEXT
                = "com.example.notification.ServiceReceiver.next";//----通知栏下一首按钮
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            assert action != null;
            switch (action) {
                case NOTIFICATION_ITEM_BUTTON_PLAY: //----通知栏播放按钮响应事件
                    //播放或暂停
                    //播放按钮变为暂停
                    playOrPause();
                    break;
                case NOTIFICATION_ITEM_BUTTON_NEXT: //----通知栏下一首按钮响应事件
                    //下一曲切歌,计算是否加1
                    calculateThisMusicIsAddCount(mPosition);
                    mPlayMusicStartTime = System.currentTimeMillis();

                    //下一曲,更新通知栏,第一次进入时点击下一曲，需要判断pid
                    if (pid != 0) {
                        mPosition = mServiceDataToActivity.getPositionFromDataOnPid(pid);   //上一次关闭时的位置
                        if (mPosition == -1) {
                            mPosition = 0;
                        }
                        pid = 0;
                    }
                    mPosition = (mPosition + 1) % mMusicIds.size();     //下一曲

                    refreshNotification();
                    playMusic(0);
                    break;
                case NOTIFICATION_ITEM_BUTTON_CLOSE:
                    //关闭进程
                    mNotificationManager.cancel(1);
                    stopSelf();
                    mServiceDataToActivity.exitApp();
//                sendBroadcast(new Intent().setAction(StaticFinalUtil.RECEIVER_CLOSE_APP));
//                MusicApplication.exitApp();
                    break;
            }
            //监听电话拨打和接听
            Log.e("moneyReceiver", "onReceive: "+action );
            if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)||action.equals(Service.TELEPHONY_SERVICE)){
                playOrPause();
            }
            //监听拔掉耳机
            if(action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    showCustomView(false);
                    //通知MainActivity更新播放暂停动画
                    mServiceDataToActivity.refreshPlayPauseAnimation(false);
                }
            }
            //接收耳机插拔广播
            if ("android.intent.action.HEADSET_PLUG".equals(action)){
                //
                if (intent.hasExtra("state")){
                    if (intent.getIntExtra("state", 0) == 0){
//                        Toast.makeText(context, "headset not connected", Toast.LENGTH_SHORT).show();
                        try {
                            if (mMediaPlayer != null) {
                                if (mMediaPlayer.isPlaying()) {
                                    //在播放，则暂停
                                    mMediaPlayer.pause();
                                    showCustomView(false);
                                    //通知MainActivity更新播放暂停动画
                                    mServiceDataToActivity.refreshPlayPauseAnimation(false);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else if (intent.getIntExtra("state", 0) == 1){
//                        Toast.makeText(context, "headset connected", Toast.LENGTH_SHORT).show();
                        try {
                            if (mMediaPlayer != null) {
                                if (!mMediaPlayer.isPlaying()) {
                                    //没有播放，则开始播放
                                    mPlayMusicStartTime = System.currentTimeMillis();
                                    playMusic(mCurrentTime);
                                    showCustomView(true);
                                    //通知MainActivity更新播放暂停动画
                                    mServiceDataToActivity.refreshPlayPauseAnimation(true);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新通知栏，用于在用户点击音乐列表之后的播放 和 点击通知栏的播放
     */
    private void refreshNotification(){
//        MusicApplication.getFixedThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                MusicData musicData = mServiceDataToActivity.getPlayMusicData(mMusicIds.get(mPosition));
//                if (musicData == null){
//                    return;
//                }
//                nowPlayMusicName = musicData.getMusicName();
//                nowPlayMusicAlbum = musicData.getMusicAlbum();
//                nowPlayAlbumPic = musicData.getMusicAlbumPicPath();
//
//                Message message = handler.obtainMessage();
//                message.what = StaticFinalUtil.HANDLER_SHOW_CUSTOM;
//                message.obj = true;
//                message.sendToTarget();
//            }
//        });

        if (mServiceDataToActivity == null){
            return;
        }
        MusicData musicData = mServiceDataToActivity.getPlayMusicData(mMusicIds.get(mPosition));
        if (musicData == null){
            return;
        }
        nowPlayMusicName = musicData.getMusicName();
        nowPlayMusicAlbum = musicData.getMusicAlbum();
        nowPlayAlbumPic = musicData.getMusicAlbumPicPath();
        showCustomView(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
//        if (intent != null && mMusicPaths == null){
//            mMusicPaths = intent.getStringArrayListExtra("musicInfoList");
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setBroadCastReceiver() {
        mReceiver = new ServiceReceiver();//----注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_CLOSE);
        intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
        intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * handler用来接收消息，来发送广播更新播放时间
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            // 1 为每一秒发送过来更新播放时间等数据
            if (msg.what == 1) {
                if(mMediaPlayer != null) {
                    mCurrentTime = mMediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                    handler.sendEmptyMessageDelayed(1, 1000);
                }

            }else if (msg.what == StaticFinalUtil.HANDLER_SHOW_CUSTOM){
                //显示通知栏
                showCustomView((boolean)msg.obj);
            }
        }
    };

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

    /**
     * 播放音乐
     * @param currentTime 当前时间
     */
    private void playMusic(final int currentTime) {
        MusicApplication.getFixedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (mPlayMusicStartTime == 0){
                    mPlayMusicStartTime = System.currentTimeMillis();
                }

                //存储记录，方便下一次进入的开始播放次序
                long id = mMusicIds.get(mPosition);
                getSharedPreferences("notes", MODE_PRIVATE).edit()
                        .putLong("lastTimePlayPid", id)
                        .putString("lastPlayMusicName", nowPlayMusicName)
                        .putString("lastPlayMusicAlbum", nowPlayMusicAlbum)
                        .putString("lastPlayMusicPicPath", nowPlayAlbumPic).apply();

                //这里是个问题，第一次进入app，获取不到mPosition，需要根据pid查询得到mPosition
                if (pid != 0) {
                    mPosition = mServiceDataToActivity.getPositionFromDataOnPid(pid);
//            i = mPosition;
                    if (mPosition == -1){
                        mPosition = 0;
                    }
                    pid = 0;
                }
                try {
                    mMediaPlayer.reset();// 把各项参数恢复到初始状态
                    mMediaPlayer.setDataSource(mServiceDataToActivity.getMusicFilePath(mMusicIds.get(mPosition)));
                    mMediaPlayer.prepare(); // 进行缓冲
//                mMediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
                    mMediaPlayer.start();
                    mMediaPlayer.seekTo(currentTime);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //在playMusic之后再读取歌词文件，因为所有播放音乐的最后一步都是在这里实现的，所以只用写一份代码
                mServiceDataToActivity.showLyricAtActivity(mMusicIds.get(mPosition));
            }
        });
    }

    /**
     * Activity和Service传递数据
     */
    @Override
    public void initServiceData(ArrayList<Long> musicId) {
        if (mMusicIds == null) {
            mMusicIds = new ArrayList<>();
        }
        mMusicIds.clear();
        mMusicIds.addAll(musicId);
    }

    /**
     * 用户点击播放，传入position
     * 2018.01.13记录，因为音乐播放计数需求，还好所有音乐在播放之前都会调用此方法，
     * 所以在这个方法中判断是否切歌
     * @param position i
     */
    @Override
    public void playMusicFromClick(int position) {
        //点击切换歌曲
        calculateThisMusicIsAddCount(mPosition);    //mPosition为之前的一首音乐
        mPlayMusicStartTime = System.currentTimeMillis();

        if (position < 0){
            position = mMusicIds.size()-1;
        }
        if (position == mMusicIds.size()){
            position = 0;
        }
        mPosition = position;
        //pid = 0;
        pid = 0;
        //播放音乐,更新通知栏
        refreshNotification();  //通知栏
        playMusic(0);
    }

    /**
     *
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     *
     */
    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start(); // 开始播放
            if (currentTime > 0) { // 如果音乐不是从头播放
                mMediaPlayer.seekTo(currentTime);
            }
        }
    }

    /**
     * 暂停音乐或者播放音乐，主页面的按钮
     */
    @Override
    public void playOrPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            showCustomView(false);
            //通知MainActivity更新播放暂停动画
            mServiceDataToActivity.refreshPlayPauseAnimation(false);
        }else {
            showCustomView(true);
            mPlayMusicStartTime = System.currentTimeMillis();
            playMusic(mCurrentTime);
            //通知MainActivity更新播放暂停动画
            //playMusic()方法中在最后显示歌词时会刷新动画状态，所以此处不再更新
//            mServiceDataToActivity.refreshPlayPauseAnimation(true);
        }
    }

    /**
     * 修改后台播放列表，传入musicId,当前播放顺序
     * @param musicInfoList musicInfoList 存储musicId
     * @param position 播放第几个
     */
    @Override
    public void replaceBackStageMusicList(ArrayList<Long> musicInfoList, int position) {
        //替换歌单切歌,切换歌单暂时不考虑加1
//        calculateThisMusicIsAddCount(mPosition);
        mPlayMusicStartTime = System.currentTimeMillis();

        if (mMusicIds == null){
            mMusicIds = new ArrayList<>();
        }
        mMusicIds.clear();
        mMusicIds.addAll(musicInfoList);   //pid
        mPosition = position;       //position
        refreshNotification();
        playMusic(0);
    }

    /**
     * 获取播放进度，返回毫秒
     * @return int
     */
    @Override
    public int getMusicPlayTimeStamp() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 获取当前播放的音乐id，查询数据，便于显示
     * @return pid
     */
    @Override
    public long getPlayingMusicId() {
        return mMusicIds.get(mPosition);
    }

    /**
     * 获取播放位置position
     * @return position
     */
    @Override
    public int getPlayPosition() {
        return mPosition;
    }

    @Override
    public boolean isPlayingMusic() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    /**
     * 计算好现在要开始播放的时间，并且将后台的正在播放的时间修改了
     * @param time time 在PopupWindow里进行计算
     */
    @Override
    public void changePlayingTime(int time) {
        mCurrentTime = time;
        mPlayMusicStartTime = System.currentTimeMillis();
        playMusic(mCurrentTime);
    }

    @Override
    public void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(1);
        }
    }

    /**
     * 返回MyBinder对象，在MainActivity中使用bindService之后监听连接成功可以获取IBinder对象，转为MyBinder类型，
     * 再获取PlayMusicService实例，赋值给 ServiceDataTrans
     * @param intent intent
     * @return 返回MyBinder对象
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            unregisterReceiver(mReceiver);
            mMediaPlayer = null;
        }
//        mNotificationManager.cancel(1);
        Log.i(TAG, "onDestroy: ");
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



}
