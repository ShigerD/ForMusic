package com.example.ningyuwen.music.view.activity.i;

import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;

/**
 * 接口，负责MainActivity到Fragment的数据传输
 * Created by ningyuwen on 17-11-11.
 */

public interface IMainActivityToFragment {
    void refreshAllMusic();         //刷新音乐列表，初始化时通知几个Fragment获取相应数据
    void refreshAllMusicDislike(MusicData musicData);  //在我喜爱的页面将音乐取消喜爱，在所有音乐页面将它置为不喜欢
    void refreshCustomMusic(SongListInfo songListInfo);       //刷新歌单页面数据，传入SongListInfo,只刷新这一小部分数据，减少CPU消耗
}
