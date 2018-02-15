package com.example.ningyuwen.music.view.widget;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.util.DisplayUtil;
import com.example.ningyuwen.music.util.FastBlurUtil;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 将播放页面的activity改为popupwindow
 * Created by ningyuwen on 18-1-10.
 */

public class PlayMusicPopupWindow extends PopupWindow implements View.OnClickListener,
        MusicSongListDialogFragment.IMusicSongPopToPlayPop {

    private TextView tvMusicName;
    private TextView tvMusicPlayer;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private SeekBar musicSeekBar;
    private ImageView ivPlayType;
    private ImageView ivLast;
    private ImageView ivPlayOrPause;
    private ImageView ivNext;
    private ImageView ivPlayList;
    private RelativeLayout rootLayout;
    private ImageView mBackDiscImg;     //disc背景
    private Context mContext;   //上下文

    private MusicData mMusicData;

//    private DiscView mDisc;     //转盘

    private ViewPager mViewPager;   //转盘
    private View mRootView;     //根布局
    private int mScreenWidth, mScreenHeight;
    private boolean shouldPauseChangeProgress = false;
    private boolean isPlaying = false;  //正在播放
    private boolean isSliding = false;    //在滑动
    private MusicSongListDialogFragment mMusicSongListDialogFragment;  //歌单popup
    private PlayMusicDiscAdapter mAdapter;   //viewpager的adapter

    @SuppressLint("InflateParams")
    public PlayMusicPopupWindow(Context context) {
        this(context, LayoutInflater.from(context)
                        .inflate(R.layout.activity_play, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
    }

    private PlayMusicPopupWindow(Context context, View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        mRootView = contentView;
        setContentView(context, contentView);
        initPopupWindow();
    }

    private void setBroadCastReceiver(){
        BroadcastReceiver mReceiver = new ServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST);
        mContext.registerReceiver(mReceiver, intentFilter);
    }

    class ServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST.equals(action)){
                //更换了歌单
                if (mAdapter != null){
                    mViewPager.removeAllViews();
                    setDiscData();

//                    List<String> picPathList = new ArrayList<>();
//                    for (int i = 0;i < BaseActivity.mMusicDatas.size();i++){
//                        picPathList.add(BaseActivity.mMusicDatas.get(i).getMusicAlbumPicPath());
//                    }
//                    mAdapter.setPicPathList(picPathList);
                    mViewPager.setCurrentItem(intent.getIntExtra("position",0));
                }
            }
        }
    }

    public void setContentView(Context context, View contentView) {
        super.setContentView(contentView);
        mContext = context;
        mScreenWidth = DisplayUtil.getScreenWidth(mContext);
        mScreenHeight = DisplayUtil.getScreenHeight(mContext);
    }

    private void initPopupWindow() {
        setBroadCastReceiver();
        findViews();
        initView();
        setListener();
        setPlayActivityBg();
        setDiscData();
        myHandler = new MyHandler(PlayMusicPopupWindow.this);
        ((MainActivity)mContext).setIBaseActivityToPopupListener(mIBaseActivityToPopup);
        MusicApplication.getDiscSingleThreadPool().execute(new DiscPlayRunnable());
    }

    @Override
    public void showAsDropDown(View anchor) {
        //刷新一些数据
        initData();
        initUi(false);

        //如果变了音乐,修改背景
        if (BaseActivity.mShouldChangePlayingBg){
            setPlayActivityBg();
            BaseActivity.mShouldChangePlayingBg = false;
        }
        if (BaseActivity.mServiceDataTrans != null) {
            mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        //刷新一些数据
        initData();
        initUi(false);

        //如果变了音乐,修改背景
        if (BaseActivity.mShouldChangePlayingBg){
            setPlayActivityBg();
            BaseActivity.mShouldChangePlayingBg = false;
        }
        if (BaseActivity.mServiceDataTrans != null) {
            mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
        }
        super.showAtLocation(parent, gravity, x, y);
    }

    private BaseActivity.IBaseActivityToPopup mIBaseActivityToPopup = new BaseActivity.IBaseActivityToPopup() {
        @Override
        public void refreshPopupBgAndDisc(int position) {
            if (isShowing()){
                //在显示，则刷新，未显示，则不刷新
                initData();
                initUi(true);
                setPlayActivityBg();
                mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
                BaseActivity.mShouldChangePlayingBg = false;
            }
        }
    };

    /**
     * 绑定控件
     */
    private void findViews() {
        tvMusicName = mRootView.findViewById(R.id.tv_music_name);
        tvMusicPlayer = mRootView.findViewById(R.id.tv_music_player);
        mViewPager = mRootView.findViewById(R.id.dis_viewpager);
        tvCurrentTime = mRootView.findViewById(R.id.tvCurrentTime);
        tvTotalTime = mRootView.findViewById(R.id.tvTotalTime);
        musicSeekBar = mRootView.findViewById(R.id.musicSeekBar);
        ivPlayType = mRootView.findViewById(R.id.iv_play_type);
        ivLast = mRootView.findViewById(R.id.ivLast);
        ivPlayOrPause = mRootView.findViewById(R.id.ivPlayOrPause);
        ivNext = mRootView.findViewById(R.id.ivNext);
        ivPlayList = mRootView.findViewById(R.id.iv_play_list);
        rootLayout = mRootView.findViewById(R.id.rootLayout);
        mBackDiscImg = mRootView.findViewById(R.id.iv_background);
    }

    private void setListener(){
        ivLast.setOnClickListener(this);
        ivPlayType.setOnClickListener(this);
        ivPlayOrPause.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPlayList.setOnClickListener(this);
        musicSeekBar.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });

        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //改变之后，修改播放时间,为了方便，此处只在改变之后修改
                Log.i(TAG, "onProgressChanged: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始触摸，将handler中的progress变化先暂停
                shouldPauseChangeProgress = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //结束触摸，开始播放,在此处设置Service的播放
                try {
                    shouldPauseChangeProgress = false;
                    Log.i(TAG, "onProgressChanged22: " + seekBar.getProgress());
                    BaseActivity.mServiceDataTrans.changePlayingTime(mMusicData.getMusicTime() / 100
                            * seekBar.getProgress());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLast:
                //上一曲，根据播放模式变化
                try {
                    BaseActivity.mServiceDataTrans.playMusicFromClick(
                            calculatePlayPosition(BaseActivity.mServiceDataTrans.getPlayPosition(), false));
                    mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
                    initData();
                    initUi(true);
                    setPlayActivityBg();
                }catch (Exception e){
                    e.printStackTrace();
                    //test
                }
                break;
            case R.id.ivPlayOrPause:
                //播放或者暂停
                try {
                    if (BaseActivity.mServiceDataTrans.isPlayingMusic()) {
                        //暂停
                        ivPlayOrPause.setImageResource(R.drawable.ic_pause);
                        isPlaying = false;
                    } else {
                        //播放
                        ivPlayOrPause.setImageResource(R.drawable.ic_play);
                        isPlaying = true;
                    }
                    BaseActivity.mServiceDataTrans.playOrPause();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.ivNext:
                //下一曲，根据播放模式变化
                //播放下一曲时，判断当前播放状态，给出播放位置
                try {
                    BaseActivity.mServiceDataTrans.playMusicFromClick(
                            calculatePlayPosition(BaseActivity.mServiceDataTrans.getPlayPosition(), true));
                    //修改背景图片
                    mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
                    initData();
                    initUi(true);
                    setPlayActivityBg();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.iv_play_type:
                //切换播放模式,三种模式，单曲循环，列表循环，随机播放
                //默认为列表循环
//                int type = mContext.getSharedPreferences("notes", Context.MODE_PRIVATE)
//                        .getInt("playType", StaticFinalUtil.SERVICE_PLAY_TYPE_LIST);
                if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_LIST){
                    //如果是列表循环，变为单曲循环
                    StaticFinalUtil.SERVICE_PLAY_TYPE_NOW = StaticFinalUtil.SERVICE_PLAY_TYPE_SINGLE;
                    mContext.getSharedPreferences("notes", Context.MODE_PRIVATE).edit()
                            .putInt("playType", StaticFinalUtil.SERVICE_PLAY_TYPE_SINGLE).apply();
                    ivPlayType.setImageResource(R.drawable.play_icn_one_prs);
                }else if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_SINGLE){
                    //如果是单曲循环，变为随机播放
                    StaticFinalUtil.SERVICE_PLAY_TYPE_NOW = StaticFinalUtil.SERVICE_PLAY_TYPE_RANDOM;
                    mContext.getSharedPreferences("notes", Context.MODE_PRIVATE).edit()
                            .putInt("playType", StaticFinalUtil.SERVICE_PLAY_TYPE_RANDOM).apply();
                    ivPlayType.setImageResource(R.drawable.play_icn_shuffle);
                }else if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_RANDOM){
                    //如果是随机播放，变为列表循环
                    StaticFinalUtil.SERVICE_PLAY_TYPE_NOW = StaticFinalUtil.SERVICE_PLAY_TYPE_LIST;
                    mContext.getSharedPreferences("notes", Context.MODE_PRIVATE).edit()
                            .putInt("playType", StaticFinalUtil.SERVICE_PLAY_TYPE_LIST).apply();
                    ivPlayType.setImageResource(R.drawable.play_icn_loop_prs);
                }
                break;
            case R.id.iv_play_list:
                //显示当前歌单
                //再弹出一个popupwindow
                if (mMusicSongListDialogFragment == null){
                    initMusicSongListPop();
                }
                mMusicSongListDialogFragment.show(((MainActivity)mContext).getSupportFragmentManager(), null);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化歌单popup
     */
    private void initMusicSongListPop() {
        mMusicSongListDialogFragment = new MusicSongListDialogFragment();
        mMusicSongListDialogFragment.setPopupWindowListener(this);
    }

    /**
     * 点击上一曲或者下一曲时生成播放的Position
     * @param position 当前播放的位置
     * @param isNext 是否是点击了下一曲
     * @return 给出需要播放的position
     */
    private int calculatePlayPosition(int position, boolean isNext) {
        if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_LIST ||
                StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_SINGLE){
            //列表循环，加一或减一 还有单曲循环
            if (isNext){
                return position + 1;
            }else {
                return position - 1;
            }
        } else if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_RANDOM){
            return new Random().nextInt(BaseActivity.mMusicDatas.size())
                    % (BaseActivity.mMusicDatas.size() + 1);
        }
        return 0;
    }

    /**
     * 点击事件，传给popupwindow,切换音乐
     * @param position position
     */
    @Override
    public void setPlayMusicPosition(int position) {
        //下一曲，根据播放模式变化
        //播放下一曲时，判断当前播放状态，给出播放位置
        BaseActivity.mServiceDataTrans.playMusicFromClick(position);
        //修改背景图片
        mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
        initData();
        initUi(true);
        setPlayActivityBg();
    }

    /**
     * Handler，用于接收消息，转换为主线程
     */
    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {

        private WeakReference<PlayMusicPopupWindow> popupWindowWeakReference;
        private ImageView imageView;

        MyHandler(PlayMusicPopupWindow popupWindow) {
            popupWindowWeakReference = new WeakReference<>(popupWindow);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (popupWindowWeakReference.get() == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    //显示背景图片
                    Bitmap bitmap = (Bitmap) msg.obj;
                    rootLayout.setBackground(new BitmapDrawable(bitmap));
                    break;
                case 1:
                    //显示播放时间
                    tvCurrentTime.setText(getTextFromTime(BaseActivity.mServiceDataTrans.getMusicPlayTimeStamp()));
                    if (!shouldPauseChangeProgress){
                        musicSeekBar.setProgress(BaseActivity.mServiceDataTrans.getMusicPlayTimeStamp() * 100 / mMusicData.getMusicTime());
                    }
                    myHandler.sendEmptyMessageDelayed(1, 1000);
                    break;
                case StaticFinalUtil.HANDLER_SHOW_DISC_ROTATION:
                    imageView = (ImageView) msg.obj;
                    if (imageView != null) {
                        imageView.setRotation(imageView.getRotation() + 0.3f);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    class DiscPlayRunnable implements Runnable{

        @Override
        public void run() {
            //记录当前位置，相同则一直执行，不同则关闭此线程
            //只有在切换音乐时关闭此线程，其它操作只需暂停旋转
            Message message;
            try {
                while (true) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isShowing() && isPlaying && !isSliding) {
                        View view = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
                        if (view != null) {
                            ImageView imageView = view.findViewById(mViewPager.getCurrentItem());
                            message = myHandler.obtainMessage();
                            message.what = StaticFinalUtil.HANDLER_SHOW_DISC_ROTATION;
                            message.obj = imageView;
                            message.sendToTarget();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * ViewPager中只放三条，滑动一个添加一个，删除一个
     */
    private void setDiscData() {
//        for (int i = 0; i < BaseActivity.mMusicDatas.size(); i++) {
//            list.add(new RelativeLayout(mContext));
//        }
        List<String> picPathList = new ArrayList<>();
        for (int i = 0;i < BaseActivity.mMusicDatas.size();i++){
            picPathList.add(BaseActivity.mMusicDatas.get(i).getMusicAlbumPicPath());
        }
        mAdapter = new PlayMusicDiscAdapter(picPathList);
        mViewPager.setAdapter(mAdapter);
        final boolean[] isSlide = {false};
        final int[] nowPosition = new int[1];    //记录当前位置
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                isSlide
                Log.i(TAG, "onPageScrolled: 0 " + position);
            }

            @Override
            public void onPageSelected(final int position) {
                Log.i(TAG, "onPageSelected: " + position);
                mViewPager.setTag(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        //1 dragging（拖动），理解为：只要触发拖动/滑动事件时，则 state = ViewPager.SCROLL_STATE_DRAGGING
                        isSlide[0] = true;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        //2 settling(安放、定居、解决)，理解为：通过拖动/滑动，安放到了目标页，则 state = ViewPager.SCROLL_STATE_SETTLING
//                        isSlide[0] = true;
                        if (isSlide[0] && nowPosition[0] != mViewPager.getCurrentItem()){
                            isSlide[0] = false;
                            //下一曲，根据播放模式变化
                            BaseActivity.mServiceDataTrans.playMusicFromClick(mViewPager.getCurrentItem());
                            //修改背景图片
                            initData();
                            initUi(true);
                            setPlayActivityBg();
                        }
                        break;
                    default:
                        break;
                }
                isSliding = isSlide[0]; //赋值给isSliding，用于控制旋转动画
            }

        });
    }

    class PlayMusicDiscAdapter extends PagerAdapter {

        private List<String> mAlbumPicPath;

        PlayMusicDiscAdapter(List<String> albumPicPath) {
//            super();
            mAlbumPicPath = albumPicPath;
        }

        @Override
        public int getCount() {
            return BaseActivity.mMusicDatas.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        ImageView imageViewFront;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            @SuppressLint("InflateParams") View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_disc, null);
            imageViewFront = rootView.findViewById(R.id.ivDisc);
            imageViewFront.setImageDrawable(getDiscDrawable(mAlbumPicPath.get(position)));
            imageViewFront.setId(position);
            container.addView(rootView);
            return rootView;
        }

    }

    /**
     * 得到唱盘图片
     * 唱盘图片由空心圆盘及音乐专辑图片“合成”得到
     */
    private Drawable getDiscDrawable(final String musicPicRes) {
        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        int musicPicSize = (int) (mScreenWidth * DisplayUtil.SCALE_MUSIC_PIC_SIZE);

        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R
                .drawable.ic_disc), discSize, discSize, false);

        Bitmap bitmapMusicPic = getMusicPicBitmap(musicPicSize, musicPicRes, false);
        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
                (mContext.getResources(), bitmapMusicPic);

        //抗锯齿
        discDrawable.setAntiAlias(true);
        roundMusicDrawable.setAntiAlias(true);

        Drawable[] drawables = new Drawable[2];
        drawables[0] = roundMusicDrawable;
        drawables[1] = discDrawable;

        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int musicPicMargin = (int) ((DisplayUtil.SCALE_DISC_SIZE - DisplayUtil
                .SCALE_MUSIC_PIC_SIZE) * mScreenWidth / 2);
        //调整专辑图片的四周边距，让其显示在正中
        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin,
                musicPicMargin);

        return layerDrawable;
    }

    private Bitmap getMusicPicBitmap(int musicPicSize, String musicPicRes, boolean isPlayBg) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if ("".equals(musicPicRes) || musicPicRes == null){
            if (isPlayBg){
                return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.login_bg_night, options),
                        musicPicSize, musicPicSize, true);
            }
            return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.bg_default_disc, options),
                    musicPicSize, musicPicSize, true);
        }
        options.inJustDecodeBounds = true;

