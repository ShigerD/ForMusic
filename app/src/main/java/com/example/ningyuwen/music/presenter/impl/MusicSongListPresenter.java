package com.example.ningyuwen.music.presenter.impl;

import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.model.entity.music.MusicRecordInfo;
import com.example.ningyuwen.music.presenter.i.IMusicSongListPresenter;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
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
        return getMusicAllInfo(recordInfo);
    }

    /**
     * 获取音乐数据，包括MusicBasicInfo 和 MusicRecordInfoDao中的数据，通过pid查询，存储到 musicDataList返回
     * @param recordInfos recordInfos
     * @return musicDataList
     */
    public List<MusicData> getMusicAllInfo(List<MusicRecordInfo> recordInfos) {
        List<MusicData> musicDataList = new ArrayList<>();
        for (int i = 0;i < recordInfos.size();i++){
            MusicData data = new MusicData();
            MusicBasicInfo basicInfo = mDaoSession.getMusicBasicInfoDao().load(recordInfos.get(i).getPId());    //当前音乐的记录信息，根据记录信息获取基本信息

            data.setpId(recordInfos.get(i).getPId());
            data.setMusicName(basicInfo.getMusicName());
            data.setMusicPlayer(basicInfo.getMusicPlayer());
            data.setMusicAlbum(basicInfo.getMusicAlbum());
            data.setMusicFilePath(basicInfo.getMusicFilePath());
            data.setMusicTime(basicInfo.getMusicTime());
            data.setMusicFileSize(basicInfo.getMusicFileSize());
            data.setMusicAlbumPicUrl(basicInfo.getMusicAlbumPicUrl());
            data.setMusicAlbumPicPath(basicInfo.getMusicAlbumPicPath());

            //加上记录信息
            data.setMusicPlayTimes(recordInfos.get(i).getMusicPlayTimes());
            data.setLove(recordInfos.get(i).getIsLove());
            data.setMusicSongList(recordInfos.get(i).getMusicSongList());
            musicDataList.add(data);
        }
        return musicDataList;
    }
}
