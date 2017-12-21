package com.example.ningyuwen.music.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.ningyuwen.music.util.DensityUtil;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import java.util.ArrayList;

/**
 * 后台服务，用于播放音乐
 * Created by ningyuwen on 17-9-26.
 */

public class PlayMusicService extends Service implements MainActivity.IServiceDataTrans {
    private String TAG = "testni";
    private MediaPlayer mMediaPlayer; // 媒体播放器对象
    private ArrayList<Long> mMusicIds;
    private byte mPlayStatus = 0;   // 0:列表循环  1:列表播放一次  2：随即播放  3：单曲循环
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

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();

        setBroadCastReceiver();

        pid = getSharedPreferences("notes", MODE_PRIVATE).getLong("lastTimePlayPid", 0);

        nowPlayMusicName = getSharedPreferences("notes", MODE_PRIVATE).getString("lastPlayMusicName","");
        nowPlayMusicAlbum = getSharedPreferences("notes", MODE_PRIVATE).getString("lastPlayMusicAlbum","");
        nowPlayAlbumPic = getSharedPreferences("notes", MODE_PRIVATE).getString("lastPlayMusicPicPath","");

        showCustomView(false);
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
            if (action.equals(NOTIFICATION_ITEM_BUTTON_CLOSE)) {//----通知栏播放按钮响应事件
                //关闭进程
                mNotificationManager.cancel(1);
                stopSelf();
                MusicApplication.exitApp();
            } else if (action.equals(NOTIFICATION_ITEM_BUTTON_PLAY)) {//----通知栏播放按钮响应事件
                //播放或暂停
                //播放按钮变为暂停
                playOrPause();
            } else if (action.equals(NOTIFICATION_ITEM_BUTTON_NEXT)) {//----通知栏下一首按钮响应事件
                //下一曲,更新通知栏,第一次进入时点击下一曲，需要判断pid
                if (pid != 0){
                    mPosition = mServiceDataToActivity.getPositionFromDataOnPid(pid);   //上一次关闭时的位置
                    pid = 0;
                }
                mPosition = (mPosition + 1) % mMusicIds.size();     //下一曲
                refreshNotification();
                playMusic(mPosition, 0);
            }
        }
    }

    /**
     * 更新通知栏，用于在用户点击音乐列表之后的播放 和 点击通知栏的播放
     */
    private void refreshNotification(){
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

//                    Intent intent = new Intent();
//                    intent.setAction(MUSIC_CURRENT);
//                    intent.putExtra("currentTime", currentTime);
//                    sendBroadcast(intent); // 给PlayerActivity发送广播
                    handler.sendEmptyMessageDelayed(1, 1000);
                }

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
    }

    /**
     * 播放音乐
     * @param currentTime 当前时间
     */
    private void playMusic(int i, int currentTime) {
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
            i = mPosition;
            pid = 0;
        }

        try {
            mMediaPlayer.reset();// 把各项参数恢复到初始状态
            mMediaPlayer.setDataSource(mServiceDataToActivity.getMusicFilePath(mMusicIds.get(i)));
            mMediaPlayer.prepare(); // 进行缓冲
            mMediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mPosition = (mPosition + 1) % mMusicIds.size();
                    refreshNotification();  //通知栏
                    playMusic(mPosition, 0);
                }
            });

            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //在playMusic之后再读取歌词文件，因为所有播放音乐的最后一步都是在这里实现的，所以只用写一份代码
        mServiceDataToActivity.showLyricAtActivity(mMusicIds.get(i));
    }

    /**
     * Activity和Service传递数据
     */
    @Override
    public void initServiceData(ArrayList<Long> musicId) {
        mMusicIds = new ArrayList<>();
        mMusicIds = musicId;
    }

    /**
     * 用户点击播放，传入position
     * @param position i
     */
    @Override
    public void playMusicFromClick(int position) {
        mPosition = position;
        //pid = 0;
        pid = 0;
        //播放音乐,更新通知栏
        refreshNotification();  //通知栏
        playMusic(mPosition, 0);
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
            playMusic(mPosition, mCurrentTime);
            //通知MainActivity更新播放暂停动画
            mServiceDataToActivity.refreshPlayPauseAnimation(true);
        }
    }

    /**
     * 修改后台播放列表，传入musicId,当前播放顺序
     * @param musicInfoList musicInfoList 存储musicId
     * @param position 播放第几个
     */
    @Override
    public void replaceBackStageMusicList(ArrayList<Long> musicInfoList, int position) {
        if (mMusicIds == null){
            mMusicIds = new ArrayList<>();
        }
        mMusicIds.clear();
        mMusicIds = musicInfoList;   //pid
        mPosition = position;       //position
        playMusic(mPosition, 0);
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
