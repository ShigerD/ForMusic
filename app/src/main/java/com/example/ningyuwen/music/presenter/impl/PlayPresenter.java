package com.example.ningyuwen.music.presenter.impl;


import com.example.ningyuwen.music.presenter.i.IPlayPresenter;
import com.example.ningyuwen.music.view.activity.impl.PlayActivity;

/**
 * PlayPresenter，处理PlayActivity（播放页面）的逻辑运算
 * Created by ningyuwen on 17-11-13.
 */

public class PlayPresenter extends BasePresenter<PlayActivity> implements IPlayPresenter {


    public PlayPresenter(PlayActivity view) {
        super(view);
    }
}
