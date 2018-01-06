package com.example.ningyuwen.music.view.activity.i;

import com.example.ningyuwen.music.model.entity.classify.ClassifyMusicPlayer;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口，Mainactivity
 * Created by ningyuwen on 17-9-25.
 */

public interface IMainActivity {
    List<SongListInfo> getSongListInfo();  //获取歌单列表数据
    void addSongListToDB(SongListInfo info);  //添加歌单
    List<MusicData> getMyLoveMusicData();
    List<List<MusicData>> getClassifyMusicInfo(List<ClassifyMusicPlayer> musicPlayers);
    List<ClassifyMusicPlayer> getClassifyMusicPlayerInfo();
    void sendBroadCastForString(String string);
    void replaceMusicList(ArrayList<Long> musicInfoList, int position);  //替换歌单列表，某些播放状态下会使用到,position为点击的位置
    boolean existSongListName(String songlistName);     //判断歌单名是否存在或是否为空，不满足前两项才能添加歌单
    void addMusicToSongList(long musicId, long songListId);              //将音乐添加至歌单
}
