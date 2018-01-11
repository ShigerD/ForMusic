package com.example.ningyuwen.music.view.activity.impl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.presenter.impl.PlayPresenter;
import com.example.ningyuwen.music.util.FastBlurUtil;
import com.example.ningyuwen.music.view.activity.i.IPlayActivity;
import com.example.ningyuwen.music.view.fragment.impl.DiscViewFragment;
import com.example.ningyuwen.music.view.widget.BackgourndAnimationRelativeLayout;
import com.example.ningyuwen.music.view.widget.DiscView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * PlayActivity，播放音乐音乐
 * Created by ningyuwen on 17-11-13.
 */

public class PlayActivity extends BaseActivity<PlayPresenter> implements IPlayActivity, DiscView.IPlayInfo {


    //    @BindView(R.id.toolBar)
//    Toolbar toolBar;
//    @BindView(R.id.ivDiscBlackgound)
//    ImageView ivDiscBlackgound;
//    @BindView(R.id.vpDiscContain)
//    ViewPager vpDiscContain;
//    @BindView(R.id.ivNeedle)
//    ImageView ivNeedle;
    @BindView(R.id.tvCurrentTime)
    TextView tvCurrentTime;
    @BindView(R.id.musicSeekBar)
    SeekBar musicSeekBar;
    @BindView(R.id.tvTotalTime)
    TextView tvTotalTime;
    @BindView(R.id.rlMusicTime)
    RelativeLayout rlMusicTime;
    @BindView(R.id.ivLast)
    ImageView ivLast;
    @BindView(R.id.ivPlayOrPause)
    ImageView ivPlayOrPause;
    @BindView(R.id.ivNext)
    ImageView ivNext;
    @BindView(R.id.llPlayOption)
    LinearLayout llPlayOption;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_music_name)
    TextView tvMusicName;
    @BindView(R.id.tv_music_player)
    TextView tvMusicPlayer;
    @BindView(R.id.iv_play_type)
    ImageView ivPlayType;
    @BindView(R.id.iv_play_list)
    ImageView ivPlayList;
    @BindView(R.id.rootLayout)
    BackgourndAnimationRelativeLayout rootLayout;

    private MusicData mMusicData;
    private long mPlayingMusicId;   //当前播放的音乐id

//    private DiscView mDisc;     //转盘

    private ViewPager mViewPager;   //转盘

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStatusBarTrans();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initView();
        ButterKnife.bind(this);
        setListener();

        myHandler = new MyHandler(PlayActivity.this);

        initData();
        initUi();
        setPlayActivityBg();

        setDiscData();
