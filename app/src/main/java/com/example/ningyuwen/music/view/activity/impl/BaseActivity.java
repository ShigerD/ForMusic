package com.example.ningyuwen.music.view.activity.impl;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.presenter.impl.BasePresenter;
import com.example.ningyuwen.music.service.PlayMusicService;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.i.IBaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseActivity,所有activity继承BaseActivity
 * 2018.01.06:做以下修改
 * 之前数据主要保存在MainActivity中,导致数据交互很麻烦，现在将数据保存在BaseActivity中，其他Activity直接从
 * BaseActivity中取出数据
 * Created by ningyuwen on 17-9-22.
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IBaseActivity {
    protected P mPresenter;     //presenter对象，处理逻辑运算和数据存储
    private Snackbar mSnacker;       //toast对象

    public static List<MusicData> mMusicDatas;  //音乐信息,总的音乐数据,涵盖基本信息和记录信息
    public static MainActivity.IServiceDataTrans mServiceDataTrans;  //Activity和Service交互的接口
    public static List<Pair<Long, String>> mTimeAndLyric;   //歌词

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getPresenter();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //设置为强制竖屏
    }

    /**
     * 返回一个持有Activity对象的Presenter对象
     *
     * @return 返回的presenter对象
     * @see #onCreate(Bundle) 中调用了该方法,子类只需要复写,不需要调用
     */
    protected abstract P getPresenter();

    /**
     * 显示Toast,该方法已实现,子类只需要调用即可,短时的Toast.
     *打断之前的toast，在显示短时的toast
     * @param message 要显示的信息
     */

    public void showToast(@NonNull View view, @NonNull String message) {
        if (mSnacker != null){
            mSnacker.dismiss();
        }
//        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mSnacker = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        mSnacker.show();
    }

    /**
     * 设置状态栏透明
     * 适用于DrawerLayout
     *
     * @param drawerLayout drawerLayout
     */
    public void setStatusBarTransparentForDrawerLayout(DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置内容布局属性
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            contentLayout.setFitsSystemWindows(false);
            contentLayout.setClipToPadding(false);
            // 设置抽屉布局属性
            ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
            drawer.setFitsSystemWindows(true);
            drawer.setClipToPadding(true);
            // 设置 DrawerLayout 属性
            drawerLayout.setFitsSystemWindows(false);
        }
    }

    /**
     * 设置状态栏透明
     * 使用于非为DrawerLayout.当以图片作为背景时,图片会铺满全屏
     */
    public void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置状态栏透明
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置跟布局的参数,让布局从状态栏下方开始,而不是跟状态栏重合
            ViewGroup rootView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(false);
            rootView.setClipToPadding(false);
        }
    }

    public void setStatusBarTrans(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    private int getStatusBarHeight() {
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return this.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取一个和状态栏大小相同的View
     *
     * @param color view的颜色,ARGB值
     * @return view
     */
    public View createStatusBarView(int color) {
        //绘制一个和状态栏大小相同的view
        View statusView = new View(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);              //设置背景色
        return statusView;
    }

    /**
     * 设置状态栏颜色
     * 适用于非DrawerLayout,布局包含toolbar时，可以将状态栏设置为何toolbar相同的颜色
     *
     * @param color 设置的状态栏的颜色ARGB值
     */
    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置状态栏透明
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View statusView = createStatusBarView(color);  //生成一个和状态栏一样大小的View
            ViewGroup decorView = (ViewGroup) this.getWindow().getDecorView(); //获取布局容器
            decorView.addView(statusView);  //添加View到布局中
            //设置跟布局的参数,让布局从状态栏下方开始,而不是跟状态栏重合
            ViewGroup rootView = (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * 获取权限
     */
    public void getReadPermissionAndGetInfoFromSD() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions,
                        StaticFinalUtil.MUSIC_FILE_PERMISSION);
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                //没有该权限，返回
                return;
            }
            //有权限，直接读取SD卡中的数据
            getMusicInfoFromSD();
        }
    }

    /**
     * 从SD卡扫描音乐文件并存储到数据库
     */
    public void getMusicInfoFromSD() {
        mMusicDatas.clear();

        //查询媒体数据库
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if (cursor != null && cursor.moveToFirst()) {
            List<MusicBasicInfo> musicBasicInfos = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲标题
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //专辑id
                String albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (size > 1024 * 800) {//大于800K
                    MusicBasicInfo data = new MusicBasicInfo();
                    data.setPId((long) id);
                    data.setMusicName(tilte);
                    data.setMusicPlayer(artist);
                    data.setMusicAlbum(album);
                    data.setMusicFilePath(url);
                    data.setMusicTime(duration);
                    data.setMusicFileSize(size);
                    data.setMusicAlbumId(Long.valueOf(albumId));
                    musicBasicInfos.add(data);   //保存到基本信息List，存储到数据库，其他信息不变
                }
                cursor.moveToNext();
            }
            cursor.close();
            //存储数据到数据库，两张表
            mPresenter.saveMusicInfoFromSD(musicBasicInfos);

            //保存基本信息之后,匹配歌词文件路径
            try {
                mPresenter.scanLyricFileFromSD();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "run: 异常2");
            }

            //从musicBasicInfos 和 数据库读取数据到 mMusicDatas
            mMusicDatas.addAll(mPresenter.getMusicAllInfoFromBasic(musicBasicInfos));
