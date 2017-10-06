package com.example.ningyuwen.music.view.activity.i;

import com.example.ningyuwen.music.model.entity.customize.SongListInfo;

import java.util.List;

/**
 * 接口，Mainactivity
 * Created by ningyuwen on 17-9-25.
 */

public interface IMainActivity {
    List<SongListInfo> getSongListInfo();  //获取歌单列表数据
    void addSongListToDB(SongListInfo info);  //添加歌单
}
