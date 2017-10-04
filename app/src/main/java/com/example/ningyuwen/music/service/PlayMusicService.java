package com.example.ningyuwen.music.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ningyuwen.music.model.entity.music.MusicData;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台服务，用于播放音乐
 * Created by ningyuwen on 17-9-26.
 */

public class PlayMusicService extends Service {
    private String TAG = "testni";
    private MediaPlayer mMediaPlayer; // 媒体播放器对象
    private ArrayList<String> mMusicPaths;
    private byte mPlayStatus = 0;   // 0:列表循环  1:列表播放一次  2：随即播放  3：单曲循环
    private BroadcastReceiver mReceiver;
    private int mCurrentTime;        //当前播放进度
    private int mPosition;

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
        if (mMusicPaths == null){
            mMusicPaths = intent.getStringArrayListExtra("musicInfoList");
        }
        Log.i(TAG, "onStartCommand: " + mMusicPaths.size());

        return super.onStartCommand(intent, flags, startId);
    }

    private void setBroadCastReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action){
                    case "PlayMusic":
                        int i = intent.getIntExtra("palyPosition", 0);
                        mPosition = i;
                        Log.i(TAG, "onReceive: ttttt  " + i);
                        //播放音乐
                        playMusic(i, 0);
                        break;
                    case "ChangePlayStatus":

                        break;
                    case "PlayOrPause":
                        //点击主页面的播放暂停按钮，判断当前播放状态，为播放就暂停
                        playOrPause();
                        break;
                    default:
                        break;
                }

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("PlayMusic");
        filter.addAction("ChangePlayStatus");
        filter.addAction("PlayOrPause");
        registerReceiver(mReceiver, filter);
    }

    /**
     * handler用来接收消息，来发送广播更新播放时间
     */
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
     * 播放音乐
     * @param currentTime 当前时间
     */
    private void playMusic(int i, int currentTime) {
        try {
            mMediaPlayer.reset();// 把各项参数恢复到初始状态
            mMediaPlayer.setDataSource(mMusicPaths.get(i));
            mMediaPlayer.prepare(); // 进行缓冲
            mMediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mPosition = (mPosition + 1) % mMusicPaths.size();
                    playMusic(mPosition, 0);
                }
            });

            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//            Intent intent = new Intent();
//            intent.setAction(MUSIC_DURATION);
//            duration = mediaPlayer.getDuration();
//            intent.putExtra("duration", duration);  //通过Intent来传递歌曲的总长度
//            sendBroadcast(intent);
        }
    }

    /**
     * 暂停音乐或者播放音乐，主页面的按钮
     */
    private void playOrPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
//            isPause = true;
        }else {
            playMusic(mPosition, mCurrentTime);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            unregisterReceiver(mReceiver);
            mMediaPlayer = null;
        }
        Log.i(TAG, "onDestroy: ");
    }
}
