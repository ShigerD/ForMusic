package com.example.ningyuwen.music.view.activity.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.presenter.impl.PlayPresenter;
import com.example.ningyuwen.music.view.activity.i.IPlayActivity;

/**
 * PlayActivity，播放音乐音乐
 * Created by ningyuwen on 17-11-13.
 */

public class PlayActivity extends BaseActivity<PlayPresenter> implements IPlayActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }

    @Override
    protected PlayPresenter getPresenter() {
        return new PlayPresenter(this);
    }
}
