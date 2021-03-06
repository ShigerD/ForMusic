package com.example.ningyuwen.music.presenter.i;

import android.util.Pair;

import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;

import java.io.IOException;
import java.util.List;

/**
 * BasePresenter实现
 * Created by ningyuwen on 18-1-6.
 */

public interface IBasePresenter {
    void saveMusicInfoFromSD(List<MusicBasicInfo> musicDatas);
    List<MusicData> getMusicAllInfoFromBasic(List<MusicBasicInfo> basicInfoList); //基本信息和记录信息
    List<MusicData> getMusicBasicInfoFromDB();
    void setIsLoveToDB(long pid, boolean isLove);  //是否喜愛
    void addLrcPathAndMusicPicToDB(String musicName, String musicPlayer, String musicPic,
                                   String filePath, String whichApp);  //扫描歌词文件，将文件路径存储到数据库
    void scanLyricFileFromSD() throws IOException;  //网易云音乐、QQ音乐、虾米音乐等几款音乐播放器歌词路径
    MusicBasicInfo getMusicDataUsePid(long pid);  //用pid查询音乐数据基本信息
    String getLyricFromDBUsePid(MusicBasicInfo musicBasicInfo);        //获取歌词数据
    List<Pair<Long, String>> analysisLyric(String lyric);               //解析歌词,返回歌词List
}
