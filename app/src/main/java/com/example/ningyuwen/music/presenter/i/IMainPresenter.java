package com.example.ningyuwen.music.presenter.i;

import android.util.Pair;

import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.model.entity.music.MusicRecordInfo;

import java.io.IOException;
import java.util.List;

/**
 * 主页面Presenter接口
 * Created by ningyuwen on 17-9-22.
 */

public interface IMainPresenter {
    List<SongListInfo> getSongListInfoFromDB();
    void addSongListToDB(SongListInfo info);  //添加歌单
    boolean existSongListName(String songlistName);     //判断歌单名是否存在或是否为空，不满足前两项才能添加歌单
    void updateLoveMusicToDisLike(MusicData musicData); //将之前喜爱的音乐变为不喜欢
    void addMusicToSongList(long musicId, long songListId);              //将音乐添加至歌单
}
