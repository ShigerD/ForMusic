package com.example.ningyuwen.music.view.activity.impl;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.classify.ClassifyMusicPlayer;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.presenter.impl.MainPresenter;
import com.example.ningyuwen.music.util.FastBlurUtil;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.i.IMainActivity;
import com.example.ningyuwen.music.view.activity.i.IMainActivityToFragment;
import com.example.ningyuwen.music.view.adapter.MainFragmentAdapter;
import com.example.ningyuwen.music.view.fragment.impl.AllMusicFragment;
import com.example.ningyuwen.music.view.fragment.impl.ClassifyMusicFragment;
import com.example.ningyuwen.music.view.fragment.impl.CustomizeMusicFragment;
import com.example.ningyuwen.music.view.fragment.impl.MyLoveMusicFragment;
import com.example.ningyuwen.music.view.widget.PlayMusicDialogFragment;
import com.example.ningyuwen.music.view.widget.PlayMusicPopupWindow;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 主页面，音乐播放，扫描音乐等
 * Created by ningyuwen on 17-9-22.
 */

public class MainActivity extends BaseActivity<MainPresenter> implements
        View.OnClickListener , IMainActivity {

//    public static List<MusicData> mMusicDatas;
    private DrawerLayout mDrawerMenu;
    private ViewPager mMainViewPager;
    private static ArrayList<Fragment> mFragments;
    private ImageView mIvBg;
    private TabLayout mTabLayout;
    public static final String NOTIFICATION_CHANNEL_ID = "4655";
//    private CircleImageView mCircleUserHead;//用户原型头像
//    private TextView mTextNickName;//用户昵称
//    private TextView mTextSign;//用户签名
    private ListView mListDrawer;//侧滑菜单item
    private List<Map<String, Object>> List = new ArrayList<>(); ;
    private String[] item_name= {"主题换肤","关于开发者","计时关闭","退出"};
    private int[] item_icon = {R.drawable.ic_backgroudstyle,
            R.drawable.ic_aboutus,
            R.drawable.ic_timer,
            R.drawable.ic_exit};
    private int alert_finish = -1;


    private TextView mTvMusicName;  //显示音乐名
    private static TextView mTvMusicLyric; //显示音乐歌词

    private PlayPauseView mPlayPauseView;   //播放暂停按钮
    private BroadcastReceiver mReceiver;
    private PlayMusicPopupWindow mPlayMusicPopupWindow;  //播放页面
    private PlayMusicDialogFragment mPlayMusicDialogFragment;      //改为dialog
    private CardView mMainCardView; //cardview


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarTransparent();

//        mMyHandler = new MyHandler(MainActivity.this);
        //绑定控件和设置监听
        findView();
        setMainActivityBg();
        setListener();
        setStatusBarTransparentForDrawerLayout(mDrawerMenu);
        //初始化fragment
        initPage();
        //默认第一页
        mMainViewPager.setCurrentItem(0);
        //drawerlayout
        initdrawer();

        //线程池，添加一个任务
        MusicApplication.getFixedThreadPool().execute(runnable);

//        sendNotification();
        setBroadCastReceiver();
    }

    /**
     * 设置ListView的adaptter
     */
    private void initdrawer() {
//        List<Map<String, Object>> List = new ArrayList<>(); ;
//        int resouces = R.layout.item_drawer;
//        String[] from= {"主题换肤","关于开发者","计时关闭","退出"};
//        int[] to = {R.drawable.ic_backgroudstyle,
//                R.drawable.ic_aboutus,
//                R.drawable.ic_timer,
//                R.drawable.ic_exit};
        for (int i = 0;i<item_name.length;i++){
            Map<String,Object> item = new HashMap<String, Object>();
            item.put("name",this.item_name[i]);//item text
            item.put("icon",this.item_icon[i]);//item_image
            this.List.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this,this.List,R.layout.item_drawer,
                new String[] {"name","icon"},new int[] {R.id.drawerlist_text,R.id.drawerlist_icon});
        mListDrawer.setAdapter(adapter);

    }

    /**
     * 获取音乐数据，数据库有则获取数据库中的数据，数据库没有则扫描SD卡中的数据，再启动服务 startPlayMusicService
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
            mMusicDatas.addAll(mPresenter.getMusicBasicInfoFromDB());

            //如果返回数据为空，从SD卡读取数据
            if (mMusicDatas.size() == 0){
                //获取读写权限，并获取音乐数据，存储到数据库，和 存储到 mMusicDatas
                getReadPermissionAndGetInfoFromSD();
            }else {
                //fragment更新数据
//                sendBroadCastForString("AllMusicRefresh");
                Message message = handler.obtainMessage();
                message.what = StaticFinalUtil.HANDLER_REFRESH_MUSIC;
                message.sendToTarget();
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
    }

    @Override
    public void showLyricOnActivity(long pid) {
        MusicBasicInfo musicBasicInfo = mPresenter.getMusicDataUsePid(pid);
        mTvMusicName.setText(musicBasicInfo.getMusicName());   // 显示音乐名
        String lyric = mPresenter.getLyricFromDBUsePid(musicBasicInfo);   //获取歌词
        Log.i(TAG, "showLyricAtActivity: " + lyric);
        if ("".equals(lyric)){
            mTvMusicLyric.setText("暂无歌词");
            return;
        }

        //歌词List，时间加歌词
        if (mTimeAndLyric == null){
            mTimeAndLyric = new ArrayList<>();
        }
        mTimeAndLyric.clear();
        mTimeAndLyric = mPresenter.analysisLyric(lyric);   //歌词
        MusicApplication.getSingleThreadPool().execute(MainActivity.LyricRunnable.getInstance());
    }

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
    }

    /**
     * handler
     */
