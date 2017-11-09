package com.example.ningyuwen.music.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();

        setStatusListener();
        setBroadCastReceiver();
        Log.i(TAG, "onCreate: ");
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
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action){
                    default:
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("ReplaceMusicList");  //替换音乐列表数据，可能在歌手分类，我喜爱的，自定歌单这些页面用到
        registerReceiver(mReceiver, filter);
    }

    /**
     * handler用来接收消息，来发送广播更新播放时间
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
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
        };
    };

    private void setStatusListener() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
    }

    /**
     * 将Service的数据传给Activity
     */
    public interface IServiceDataToActivity {
        String getMusicFilePath(long pid);   //获取音乐文件路径
        void showLyricAtActivity(long pid);  //展示歌词,通过pid查询到文件路径，再解析歌词文件
    }

    /**
     * 播放音乐
     * @param currentTime 当前时间
     */
    private void playMusic(int i, int currentTime) {
        try {
            mMediaPlayer.reset();// 把各项参数恢复到初始状态
            mMediaPlayer.setDataSource(mServiceDataToActivity.getMusicFilePath(mMusicIds.get(i)));
            mMediaPlayer.prepare(); // 进行缓冲
            mMediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mPosition = (mPosition + 1) % mMusicIds.size();
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
        //播放音乐
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
//            isPause = true;
        }else {
            playMusic(mPosition, mCurrentTime);
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
