package com.example.ningyuwen.music.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.util.DisplayUtil;
import com.example.ningyuwen.music.util.FastBlurUtil;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *popupwindow不能使用viewpager加fragment
 * Created by ningyuwen on 18-1-10.
 */

public class PlayMusicDialogFragment extends DialogFragment {

    TextView tvMusicName;
    TextView tvMusicPlayer;
    TextView tvCurrentTime;
    TextView tvTotalTime;
    SeekBar musicSeekBar;
    ImageView ivPlayType;
    ImageView ivLast;
    ImageView ivPlayOrPause;
    ImageView ivNext;
    ImageView ivPlayList;
    BackgourndAnimationRelativeLayout rootLayout;
    private ImageView mBackDiscImg;     //disc背景
    private Context mContext;   //上下文

    private MusicData mMusicData;
    private long mPlayingMusicId;   //当前播放的音乐id

//    private DiscView mDisc;     //转盘

    private ViewPager mViewPager;   //转盘
    private View mRootView;     //根布局
    private int mScreenWidth, mScreenHeight;

    public PlayMusicDialogFragment(Context context) {
        super();
        mContext = context;
        mScreenWidth = DisplayUtil.getScreenWidth(mContext);
        mScreenHeight = DisplayUtil.getScreenHeight(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //去出标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //放置位置
        Window window = getDialog().getWindow();
        assert window != null;
        window.setGravity(Gravity.TOP | Gravity.RIGHT);

        //设置布局
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.activity_play,
                    ((ViewGroup) window.findViewById(android.R.id.content)),
                    false);//需要用android.R.id.content这个view
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
            window.setLayout(DisplayUtil.getScreenWidth(getActivity()),
                    DisplayUtil.getScreenHeight(getActivity()) - 20);//这2行,和上面的一样,注意顺序就行;
            initPopupWindow();
        }
        return mRootView;
    }


    private void initPopupWindow() {
        findViews();
        initView();
        setListener();

        myHandler = new MyHandler(PlayMusicDialogFragment.this);

        initData();
        initUi();
        setPlayActivityBg();

        setDiscData();
    }

    /**
     * 绑定控件
     */
    private void findViews() {
        tvMusicName = (TextView) mRootView.findViewById(R.id.tv_music_name);
        tvMusicPlayer = (TextView) mRootView.findViewById(R.id.tv_music_player);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.dis_viewpager);
        tvCurrentTime = (TextView)mRootView.findViewById(R.id.tvCurrentTime);
        tvTotalTime = (TextView)mRootView.findViewById(R.id.tvTotalTime);
        musicSeekBar = (SeekBar)mRootView.findViewById(R.id.musicSeekBar);
        ivPlayType = (ImageView)mRootView.findViewById(R.id.iv_play_type);
        ivLast = (ImageView)mRootView.findViewById(R.id.ivLast);
        ivPlayOrPause = (ImageView)mRootView.findViewById(R.id.ivPlayOrPause);
        ivNext = (ImageView)mRootView.findViewById(R.id.ivNext);
        ivPlayList = (ImageView)mRootView.findViewById(R.id.iv_play_list);
        rootLayout = (BackgourndAnimationRelativeLayout)mRootView.findViewById(R.id.rootLayout);
        mBackDiscImg = (ImageView) mRootView.findViewById(R.id.iv_background);
    }

    /**
     * Handler，用于接收消息，转换为主线程
     */
    private class MyHandler extends Handler {

        private WeakReference<PlayMusicDialogFragment> popupWindowWeakReference;

        public MyHandler(PlayMusicDialogFragment popupWindow) {
            popupWindowWeakReference = new WeakReference<PlayMusicDialogFragment>(popupWindow);
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
                    musicSeekBar.setProgress(BaseActivity.mServiceDataTrans.getMusicPlayTimeStamp() * 100 / mMusicData.getMusicTime());
                    myHandler.sendEmptyMessageDelayed(1, 1000);
                    break;
                default:
                    break;
            }
        }
    }

    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            setDiscData();
        }
    };

    private List<RelativeLayout> list;

    /**
     * ViewPager中只放三条，滑动一个添加一个，删除一个
     */
    private void setDiscData() {
        list = new ArrayList<RelativeLayout>();
        for (int i = 0; i < BaseActivity.mMusicDatas.size(); i++) {
            list.add(new RelativeLayout(mContext));
        }
//        PlayMusicDialogFragment.DiscFragmentAdapter fragmentAdapter = new PlayMusicDialogFragment
//                .DiscFragmentAdapter(getChildFragmentManager());
        PlayMusicDiscAdapter discAdapter = new PlayMusicDiscAdapter();
        mViewPager.setAdapter(discAdapter);
        mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
    }