//            sendBroadCastForString("AllMusicRefresh");
            //显示音乐信息
            showMusicInfoAtActivity(StaticFinalUtil.HANDLER_REFRESH_MUSIC);
            startPlayMusicService();
        }
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

    /**
     * 将Service的数据传给Activity
     */
    private PlayMusicService.IServiceDataToActivity mServiceDataToActivity = new PlayMusicService.IServiceDataToActivity() {

        //获取音乐文件路径
        @Override
        public String getMusicFilePath(long pid) {
            try {
                return mPresenter.getMusicDataUsePid(pid).getMusicFilePath();
            }catch (NullPointerException e){
                e.printStackTrace();
                return "";
            }
        }

        //展示歌词,通过pid查询到文件路径，再解析歌词文件
        @Override
        public void showLyricAtActivity(long pid) {
            showLyricOnActivity(pid);
        }

        /**
         * 获取MusicData,展示通知栏时需要获取专辑图片,音乐名和专辑名
         * @param pid pid
         * @return MusicData
         */
        @Override
        public MusicData getPlayMusicData(long pid) {
            return getDataFromPid(pid);
        }

        @Override
        public int getPositionFromDataOnPid(long pid) {
            return getPositionFromPid(pid);
        }

        @Override
        public void refreshPlayPauseAnimation(boolean play) {
//            refreshPlayPauseView(play);
            refreshPlayPauseView(play);
        }
    };

    /**
     * BaseActivity数据修改之后通知子类修改数据,布局
     */
    public abstract void showLyricOnActivity(long pid);
    public abstract void refreshPlayPauseView(boolean play);
    public abstract void showMusicInfoAtActivity(int what);

    /**
     * 获取歌单中某首歌曲的位置
     * @param pid pid
     * @return position
     */
    public int getPositionFromPid(long pid){
        for (int i = 0;i < mMusicDatas.size();i++){
            if (mMusicDatas.get(i).getpId() == pid){
                return i;
            }
        }
        return 0;
    }

    /**
     * 設置是否喜愛，在所有歌曲頁面和我喜愛的音樂頁面都有用到
     * @param pid pid
     * @param isLove true,false
     */
    @Override
    public void setIsLoveToDB(long pid, boolean isLove) {
        MusicData data = getDataFromPid(pid);
        if (data == null){
            return;
        }
        data.setLove(isLove);
        mPresenter.setIsLoveToDB(pid, isLove);
    }

    /**
     * 添加我喜愛的音乐，需要从一个pid得到音乐数据
     * @param pid pid
     * @return musicdata
     */
    @Override
    public MusicData getDataFromPid(long pid) {
        if (mMusicDatas == null){
            return null;
        }
        for (int i = 0;i < mMusicDatas.size();i++){
            if (mMusicDatas.get(i).getpId() == pid){
                return mMusicDatas.get(i);
            }
        }
        return null;
    }

    /**
     * 初始化Service的数据，使用接口回调
     */
    private void initServiceData() throws NullPointerException{
        if (mMusicDatas == null){
            return;
        }
        ArrayList<Long> musicId = new ArrayList<>();
        for (int i = 0; i < mMusicDatas.size();i++){
            musicId.add(i, mMusicDatas.get(i).getpId());
        }

        //传输初始化数据，音乐路径List
        mServiceDataTrans.initServiceData(musicId);
    }

    private static String TAG = "ningyuwen";

}
