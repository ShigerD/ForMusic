package com.example.ningyuwen.music.view.activity.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.presenter.impl.PlayPresenter;
import com.example.ningyuwen.music.view.activity.i.IPlayActivity;
import com.example.ningyuwen.music.view.widget.BackgourndAnimationRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * PlayActivity，播放音乐音乐
 * Created by ningyuwen on 17-11-13.
 */

public class PlayActivity extends BaseActivity<PlayPresenter> implements IPlayActivity {


    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.ivDiscBlackgound)
    ImageView ivDiscBlackgound;
    @BindView(R.id.vpDiscContain)
    ViewPager vpDiscContain;
    @BindView(R.id.ivNeedle)
    ImageView ivNeedle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStatusBarTrans();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();


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
        mPlayingMusicId = mServiceDataTrans.getPlayingMusicId();
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

    @Override
    public void refreshPlayPauseView(boolean play) {

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

    @OnClick({R.id.ivLast, R.id.ivPlayOrPause, R.id.ivNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLast:
                break;
            case R.id.ivPlayOrPause:
                break;
            case R.id.ivNext:
                break;
        }
    }
}