//        BitmapFactory.decodeFile(musicPicRes, options);

//            BitmapFactory.decodeResource(mContext.getResources(),R.drawable.play_plybar_bg,options);
        int imageWidth = options.outWidth;

        int sample = imageWidth / musicPicSize;
        int dstSample = 1;
        if (sample > dstSample) {
            dstSample = sample;
        }
        options.inJustDecodeBounds = false;
        //设置图片采样率
        options.inSampleSize = dstSample;
        //设置图片解码格式
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(musicPicRes, options),
                musicPicSize, musicPicSize, true);
    }

    /*得到唱盘背后半透明的圆形背景*/
    private Drawable getDiscBlackgroundDrawable() {
        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R
                .drawable.ic_disc_blackground), discSize, discSize, false);
        return RoundedBitmapDrawableFactory.create
                (mContext.getResources(), bitmapDisc);
    }

    private void initView() {
        mBackDiscImg.setImageDrawable(getDiscBlackgroundDrawable());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBackDiscImg
                .getLayoutParams();
        mBackDiscImg.setLayoutParams(layoutParams);

        mRootView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //初始化播放状态
        if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_LIST){
            ivPlayType.setImageResource(R.drawable.play_icn_loop_prs);
        }else if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_SINGLE){
            ivPlayType.setImageResource(R.drawable.play_icn_one_prs);
        }else if (StaticFinalUtil.SERVICE_PLAY_TYPE_NOW == StaticFinalUtil.SERVICE_PLAY_TYPE_RANDOM){
            ivPlayType.setImageResource(R.drawable.play_icn_shuffle);
        }

    }

    /**
     * 设置背景
     */
    private void setPlayActivityBg() {
        MusicApplication.getFixedThreadPool().execute(runnable);
    }

    private Handler myHandler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mMusicData == null) {
                return;
            }
            //拿到初始图
            Bitmap initBitmap;
            //                initBitmap = Glide.with(mContext)
