package com.example.ningyuwen.music.presenter.impl;

import android.util.Log;

import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.presenter.i.IMainActivityPresenter;
import com.example.ningyuwen.music.view.impl.MainActivity;

import java.util.List;

import greendao.gen.MusicBasicInfoDao;

/**
 * MainActivityPresenter对应MainActivity
 * Created by ningyuwen on 17-9-22.
 */

public class MainActivityPresenter extends BasePresenter<MainActivity>
        implements IMainActivityPresenter {
    public MainActivityPresenter(MainActivity view) {
        super(view);
    }

    private String TAG = "ningywuen";

    @Override
    public void saveMusicInfoFromSD(List<MusicBasicInfo> musicDatas) {
        //dao 基本信息
        if (musicDatas == null || musicDatas.size() == 0){
            Log.i(TAG, "saveMusicInfoFromSD: 0 ");
            return;
        }
        Log.i(TAG, "saveMusicInfoFromSD: 1  " + musicDatas.size());
        MusicBasicInfoDao basicInfoDao = mDaoSession.getMusicBasicInfoDao();
        basicInfoDao.insertOrReplaceInTx(musicDatas);

    }

    public int getSize(){
        return mDaoSession.getMusicBasicInfoDao().loadAll().size();
    }

}
