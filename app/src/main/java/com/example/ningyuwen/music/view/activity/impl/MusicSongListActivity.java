package com.example.ningyuwen.music.view.activity.impl;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.example.ningyuwen.music.view.adapter.AllMusicInfoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌单页面
 * Created by ningyuwen on 17-10-12.
 */

public class MusicSongListActivity extends BaseActivity<MusicSongListPresenter> {

    private long pid;   //歌单id
    private String mSongListName;   //歌单名
    private String mSongListPicUrl; //歌单图片
    private int mSongListNumber;    //歌单中的歌曲总数
    private RecyclerView mRvSongListMusic;  //歌单中的音乐
    private TextView mTvPlayAllMusic;       //播放全部显示的音乐数目
    private AllMusicInfoAdapter mAdapter;   //adapter
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
            mMusicDatas.addAll(mPresenter.getSongListInfoFromDB(pid));
            mAdapter = new AllMusicInfoAdapter(this, mMusicDatas);
            mRvSongListMusic.setAdapter(mAdapter);
        }
    }

    private void findViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (!"".equals(mSongListPicUrl)){
            Glide.with(this).load(mSongListPicUrl).into(imageView);
        }else {
            Glide.with(this).load(R.drawable.bg_default_play_list).into(imageView);
        }
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
}
