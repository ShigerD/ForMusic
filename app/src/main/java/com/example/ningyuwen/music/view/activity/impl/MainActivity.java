package com.example.ningyuwen.music.view.activity.impl;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.classify.ClassifyMusicPlayer;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.lyric.Lyric;
import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.presenter.impl.MainActivityPresenter;
import com.example.ningyuwen.music.service.PlayMusicService;
import com.example.ningyuwen.music.util.FastBlurUtil;
import com.example.ningyuwen.music.util.NotificationsUtils;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.i.IMainActivity;
import com.example.ningyuwen.music.view.adapter.MainFragmentAdapter;
import com.example.ningyuwen.music.view.fragment.impl.AllMusicFragment;
import com.example.ningyuwen.music.view.fragment.impl.ClassifyMusicFragment;
import com.example.ningyuwen.music.view.fragment.impl.CustomizeMusicFragment;
import com.example.ningyuwen.music.view.fragment.impl.MyLoveMusicFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页面，音乐播放，扫描音乐等
 * Created by ningyuwen on 17-9-22.
 */

public class MainActivity extends BaseActivity<MainActivityPresenter> implements
        View.OnClickListener , IMainActivity{
    private List<MusicData> mMusicDatas;
//    public static List<MusicData> mMusicDatas;
    private DrawerLayout mDrawerMenu;
    private ViewPager mMainViewPager;
    private ArrayList<Fragment> fragments;
    private ImageView mIvBg;
    private TabLayout mTabLayout;
    public static final String NOTIFICATION_CHANNEL_ID = "4655";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getNotification();

        //绑定控件和设置监听
        findView();
        setMainActivityBg();
        setListener();
        setStatusBarTransparentForDrawerLayout(mDrawerMenu);
        //初始化fragment
        initPage();
        //默认第一页
        mMainViewPager.setCurrentItem(0);

        //线程池，添加一个任务
        MusicApplication.getFixedThreadPool().execute(runnable);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "my_channel_01";
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
        // The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name,importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);

        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an ID for the notification, so it can be updated.
        int notifyID = 1;
        // The id of the channel.
        String CHANNEL_ID = "my_channel_01";
        // Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(MainActivity.this)
                .setContentTitle("New Message")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.mipmap.ic_account_circle_white_24dp)
                .setChannelId(CHANNEL_ID)
                .build();
// Issue the notification.
        mNotificationManager.notify(notifyID, notification);

    }

    /**
     * 获取去音乐数据，数据库有则获取数据库中的数据，数据库没有则扫描SD卡中的数据，再启动服务 startPlayMusicService
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mMusicDatas = new ArrayList<>();
            //先检查数据库中是否有数据，若有则读取数据库中的数据
            mMusicDatas = mPresenter.getMusicBasicInfoFromDB();

            //如果返回数据为空，从SD卡读取数据
            if (mMusicDatas.size() == 0){
                //获取读写权限，并获取音乐数据，存储到数据库，和 存储到 mMusicDatas
                getReadPermissionAndGetInfoFromSD();
            }else {
                //fragment更新数据
                sendBroadCastForString("AllMusicRefresh");
                startPlayMusicService();
            }
        }
    };

    /**
     * 设置背景
     */
    private void setMainActivityBg() {
        //拿到初始图
        Bitmap initBitmap = FastBlurUtil.drawableToBitmap(getResources().getDrawable(R.drawable.pic_main_bg));
        //处理得到模糊效果的图
        int scaleRatio = 5;
        int blurRadius = 8;
        initBitmap = Bitmap.createScaledBitmap(initBitmap,
                initBitmap.getWidth() / scaleRatio,
                initBitmap.getHeight() / scaleRatio,
                false);
        initBitmap = FastBlurUtil.doBlur(initBitmap, blurRadius, false);
        mIvBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mIvBg.setImageBitmap(initBitmap);


//        Glide.with(this)
//                .load(R.drawable.pic_main_bg)
//                .into(mIvBg);

    }

    /**
     * 初始化activity时启动服务，服务可能因为内存回收而自动关闭
     */
    public void startPlayMusicService(){
        Intent intent = new Intent(MainActivity.this, PlayMusicService.class);
        if (mMusicDatas == null){
            return;
        }
        ArrayList<String> musicPath = new ArrayList<>();
        for (int i = 0; i < mMusicDatas.size();i++){
            musicPath.add(i, mMusicDatas.get(i).getMusicFilePath());
        }
        intent.putStringArrayListExtra("musicInfoList", musicPath);
        startService(intent);
    }

    /**
     * 用户点击了音乐，需要在后台播放
     * @param position 点击位置position
     */
    public void playMusicOnBackstage(int position){
        Intent intent = new Intent("PlayMusic");
        intent.putExtra("palyPosition", position);
        sendBroadcast(intent);
    }

    private void findView() {
        mDrawerMenu = (DrawerLayout) findViewById(R.id.dr_main);              //侧滑菜单布局
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        mMainViewPager = (ViewPager) findViewById(R.id.vp_main_page);         //主页面的viewpager
        mIvBg = (ImageView) findViewById(R.id.iv_main_activity_bg);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        findViewById(R.id.iv_bar_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicApplication.getFixedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
//                        //重新导入音乐数据，查看权限并扫描SD卡
//                        getReadPermissionAndGetInfoFromSD();
//                        //发广播，更新四个fragment里面的数据
////                        sendBroadcast(new Intent("RefreshMusicData"));


                        try {
                            mPresenter.scanLyricFileFromSD();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i(TAG, "run: 异常2");
                        }

                    }
                });
            }
        });
    }

    private void setListener() {
        findViewById(R.id.iv_bar_slide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerMenu.openDrawer(GravityCompat.START);
            }
        });