//        MusicApplication.getFixedThreadPool().execute(runnable1);
    }

    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            setDiscData();
        }
    };

    private List<Fragment> list;

    /**
     * ViewPager中只放三条，滑动一个添加一个，删除一个
     */
    private void setDiscData() {
        list= new ArrayList<Fragment>();
        for (int i = 0;i < mMusicDatas.size();i++){
            list.add(new DiscViewFragment()
                    .setMusicAlbumPic(mMusicDatas.get(i).getMusicAlbumPicPath()));
        }
        DiscFragmentAdapter fragmentAdapter = new DiscFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setCurrentItem(mServiceDataTrans.getPlayPosition());
    }



    class DiscFragmentAdapter extends FragmentStatePagerAdapter {

        public DiscFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    private void setListener() {
//        mDisc.setPlayInfoListener(this);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.dis_viewpager);
    }

    /**
     * 设置背景
     */
    private void setPlayActivityBg() {
        MusicApplication.getFixedThreadPool().execute(runnable);

    }

    private android.os.Handler myHandler;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mMusicData == null){
                return;
            }
            //拿到初始图
            Bitmap initBitmap = null;
            try {
                initBitmap = Glide.with(PlayActivity.this)
                        .load(mMusicData.getMusicAlbumPicPath())
                        .asBitmap().error(R.drawable.login_bg_night).centerCrop().into(540,960).get();
                //处理得到模糊效果的图
                int scaleRatio = 10;
                int blurRadius = 16;
                initBitmap = Bitmap.createScaledBitmap(initBitmap,
                        initBitmap.getWidth() / scaleRatio,
                        initBitmap.getHeight() / scaleRatio,
                        false);
                initBitmap = FastBlurUtil.doBlur(initBitmap, blurRadius, false);

                Message message = myHandler.obtainMessage();
                message.what = 0;
                message.obj = initBitmap;
                message.sendToTarget();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onMusicInfoChanged(String musicName, String musicAuthor) {

    }

    @Override
    public void onMusicPicChanged(String musicPicRes) {

    }

    @Override
    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus) {

    }

    /**
     * Handler，用于接收消息，转换为主线程
     */
    private class MyHandler extends Handler {

        private WeakReference<PlayActivity> mActivity;

        public MyHandler(PlayActivity activity) {
            mActivity = new WeakReference<PlayActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity.get() == null){
                return;
            }
            switch (msg.what){
                case 0:
                    //显示背景图片
                    Bitmap bitmap = (Bitmap) msg.obj;
                    rootLayout.setBackground(new BitmapDrawable(bitmap));
                    break;
                case 1:
                    //显示播放时间
                    tvCurrentTime.setText(getTextFromTime(mServiceDataTrans.getMusicPlayTimeStamp()));
                    musicSeekBar.setProgress(mServiceDataTrans.getMusicPlayTimeStamp()*100/mMusicData.getMusicTime());
                    myHandler.sendEmptyMessageDelayed(1, 1000);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUi() {
        if (mMusicData == null){
            return;
        }
        tvMusicName.setText(mMusicData.getMusicName());
        tvMusicPlayer.setText(mMusicData.getMusicPlayer());
        if (mServiceDataTrans.isPlayingMusic()){
            //正在播放，显示播放按钮
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
        }
        tvTotalTime.setText(getTextFromTime(mMusicData.getMusicTime()));

        //启动一个线程，一直刷新，每秒种刷新一次
        myHandler.sendEmptyMessageDelayed(1, 1000);
    }

    /**
     * 获取分秒
     * @param time time
     * @return text 05：00
     */
    private String getTextFromTime(int time){
        int minute = time/1000/60;
        int second = time/1000 - minute*60;
        String min = "";
        String sec = "";
        if (minute < 10){
            min = String.valueOf("0" + minute);
        }else {
            min = String.valueOf(minute);
        }
        if (second < 10){
            sec = String.valueOf("0" + second);
        }else {
            sec = String.valueOf(second);
        }
        return min + ":" + sec;
    }

    private static String TAG = "ning";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: 1234");
    }

    /**
     * 初始化数据,用于展示
     */
    private void initData() {
        if (mServiceDataTrans == null){
            return;
        }
        mPlayingMusicId = mServiceDataTrans.getPlayingMusicId();
        mMusicData = getDataFromPid(mPlayingMusicId);
    }


    /**
     * 获取Presenter实例
     *
     * @return presenter
     */
    @Override
    protected PlayPresenter getPresenter() {
        return new PlayPresenter(this);
    }

    @Override
    public void showLyricOnActivity(long pid) {

    }

    /**
     * 播放暂停的状态，从service传回来
     * @param play  //bool
     */
    @Override
    public void refreshPlayPauseView(boolean play) {
        //播放或者暂停
        if (play){
            //暂停
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
        }else {
            //播放
            ivPlayOrPause.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public void showMusicInfoAtActivity(int what) {

    }

    /**
     * 关闭
     */
    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0, R.anim.activity_close);
    }

    @OnClick({R.id.ivLast, R.id.ivPlayOrPause, R.id.ivNext, R.id.iv_back, R.id.iv_play_type, R.id.iv_play_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLast:
                //上一曲，根据播放模式变化
                mServiceDataTrans.playMusicFromClick(mServiceDataTrans.getPlayPosition()-1);
                mViewPager.setCurrentItem(mServiceDataTrans.getPlayPosition());
                initData();
                initUi();
                setPlayActivityBg();
                break;
            case R.id.ivPlayOrPause:
                //播放或者暂停
                if (mServiceDataTrans.isPlayingMusic()){
                    //暂停
                    ivPlayOrPause.setImageResource(R.drawable.ic_pause);
                }else {
                    //播放
                    ivPlayOrPause.setImageResource(R.drawable.ic_play);
                }
                mServiceDataTrans.playOrPause();
                break;
            case R.id.ivNext:
                //下一曲，根据播放模式变化
                mServiceDataTrans.playMusicFromClick(mServiceDataTrans.getPlayPosition()+1);
                //修改背景图片
                mViewPager.setCurrentItem(mServiceDataTrans.getPlayPosition());
                initData();
                initUi();
                setPlayActivityBg();
                break;
            case R.id.iv_back:
//                finish();
                startActivity(new Intent(PlayActivity.this, MainActivity.class));
                break;
            case R.id.iv_play_type:
                break;
            case R.id.iv_play_list:
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PlayActivity.this, MainActivity.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