//                        .load(mMusicData.getMusicAlbumPicPath())
//                        .asBitmap().error(R.drawable.login_bg_night).centerCrop().into(540, 960).get();
            initBitmap = getMusicPicBitmap(200, mMusicData.getMusicAlbumPicPath(), true);
            if (initBitmap == null){
                return;
            }
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

        }
    };

    /**
     * 初始化数据,用于展示
     */
    private void initData() {
        if (BaseActivity.mServiceDataTrans == null) {
            return;
        }
        long mPlayingMusicId = BaseActivity.mServiceDataTrans.getPlayingMusicId();
        mMusicData = ((MainActivity) mContext).getDataFromPid(mPlayingMusicId);
    }

    /**
     * 初始化UI
     * @param switchMusic 是否是切换音乐
     */
    private void initUi(boolean switchMusic) {
        if (mMusicData == null) {
            return;
        }
        tvMusicName.setText(mMusicData.getMusicName());
        tvMusicPlayer.setText(mMusicData.getMusicPlayer());
        tvTotalTime.setText(getTextFromTime(mMusicData.getMusicTime()));

        //启动一个线程，一直刷新，每秒种刷新一次
        myHandler.sendEmptyMessageDelayed(1, 1000);

        if (switchMusic){
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
            isPlaying = true;
            return;
        }
        if (BaseActivity.mServiceDataTrans.isPlayingMusic()) {
            //正在播放，显示播放按钮
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
            isPlaying = true;
        }else {
            ivPlayOrPause.setImageResource(R.drawable.ic_pause);
            isPlaying = false;
        }
    }

    /**
     * 获取分秒
     *
     * @param time time
     * @return text 05：00
     */
    private String getTextFromTime(int time) {
        if (time < 0){
            return "";
        }
        int minute = time / 1000 / 60;
        int second = time / 1000 - minute * 60;
        String min;
        String sec;
        if (minute < 10) {
            min = String.valueOf("0" + minute);
        } else {
            min = String.valueOf(minute);
        }
        if (second < 10) {
            sec = String.valueOf("0" + second);
        } else {
            sec = String.valueOf(second);
        }
        return min + ":" + sec;
    }

    private static String TAG = "ning";

}