//    class DiscFragmentAdapter extends FragmentStatePagerAdapter {
//
//        public DiscFragmentAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return list.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
//        }
//    }

    LinkedList<View> mCaches = new LinkedList<View>();

    class PlayMusicDiscAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
//            RelativeLayout view = (RelativeLayout) object;
//            container.removeView(view);
            ((ViewPager)container).removeView(list.get(position));
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
//            container.addView(new ImageView(mContext));
//            return container;

            RelativeLayout relativeLayout = list.get(position);
            ImageView imageViewFront = new ImageView(mContext);
            imageViewFront.setImageResource(R.drawable.ic_pause);
//            imageViewFront.setLayoutParams();
            relativeLayout.addView(imageViewFront);
            ((ViewPager)container).addView(relativeLayout);
            return relativeLayout;

//            if (mViewPager == null) {
//                mViewPager = (ViewPager) container;
//            }
//            View view = null;
//            // 从废弃的里去取 取到则使用 取不到则创建
//            if (list.size() > 0) {
//                if (list.get(0) != null) {
//                    view = initView(list.get(0), position);
//                    list.remove(0);
//                }
//            }
//            view = initView(null, position);
//            mViewPager.addView(view);
//            return view;
        }

        private View initView(RelativeLayout view, int position) {
            ViewHolder viewHolder = null;
            if (view == null) {
                view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_disc_view, null);
                viewHolder = new ViewHolder();
                viewHolder.imageViewBack = (ImageView) view
                        .findViewById(R.id.ivDiscBlackgound);
                viewHolder.imageViewFront = (ImageView) view
                        .findViewById(R.id.ivDisc);

                viewHolder.imageViewBack.setImageDrawable(getDiscBlackgroundDrawable());

                int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.imageViewBack
                        .getLayoutParams();
                layoutParams.setMargins(0, marginTop, 0, 0);

                viewHolder.imageViewBack.setLayoutParams(layoutParams);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            return view;
        }

        private class ViewHolder{
            ImageView imageViewBack;
            ImageView imageViewFront;
        }
    }

    private void setDiscViewPic() {
//        getDiscDrawable(mPicPath);
//        mDiscView.setImageDrawable();
    }

    private void initDiscBlackground() {
        ImageView mDiscBlackground = (ImageView) mRootView.findViewById(R.id.ivDiscBlackgound);
        mDiscBlackground.setImageDrawable(getDiscBlackgroundDrawable());

        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDiscBlackground
                .getLayoutParams();
        layoutParams.setMargins(0, marginTop, 0, 0);

        mDiscBlackground.setLayoutParams(layoutParams);
    }

    /*得到唱盘背后半透明的圆形背景*/
    private Drawable getDiscBlackgroundDrawable() {
        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_disc_blackground), discSize, discSize, false);
        RoundedBitmapDrawable roundDiscDrawable = RoundedBitmapDrawableFactory.create
                (getResources(), bitmapDisc);
        return roundDiscDrawable;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
//                    mDiscView.setImageDrawable((LayerDrawable)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 得到唱盘图片
     * 唱盘图片由空心圆盘及音乐专辑图片“合成”得到
     */
    private void getDiscDrawable(final String musicPicRes) {
        final Message message = handler.obtainMessage();
        MusicApplication.getFixedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
                int musicPicSize = (int) (mScreenWidth * DisplayUtil.SCALE_MUSIC_PIC_SIZE);

                Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                        .drawable.ic_disc), discSize, discSize, false);