//    private static class HandlerActivity extends Handler{
//        private static HandlerActivity mHandlerActivity;  //Activity的handler实例
//
//        static HandlerActivity getInstance(){
//            if (mHandlerActivity == null){
//                mHandlerActivity = new HandlerActivity();
//            }
//            return mHandlerActivity;
//        }
//
//        @Override
//        public void handleMessage(Message message){
//            super.handleMessage(message);
//            switch (message.what){
//                case StaticFinalUtil.HANDLER_ACTIVITY_LYRIC:
//                    //显示歌词
//                    mTvMusicLyric.setText(mTimeAndLyric.get(message.arg1).second);
//                    break;
//            }
//        }
//    }

//    static class MyHandler extends Handler {
//        WeakReference<MainActivity> mActivityReference;
//
//        MyHandler(MainActivity activity) {
//            mActivityReference= new WeakReference<MainActivity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message message) {
//            Log.i("ningtest", "handleMessage: 信息");
//
//            final MainActivity activity = mActivityReference.get();
//            if (activity == null){
//                return;
//            }
//            switch (message.what){
//                case StaticFinalUtil.HANDLER_ACTIVITY_LYRIC:
//                    //显示歌词
//                    mTvMusicLyric.setText(mTimeAndLyric.get(message.arg1).second);
//                    break;
//            }
//        }
//    }

    /**
     * 用于控制歌词的显示
     */
    public static class LyricRunnable implements Runnable{
        private static LyricRunnable mLyricRunnable;

        static LyricRunnable getInstance(){
            if (mLyricRunnable == null){
                mLyricRunnable = new LyricRunnable();
            }
            return mLyricRunnable;
        }

        @Override
        public void run() {
            Log.i("ningtest", "run: ningtest");
            Message msg = handler.obtainMessage();
            msg.arg1 = 0;
            msg.what = StaticFinalUtil.HANDLER_ACTIVITY_LYRIC;
            msg.sendToTarget();
            //一直运行
            while (mTimeAndLyric.size() > 0) {
                try {
                    int nowTime = mServiceDataTrans.getMusicPlayTimeStamp();  //当前播放时间
                    //遍历找到对应的歌词顺序
                    for (int i = 0; i < mTimeAndLyric.size(); i++) {
                        if (nowTime > mTimeAndLyric.get(i).first && nowTime < mTimeAndLyric.get(i + 1).first) {
                            //发送msg更新歌词
//                            handler.obtainMessage(StaticFinalUtil.HANDLER_ACTIVITY_LYRIC, i, 0);
                            msg = handler.obtainMessage();
                            msg.arg1 = i;
                            msg.what = StaticFinalUtil.HANDLER_ACTIVITY_LYRIC;
                            msg.sendToTarget();
                            mTimeAndLyric.remove(i);
                            break;
                        }
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("ningtest", "run: 空指针异常");
                    break;
                }
            }
        }
    }

    /**
     * handler
     */
    @SuppressLint("HandlerLeak")
    static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case StaticFinalUtil.HANDLER_ACTIVITY_LYRIC:
                    //显示歌词
                    try {
                        mTvMusicLyric.setText(mTimeAndLyric.get(message.arg1).second);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case StaticFinalUtil.HANDLER_REFRESH_MUSIC:
                    ((IMainActivityToFragment)mFragments.get(0)).refreshAllMusic();
//                    ((IMainActivityToFragment)mFragments.get(1)).refreshAllMusic();
//                    ((IMainActivityToFragment)mFragments.get(2)).refreshAllMusic();
//                    ((IMainActivityToFragment)mFragments.get(3)).refreshAllMusic();
                    break;
                default:
                    break;

            }
        }
    };

    /**
     * 用户点击了音乐，需要在后台播放
     * @param position 点击位置position
     */
    public void playMusicOnBackstage(int position){
        //不使用广播，使用接口回调
        mServiceDataTrans.playMusicFromClick(position);
        refreshPlayPauseView(true);
    }

    private void findView() {
        mDrawerMenu = (DrawerLayout) findViewById(R.id.dr_main);              //侧滑菜单布局
//        mCircleUserHead = findViewById(R.id.ciecle_userHead);
//        mTextNickName = findViewById(R.id.tv_nickName);
//        mTextSign = findViewById(R.id.tv_userSign);
        mListDrawer = findViewById(R.id.ls_drawer);

        mTvMusicName = (TextView) findViewById(R.id.tv_music_name);
        mTvMusicLyric = (TextView) findViewById(R.id.tv_music_lyric);
        mMainViewPager = (ViewPager) findViewById(R.id.vp_main_page);         //主页面的viewpager
        mIvBg = (ImageView) findViewById(R.id.iv_main_activity_bg);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mPlayPauseView = (PlayPauseView) findViewById(R.id.iv_music_pic);
        mMainCardView = (CardView)findViewById(R.id.cv_show_state_lyric);


        findViewById(R.id.iv_bar_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicApplication.getFixedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //重新导入音乐数据，查看权限并扫描SD卡
                        getReadPermissionAndGetInfoFromSD();
                        //发广播，更新四个fragment里面的数据
//                        sendBroadcast(new Intent("RefreshMusicData"));

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
        mMainCardView.setOnClickListener(this);
//        findViewById(R.id.iv_music_pic).setOnClickListener(this);
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

        //播放暂停按钮
        mPlayPauseView.setPlayPauseListener(new PlayPauseView.PlayPauseListener(){

            @Override
            public void play() {
                refreshPlayPauseView(true);
                if (mServiceDataTrans == null){
                    return;
                }
                mServiceDataTrans.playOrPause();
                showToast(mTabLayout, "播放");
            }

            @Override
            public void pause() {
                refreshPlayPauseView(false);
                if (mServiceDataTrans == null){
                    return;
                }
                mServiceDataTrans.playOrPause();
                showToast(mTabLayout, "暂停");
            }
        });

        mListDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //主题换肤
                        break;
                    case 1:
                        //developer
                        break;
                    case 2:
                        //timer
                        setTimerDialog();
                        break;
                    case 3:
                        //exit
                        break;
                }
            }
        });
    }

    /**
     * 定时关闭的弹窗
     */
    private void setTimerDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final String[] choices = {"5分钟","10分钟","15分钟","30分钟","60分钟","自定义"};
        final int[] num = {300,600,900,1800,3600};

        Intent intent = new Intent().setAction(StaticFinalUtil.RECEIVER_CLOSE_APP);
        final PendingIntent pi  = PendingIntent.getBroadcast(MainActivity.this,
                0, intent, 0);
        final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);


        alert.setTitle("定时停止播放");
        alert.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==5){
                    //跳转自定义时间
                }else if(which!=-1){
                    alert_finish = which;
                }
            }
        });

        alert.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(alert_finish!=-1){
                    am.setRepeating(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis(),
                                num[alert_finish] * 1000,
                            pi);
                }
                dialog.dismiss();
            }
        });

        AlertDialog Alert = alert.create();
        Alert.show();
    }

    /**
     * 播放或暂停，那个动画
     * @param play bool
     */
    @Override
    public void refreshPlayPauseView(boolean play){
        if (play){
            mPlayPauseView.play();
        }else {
            mPlayPauseView.pause();
        }
    }

    @Override
    public void showMusicInfoAtActivity(int what) {
        Message message = handler.obtainMessage();
        message.what = what;
        message.sendToTarget();
    }

    /**
     * 初始化ViewPager的页面.
     * ViewPager内部是Fragment
     */
    private void initPage() {
        mFragments = new ArrayList<>();  //Fragment的List
        mFragments.add(new AllMusicFragment());                 //添加需要展示的Fragment
        mFragments.add(new CustomizeMusicFragment());           //歌单
        mFragments.add(new MyLoveMusicFragment());              //我喜爱的
        mFragments.add(new ClassifyMusicFragment());            //分类
        MainFragmentAdapter mainFragmentAdapter =           //ViewPager的适配器
                new MainFragmentAdapter(getSupportFragmentManager(), mFragments);
        mMainViewPager.setAdapter(mainFragmentAdapter);        //设置ViewPager的适配器
        mTabLayout.setupWithViewPager(mMainViewPager);
    }

    @Override
    protected MainPresenter getPresenter() {
        return new MainPresenter(this);
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

    /**
     * toast
     * @param message message
     */
    public void showToast(String message){
        showToast(mIvBg, message);
    }

    private String TAG = "test";

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
//            case R.id.iv_music_pic:
//                //点击播放暂停按钮
//                mServiceDataTrans.playOrPause();
//                break;
            case R.id.cv_show_state_lyric:
                //cardview，显示歌词的位置，点击进入PlayActivity
//                Intent intent = new Intent(this, PlayActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.activity_open,0);

                //改为启动popupwindow
                if (mPlayMusicPopupWindow == null){
                    initPopupWindow();
                }
                mPlayMusicPopupWindow.showAsDropDown(mIvBg);

//                if (mPlayMusicDialogFragment == null){
//                    initPopupWindow();
//                }
//                mPlayMusicDialogFragment.show(getSupportFragmentManager(), "");
                break;
            default:
                break;
        }
    }

    /**
     * 初始化popupwindow
     */
    private void initPopupWindow() {
        mPlayMusicPopupWindow = new PlayMusicPopupWindow(this);
//        mPlayMusicDialogFragment = new PlayMusicDialogFragment(this);
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
        if (mMusicDatas == null){
            mMusicDatas = new ArrayList<>();
            getMusicInfoFromSD();
        }
        for (int i = 0;i < mMusicDatas.size();i++){
            if (mMusicDatas.get(i).isLove()){
                myLoveData.add(mMusicDatas.get(i));
            }
        }
        return myLoveData;
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
     * 替换歌单列表，某些播放状态下会使用到,position为点击的位置
     * @param musicInfoList musicInfoList  存储的是音乐文件的ID，方便查询，另外节省空间
     */
    @Override
    public void replaceMusicList(ArrayList<Long> musicInfoList, int position) {
        mServiceDataTrans.replaceBackStageMusicList(musicInfoList, position);
    }

    /**
     * 判断歌单名是否存在或是否为空，不满足前两项才能添加歌单
     * @param songlistName 歌单名
     * @return bool
     */
    @Override
    public boolean existSongListName(String songlistName) {
        return mPresenter.existSongListName(songlistName);
    }

    /**
     * 将音乐添加至歌单，主要是修改记录信息，而不修改基本信息
     * @param musicId 音乐id
     * @param songListId  歌单id
     */
    @Override
    public void addMusicToSongList(long musicId, long songListId) {
        mPresenter.addMusicToSongList(musicId, songListId);
    }

    /**
     * 提示歌单页面刷新数据
     * @param songListInfo songListInfo
     */
    public void refreshCustomMusic(SongListInfo songListInfo){
        ((IMainActivityToFragment)mFragments.get(1)).refreshCustomMusic(songListInfo);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        unregisterReceiver(mReceiver);
    }

    /**
     * 设置广播接收器
     */
    private void setBroadCastReceiver() {
        mReceiver = new GetReceiverFromOther();//----注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(StaticFinalUtil.RECEIVER_CLOSE_APP);
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 广播接收器，主要用于接受从其他位置发送过来的广播，用于关闭APP
     */
    public class GetReceiverFromOther extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (StaticFinalUtil.RECEIVER_CLOSE_APP.equals(intent.getAction())){
                //重新启动MainActivity,MainActivity设为SingleTask,然后关闭App
                startActivity(new Intent(getApplicationContext(), MainActivity.class).setAction(StaticFinalUtil.RECEIVER_CLOSE_APP));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (StaticFinalUtil.RECEIVER_CLOSE_APP.equals(intent.getAction())){
            //关闭App
            finish();
            System.exit(0);
        }
    }

    /**
     * 取消我喜爱的音乐，传入MusicData
     */
    public void updateLoveMusic(MusicData musicData){
        musicData.setLove(false);
        mPresenter.updateLoveMusicToDisLike(musicData);     //数据库
        for (int i = 0;i < mMusicDatas.size();i++){
            if (Objects.equals(mMusicDatas.get(i).getpId(), musicData.getpId())){
                mMusicDatas.set(i, musicData);
                break;
            }
        }
        ((IMainActivityToFragment)mFragments.get(0)).refreshAllMusicDislike(musicData);
    }
}