//        findViewById(R.id.tv_tab_first).setOnClickListener(this);
//        findViewById(R.id.tv_tab_second).setOnClickListener(this);
//        findViewById(R.id.tv_tab_third).setOnClickListener(this);
//        findViewById(R.id.tv_tab_last).setOnClickListener(this);
        findViewById(R.id.iv_music_pic).setOnClickListener(this);
        //viewpager页码变化监听
        mMainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                showToast(String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化ViewPager的页面.
     * ViewPager内部是Fragment
     */
    private void initPage() {
        fragments = new ArrayList<>();  //Fragment的List
        fragments.add(new AllMusicFragment());                 //添加需要展示的Fragment
        fragments.add(new CustomizeMusicFragment());
        fragments.add(new MyLoveMusicFragment());
        fragments.add(new ClassifyMusicFragment());
        MainFragmentAdapter mainFragmentAdapter =           //ViewPager的适配器
                new MainFragmentAdapter(getSupportFragmentManager(), fragments);
        mMainViewPager.setAdapter(mainFragmentAdapter);        //设置ViewPager的适配器
        mTabLayout.setupWithViewPager(mMainViewPager);
    }

    @Override
    protected MainActivityPresenter getPresenter() {
        return new MainActivityPresenter(this);
    }

    /**
     * 获取权限
     */
    private void getReadPermissionAndGetInfoFromSD() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StaticFinalUtil.MUSIC_FILE_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //获取权限失败
                    showToast(mIvBg, "权限获取失败,可到设置中打开存储权限");
                } else {
                    //获取到了权限
                    showToast(mIvBg, "获取权限成功");
                    //从SD卡扫描音乐文件
                    getMusicInfoFromSD();
                }
            }
        }
    }

    private String TAG = "test";

    /**
     * 从SD卡扫描音乐文件并存储到数据库
     */
    private void getMusicInfoFromSD() {
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

                    musicBasicInfos.add(data);   //保存到基本信息List，存储到数据库，其他信息不变
                }
                cursor.moveToNext();
            }
            cursor.close();
            //存储数据到数据库，两张表
            mPresenter.saveMusicInfoFromSD(musicBasicInfos);
            //从musicBasicInfos 和 数据库读取数据到 mMusicDatas
            mMusicDatas = mPresenter.getMusicAllInfo(musicBasicInfos);
            sendBroadCastForString("AllMusicRefresh");
            startPlayMusicService();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.tv_tab_first:
