package com.example.ningyuwen.music.view.activity.impl;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.presenter.impl.BasePresenter;
import com.example.ningyuwen.music.presenter.impl.MusicSongListPresenter;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.adapter.AllMusicInfoAdapter;
import com.example.ningyuwen.music.view.adapter.MusicSongListActivityAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌单页面
 * Created by ningyuwen on 17-10-12.
 */

public class MusicSongListActivity extends BaseActivity<MusicSongListPresenter> implements
        MusicSongListActivityAdapter.IAddItemOnClickListener {

    private long pid = 0;   //歌单id
    private String mSongListName = "";   //歌单名
    private String mSongListPicUrl = ""; //歌单图片
    private int mSongListNumber = 0;    //歌单中的歌曲总数
    private RecyclerView mRvSongListMusic;  //歌单中的音乐
    private TextView mTvPlayAllMusic;       //播放全部显示的音乐数目
    private MusicSongListActivityAdapter mAdapter;   //adapter
    private List<MusicData> mMusicDatas;    //音乐数据

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_song_list);

        //获取数据
        getIntentData();
        findViews();
        initData();
    }

    private void initData() {
        mTvPlayAllMusic.setText("播放全部(共" + mSongListNumber + "首)");
        if (mSongListNumber > 0) {
            //获取音乐数据
            mMusicDatas = new ArrayList<>();
            mRvSongListMusic.setLayoutManager(new LinearLayoutManager(this));
            mMusicDatas.addAll(mPresenter.getSongListInfoFromDB(pid));
            mAdapter = new MusicSongListActivityAdapter(this, mMusicDatas);
            mRvSongListMusic.setAdapter(mAdapter);
            mAdapter.setItemClickListener(this);
        }
    }

    private void findViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mToolbar.setNavigationIcon(R.drawable.actionbar_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle(mSongListName);
        //通过CollapsingToolbarLayout修改字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色
        mRvSongListMusic = (RecyclerView) findViewById(R.id.rv_song_list_music);
        mTvPlayAllMusic = (TextView) findViewById(R.id.tv_play_all_music);
        ImageView imageView = (ImageView) findViewById(R.id.iv_music_pic);
        if ("".equals(mSongListPicUrl) || mSongListPicUrl == null){
            Glide.with(this).load(R.drawable.bg_default_play_list).into(imageView);
        }else {
            Glide.with(this).load(mSongListPicUrl).into(imageView);
        }
        findViewById(R.id.rl_play_all_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放全部，删除之前的歌单信息，添加新的歌单信息，
                //前提是本歌单中存在音乐.
                if (mMusicDatas == null || mMusicDatas.size() == 0){
                    return;
                }
                BaseActivity.MUSIC_LIST_PLAY_NOW = StaticFinalUtil.HANDLER_SHOW_CUSTOM;
                showToast(v, "开始播放歌单：《" + mSongListName + "》");
                //修改BaseActivity中的mMusicDatas数据
                if (BaseActivity.mMusicDatas == null){
                    BaseActivity.mMusicDatas = new ArrayList<>();
                }
                BaseActivity.mMusicDatas.clear();
                //this.mMusicDatas已经是本歌单的数据了
                BaseActivity.mMusicDatas.addAll(mMusicDatas);
                //向Service传递数据
                initServiceData();
                BaseActivity.mServiceDataTrans.playMusicFromClick(0);
                //刷新播放页面
                sendBroadcast(new Intent().setAction(StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST));
            }
        });
    }

    /**
     * 获取intent中的数据
     */
    private void getIntentData() {
        pid = getIntent().getLongExtra("id", 0);
        mSongListName = getIntent().getStringExtra("name");
        mSongListPicUrl = getIntent().getStringExtra("picUrl");
        mSongListNumber = getIntent().getIntExtra("number",0);
    }

    @Override
    protected MusicSongListPresenter getPresenter() {
        return new MusicSongListPresenter(this);
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
     * 传回一个position，切换歌单，然后根据position确定播放的音乐
     * 歌单在这个activity已经存在
     * @param position position
     */
    @Override
    public void playMusicFromPosition(int position) {
        //此处是点击了歌单中的某一首音乐，所以不应该变换歌单信息，而只是将点击的音乐进行播放，
        //如果这首音乐在当前歌单中，则直接播放这首音乐，
        //不在当前歌单中，则将此音乐添加进歌单，
        //只有当点击播放全部时，才更换歌单


        //查找当前歌单是否存在本音乐
        int index = getPositionFromPid(this.mMusicDatas.get(position).getpId());
        if (index != -1){
            //存在，直接播放，且不更换BaseActivity.mMusicDatas数据，不刷新播放页面
            BaseActivity.mServiceDataTrans.playMusicFromClick(index);
        }else {
            //不存在，将音乐添加到歌单，然后播放
            index = BaseActivity.mServiceDataTrans.getPlayPosition();   //复用index
            BaseActivity.mMusicDatas.add(BaseActivity.mMusicDatas.size() , this.mMusicDatas.get(position));
            //更新歌单，更新播放页面
            //此处暂时为了简单处理，直接将后台播放列表删除，然后重新导入了
            initServiceData();

            //播放
            BaseActivity.mServiceDataTrans.playMusicFromClick(BaseActivity.mMusicDatas.size() - 1);
            //发送广播刷新播放页面
            sendBroadcast(new Intent().setAction(StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST));
        }
//
//        //修改BaseActivity中的mMusicDatas数据
//        if (BaseActivity.mMusicDatas == null){
//            BaseActivity.mMusicDatas = new ArrayList<>();
//        }
//        BaseActivity.mMusicDatas.clear();
//        //this.mMusicDatas已经是本歌单的数据了
//        BaseActivity.mMusicDatas.addAll(this.mMusicDatas);
//        //向Service传递数据
//        initServiceData();
//        BaseActivity.mServiceDataTrans.playMusicFromClick(position);
    }
}