//        Bitmap bitmapMusicPic = getMusicPicBitmap(musicPicSize,musicPicRes);

                Bitmap bitmapMusicPic = null;
                try {
                    bitmapMusicPic = Glide.with(PlayMusicDialogFragment.this).load(musicPicRes)
                            .asBitmap().error(R.drawable.bg_default_disc).centerCrop().into(530,530).get();
                    BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
                    RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
                            (getResources(), bitmapMusicPic);

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

                    message.obj = layerDrawable;
                    message.what = 0;
                    message.sendToTarget();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
//                return layerDrawable;
            }
        });
    }

    private void setListener() {
//        mDisc.setPlayInfoListener(this);
    }

    private void initView() {
        mBackDiscImg.setImageDrawable(getDiscBlackgroundDrawable());
        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBackDiscImg
                .getLayoutParams();
        layoutParams.setMargins(0, marginTop, 0, 0);
        mBackDiscImg.setLayoutParams(layoutParams);

        mRootView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    /**
     * 设置背景
     */
    private void setPlayActivityBg() {
        MusicApplication.getFixedThreadPool().execute(runnable);

    }

    private Handler myHandler;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mMusicData == null) {
                return;
            }
            //拿到初始图
            Bitmap initBitmap = null;
            try {
                initBitmap = Glide.with(mContext)
                        .load(mMusicData.getMusicAlbumPicPath())
                        .asBitmap().error(R.drawable.login_bg_night).centerCrop().into(540, 960).get();
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

    /**
     * 初始化数据,用于展示
     */
    private void initData() {
        if (BaseActivity.mServiceDataTrans == null) {
            return;
        }
        mPlayingMusicId = BaseActivity.mServiceDataTrans.getPlayingMusicId();
        mMusicData = ((MainActivity) mContext).getDataFromPid(mPlayingMusicId);
    }

    private void initUi() {
        if (mMusicData == null) {
            return;
        }
        tvMusicName.setText(mMusicData.getMusicName());
        tvMusicPlayer.setText(mMusicData.getMusicPlayer());
        if (BaseActivity.mServiceDataTrans.isPlayingMusic()) {
            //正在播放，显示播放按钮
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
        }
        tvTotalTime.setText(getTextFromTime(mMusicData.getMusicTime()));

        //启动一个线程，一直刷新，每秒种刷新一次
        myHandler.sendEmptyMessageDelayed(1, 1000);
    }

    /**
     * 获取分秒
     *
     * @param time time
     * @return text 05：00
     */
    private String getTextFromTime(int time) {
        int minute = time / 1000 / 60;
        int second = time / 1000 - minute * 60;
        String min = "";
        String sec = "";
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

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLast:
                //上一曲，根据播放模式变化
                BaseActivity.mServiceDataTrans.playMusicFromClick(BaseActivity.mServiceDataTrans.getPlayPosition() - 1);
                mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
                initData();
                initUi();
                setPlayActivityBg();
                break;
            case R.id.ivPlayOrPause:
                //播放或者暂停
                if (BaseActivity.mServiceDataTrans.isPlayingMusic()) {
                    //暂停
                    ivPlayOrPause.setImageResource(R.drawable.ic_pause);
                } else {
                    //播放
                    ivPlayOrPause.setImageResource(R.drawable.ic_play);
                }
                BaseActivity.mServiceDataTrans.playOrPause();
                break;
            case R.id.ivNext:
                //下一曲，根据播放模式变化
                BaseActivity.mServiceDataTrans.playMusicFromClick(BaseActivity.mServiceDataTrans.getPlayPosition() + 1);
                //修改背景图片
                mViewPager.setCurrentItem(BaseActivity.mServiceDataTrans.getPlayPosition());
                initData();
                initUi();
                setPlayActivityBg();
                break;
            case R.id.iv_back:
//                finish();
                dismiss();
                break;
            case R.id.iv_play_type:
                break;
            case R.id.iv_play_list:
                break;
            default:
                break;
        }
    }
}