//                mMainViewPager.setCurrentItem(0);
//                break;
//            case R.id.tv_tab_second:
//                mMainViewPager.setCurrentItem(1);
//                break;
//            case R.id.tv_tab_third:
//                mMainViewPager.setCurrentItem(2);
//                break;
//            case R.id.tv_tab_last:
//                mMainViewPager.setCurrentItem(3);
//                break;
            case R.id.iv_music_pic:
                //点击播放暂停按钮
                sendBroadCastForString("PlayOrPause");
                break;
            default:
                break;
        }
    }

    /**
     * 监听按键，实现主页面点击back健不关闭应用
     * @param keyCode keyCode
     * @param event event
     * @return bool
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * fragment获取activity的音乐数据
     */
    public List<MusicData> getMusicDatas(){
        return mMusicDatas;
    }

    /**
     * 为了节省内存，在全部歌曲页面接收到数据后，回收这里的资源
     */
    public void clearMusicData(){
        mMusicDatas.clear();
    }

    /**
     * 获取歌单信息
     */
    @Override
    public List<SongListInfo> getSongListInfo() {
        return mPresenter.getSongListInfoFromDB();
    }

    /**
     * 添加歌单
     * @param info info
     */
    @Override
    public void addSongListToDB(SongListInfo info) {
        mPresenter.addSongListToDB(info);
    }

    @Override
    public List<MusicData> getMyLoveMusicData() {
        List<MusicData> myLoveData = new ArrayList<>();
        for (int i = 0;i < mMusicDatas.size();i++){
            if (mMusicDatas.get(i).isLove()){
                myLoveData.add(mMusicDatas.get(i));
            }
        }
        return myLoveData;
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
        for (int i = 0;i < mMusicDatas.size();i++){
            if (mMusicDatas.get(i).getpId() == pid){
                return mMusicDatas.get(i);
            }
        }
        return null;
    }

    /**
     * 按歌手分类页面需要的数据
     * @return list<list<MusicData>>
     */
    @Override
    public List<List<MusicData>> getClassifyMusicInfo(List<ClassifyMusicPlayer> musicPlayers) {
        List<List<MusicData>> data = new ArrayList<>();

        for (int i = 0;i < musicPlayers.size();i++){
            List<MusicData> musicData = new ArrayList<>();
            for (int j = 0;j < mMusicDatas.size();j++){
                if (musicPlayers.get(i).getMusicPlayerName().equals(mMusicDatas.get(j).getMusicPlayer())){
                    //名字相同，添加进入musicData
                    musicData.add(mMusicDatas.get(j));
                }
            }
            data.add(musicData);
        }
        return data;
    }

    @Override
    public List<ClassifyMusicPlayer> getClassifyMusicPlayerInfo() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.example.ningyuwen.music/databases/music.db",null);
//        String sql = "select DISTINCT MUSIC_PLAYER from MUSIC_BASIC_INFO ;";
        Cursor cursor = db.query(true, "MUSIC_BASIC_INFO", new String[]{"MUSIC_PLAYER"},
                null, null, null, null, null, null);

        List<ClassifyMusicPlayer> data = new ArrayList<>();
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("MUSIC_PLAYER"));
            //通过姓名查询数量
            Cursor cursorName = db.query(false, "MUSIC_BASIC_INFO", new String[]{"_id"},
                    "MUSIC_PLAYER=?", new String[]{name }, null, null, null, null);
            ClassifyMusicPlayer player = new ClassifyMusicPlayer();
            player.setMusicPlayerName(name);
            player.setMusicNumber(cursorName.getCount());  //歌手对应的歌曲数量
            data.add(player);
            cursorName.close();
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * 发送广播
     * @param string string
     */
    @Override
    public void sendBroadCastForString(String string) {
        sendBroadcast(new Intent(string));
    }

    /**
     * 传入音乐人姓名，获取音乐人有多少音乐
     * @param musicPlayer musicPlayer
     * @return int
     */
    private int getNumberOfMusicPlayerFromData(String musicPlayer){
        int number = 0;
        for (int i = 0;i < mMusicDatas.size();i++){
            if (musicPlayer.equals(mMusicDatas.get(i).getMusicPlayer())){
                number++;
            }
        }
        return number;
    }
}
