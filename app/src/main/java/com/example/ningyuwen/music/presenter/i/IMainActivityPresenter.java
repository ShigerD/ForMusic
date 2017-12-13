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

public interface IMainActivityPresenter {
    void saveMusicInfoFromSD(List<MusicBasicInfo> musicDatas);
    List<MusicData> getMusicAllInfo(List<MusicBasicInfo> basicInfoList); //基本信息和记录信息
    List<MusicData> getMusicBasicInfoFromDB();
    List<SongListInfo> getSongListInfoFromDB();
    void addSongListToDB(SongListInfo info);  //添加歌单
    void setIsLoveToDB(long pid, boolean isLove);  //是否喜愛
    void addLrcPathAndMusicPicToDB(String musicName, String musicPlayer, String musicPic,
                                   String filePath, String whichApp);  //扫描歌词文件，将文件路径存储到数据库
    void scanLyricFileFromSD() throws IOException;  //网易云音乐、QQ音乐、虾米音乐等几款音乐播放器歌词路径
    MusicBasicInfo getMusicDataUsePid(long pid);  //用pid查询音乐数据基本信息
    String getLyricFromDBUsePid(MusicBasicInfo musicBasicInfo);        //获取歌词数据
    List<Pair<Long, String>> analysisLyric(String lyric);               //解析歌词,返回歌词List
    boolean existSongListName(String songlistName);     //判断歌单名是否存在或是否为空，不满足前两项才能添加歌单
    void updateLoveMusicToDisLike(MusicData musicData); //将之前喜爱的音乐变为不喜欢
}
