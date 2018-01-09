package com.example.ningyuwen.music.view.fragment.impl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.util.DisplayUtil;
import com.example.ningyuwen.music.view.activity.impl.PlayActivity;

import java.util.concurrent.ExecutionException;

/**
 * 播放页面的转盘,显示图片
 * Created by ningyuwen on 18-1-9.
 */

public class DiscViewFragment extends Fragment {

    private View mRootView; //根View

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private int mScreenWidth, mScreenHeight;
    private ImageView mDiscView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.layout_disc_view, container, false);
            mScreenWidth = DisplayUtil.getScreenWidth(getContext());
            mScreenHeight = DisplayUtil.getScreenHeight(getContext());
            initDiscBlackground();
            mDiscView = mRootView.findViewById(R.id.ivDisc);
            setDiscViewPic();
        }
        return mRootView;
    }

    private String mPicPath;

    /**
     * 设置图片路径
     * @param picPath picPath
     */
    public DiscViewFragment setMusicAlbumPic(String picPath){
        mPicPath = picPath;
        return this;
    }

    private void setDiscViewPic() {
        getDiscDrawable(mPicPath);
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
                    mDiscView.setImageDrawable((LayerDrawable)msg.obj);
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
                    bitmapMusicPic = Glide.with(DiscViewFragment.this).load(musicPicRes)
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

    private Bitmap getMusicPicBitmap(int musicPicSize, String musicPicRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(),R.drawable.play_plybar_bg,options);
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

        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.back_add_playlist, options), musicPicSize, musicPicSize, true);
    }
}
