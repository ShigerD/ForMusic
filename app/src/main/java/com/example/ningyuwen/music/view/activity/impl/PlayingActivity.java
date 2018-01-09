package com.example.ningyuwen.music.view.activity.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.presenter.impl.BasePresenter;
import com.example.ningyuwen.music.presenter.impl.PlayingPresenter;

/**
 * Created by ningyuwen on 18-1-9.
 */

public class PlayingActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
    }

    @Override
    protected BasePresenter getPresenter() {
        return new PlayingPresenter(this);
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
}
