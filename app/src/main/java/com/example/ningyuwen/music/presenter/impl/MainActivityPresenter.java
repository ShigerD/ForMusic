package com.example.ningyuwen.music.presenter.impl;

import android.util.Log;

import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.model.entity.music.MusicRecordInfo;
import com.example.ningyuwen.music.presenter.i.IMainActivityPresenter;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import java.util.ArrayList;
import java.util.List;

import greendao.gen.MusicBasicInfoDao;
import greendao.gen.MusicRecordInfoDao;

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

        MusicRecordInfoDao recordInfoDao = mDaoSession.getMusicRecordInfoDao();
        //插入或替换记录数据
        for (int i = 0;i < musicDatas.size();i++){
            MusicRecordInfo info = new MusicRecordInfo();
            info.setPId(musicDatas.get(i).getPId());
            recordInfoDao.insertOrReplace(info);
        }
    }

    /**
     * 获取音乐数据，包括MusicBasicInfo 和 MusicRecordInfoDao中的数据，通过pid查询，存储到 musicDataList返回
     * @param basicInfoList basicInfo
     * @return musicDataList
     */
    @Override
    public List<MusicData> getMusicAllInfo(List<MusicBasicInfo> basicInfoList) {
        List<MusicData> musicDataList = new ArrayList<>();
        for (int i = 0;i < basicInfoList.size();i++){
            MusicData data = new MusicData();
            data.setpId(basicInfoList.get(i).getPId());
            data.setMusicName(basicInfoList.get(i).getMusicName());
            data.setMusicPlayer(basicInfoList.get(i).getMusicPlayer());
            data.setMusicAlbum(basicInfoList.get(i).getMusicAlbum());
            data.setMusicFilePath(basicInfoList.get(i).getMusicFilePath());
            data.setMusicTime(basicInfoList.get(i).getMusicTime());
            data.setMusicFileSize(basicInfoList.get(i).getMusicFileSize());

            //通过pid在表recordInfo中查询信息存储到
            MusicRecordInfo recordInfo = mDaoSession.getMusicRecordInfoDao().load(basicInfoList.get(i).getPId());
            data.setMusicPlayTimes(recordInfo.getMusicPlayTimes());
            data.setLove(recordInfo.getIsLove());
            data.setMusicSongList(recordInfo.getMusicSongList());
            musicDataList.add(data);
        }
        return musicDataList;
    }

    /**
     * 从数据库获取数据
     * @return List<MusicData>
     */
    @Override
    public List<MusicData> getMusicBasicInfoFromDB() {
        List<MusicBasicInfo> musicBasicInfoList = mDaoSession.getMusicBasicInfoDao().loadAll();
        if (musicBasicInfoList == null || musicBasicInfoList.size() == 0){
            return new ArrayList<>();
        }
        return getMusicAllInfo(musicBasicInfoList);
    }


}
