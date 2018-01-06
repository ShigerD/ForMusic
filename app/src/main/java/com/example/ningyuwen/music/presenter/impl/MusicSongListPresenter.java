package com.example.ningyuwen.music.presenter.impl;

import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.model.entity.music.MusicRecordInfo;
import com.example.ningyuwen.music.presenter.i.IMusicSongListPresenter;
import com.example.ningyuwen.music.view.activity.impl.MusicSongListActivity;

import java.util.ArrayList;
import java.util.List;

import greendao.gen.MusicRecordInfoDao;

/**
 * 歌单页面
 * Created by ningyuwen on 17-12-21.
 */

public class MusicSongListPresenter extends BasePresenter<MusicSongListActivity>
        implements IMusicSongListPresenter{

    public MusicSongListPresenter(MusicSongListActivity view) {
        super(view);
    }

    /**
     * 根据pid查询满足条件的歌单中的音乐信息
     * @param musicListId   musicListId
     * @return  List<MusicData>
     */
    @Override
    public List<MusicData> getSongListInfoFromDB(long musicListId) {
        List<MusicRecordInfo> recordInfo = mDaoSession.getMusicRecordInfoDao().queryBuilder().where(
                MusicRecordInfoDao.Properties.MusicSongListId.eq(musicListId)).list();
        if (recordInfo == null || recordInfo.size() == 0){
            return null;
        }
        return getMusicAllInfoFromRecord(recordInfo);
    }
}
