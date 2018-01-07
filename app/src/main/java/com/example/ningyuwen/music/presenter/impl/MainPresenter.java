package com.example.ningyuwen.music.presenter.impl;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.model.entity.music.MusicRecordInfo;
import com.example.ningyuwen.music.presenter.i.IMainPresenter;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import java.util.ArrayList;
import java.util.List;

import greendao.gen.SongListInfoDao;

/**
 * MainActivityPresenter对应MainActivity
 * Created by ningyuwen on 17-9-22.
 */

public class MainPresenter extends BasePresenter<MainActivity>
        implements IMainPresenter {

    public MainPresenter(MainActivity view) {
        super(view);
    }

    private String TAG = "ningywuen";

    /**
     * 获取歌单信息
     * @return
     */
    @Override
    public List<SongListInfo> getSongListInfoFromDB() {
        List<SongListInfo> songListInfoList = mDaoSession.getSongListInfoDao().loadAll();
        if (songListInfoList == null || songListInfoList.size() == 0){
            return new ArrayList<>();
        }
        return songListInfoList;
    }

    @Override
    public void addSongListToDB(SongListInfo info) {
        mDaoSession.getSongListInfoDao().insertOrReplace(info);
    }

    /**
     * 判断歌单名是否存在或是否为空，不满足前两项才能添加歌单
     * @param songlistName 歌单名
     * @return bool
     */
    @Override
    public boolean existSongListName(String songlistName) {
        return mDaoSession.getSongListInfoDao().queryBuilder().where(
                SongListInfoDao.Properties.Name.eq(songlistName)).list().size() > 0;
    }

    /**
     * 将之前喜爱的音乐变为不喜欢
     * @param musicData musicData
     */
    @Override
    public void updateLoveMusicToDisLike(MusicData musicData) {
        MusicRecordInfo info = mDaoSession.getMusicRecordInfoDao().load(musicData.getpId());
        info.setIsLove(false);
        mDaoSession.getMusicRecordInfoDao().update(info);
    }

    /**
     * 将音乐添加至歌单，主要是修改记录信息，而不修改基本信息
     * @param musicId 音乐id
     * @param songListId  歌单id
     */
    @Override
    public void addMusicToSongList(long musicId, long songListId) {
        //修改记录信息中的歌单id
        MusicRecordInfo info = mDaoSession.getMusicRecordInfoDao().load(musicId);
        if (info.getMusicSongListId() == songListId){
            //歌曲以存在于此歌单，
            mView.showToast("此歌曲已存在于本歌单中");
            return;
        }
        info.setMusicSongListId(songListId);
        mDaoSession.getMusicRecordInfoDao().update(info);

        //修改SongListInfo表中的number和URL信息
        String urlPath = mDaoSession.getMusicBasicInfoDao().load(musicId).getMusicAlbumPicPath();
        SongListInfo songListInfo = mDaoSession.getSongListInfoDao().load(songListId);
        int musicNumber = songListInfo.getNumber();
        songListInfo.setNumber(++musicNumber);
        songListInfo.setSonglistImgUrl(urlPath);
        mDaoSession.getSongListInfoDao().update(songListInfo);
        mView.showToast("添加成功");

        //修改歌单页面数据
        mView.refreshCustomMusic(songListInfo);
    }

    /**
     * 传入歌词string，文本文件路径，用于解析歌词文件
     * @param lyricStr string
     * @param filePath string:文件路径
     */
    private void analysisLyric(String lyricStr, String filePath){
        try {
            JSONObject jsonObject = (JSONObject) JSON.parse(lyricStr);
            String[] musicPlayerInfo = jsonObject.getString("kalaokLyric").split("\n", 5);
            for (int i = 0;i < 4;i++){
                musicPlayerInfo[i] = musicPlayerInfo[i]
                        .replace("[", "")
                        .replace("]","");
            }

            String musicName = musicPlayerInfo[0].replace("ti:", "");
            String musicPlayer = musicPlayerInfo[1].replace("ar:", "");

            //在数据库中查询音乐名和音乐人对应的歌曲名
//            addLyricPathToDB(musicName, musicPlayer, filePath);

        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "run: 异常");

//            //取出musicID  http://music.163.com/#/song?id=
//            JSONObject jsonObject = (JSONObject) JSON.parse(lyricStr);
//            String musicId = jsonObject.getString("musicId");
//            Log.i(TAG, "analysisLyric: " + getMusicNameFromNet(musicId));

        }
    }

}
