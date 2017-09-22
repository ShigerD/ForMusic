package com.example.ningyuwen.music.model.entity.music;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 保存播放次数、对应歌单名、我喜爱的
 * Created by ningyuwen on 17-9-22.
 */

@Entity
public class MusicRecordInfo {
    @Id
    private long pId;   //id对应到MusicBasicInfo 中的音乐文件id

    @Property
    private String musicPlayTimes;  //音乐播放次数
    private boolean isLove;    //是否添加到我喜爱的
    private String musicSongList;    //被添加到的歌单名,用户自定义歌单
    @Generated(hash = 1223369054)
    public MusicRecordInfo(long pId, String musicPlayTimes, boolean isLove,
            String musicSongList) {
        this.pId = pId;
        this.musicPlayTimes = musicPlayTimes;
        this.isLove = isLove;
        this.musicSongList = musicSongList;
    }
    @Generated(hash = 1587270878)
    public MusicRecordInfo() {
    }
    public long getPId() {
        return this.pId;
    }
    public void setPId(long pId) {
        this.pId = pId;
    }
    public String getMusicPlayTimes() {
        return this.musicPlayTimes;
    }
    public void setMusicPlayTimes(String musicPlayTimes) {
        this.musicPlayTimes = musicPlayTimes;
    }
    public boolean getIsLove() {
        return this.isLove;
    }
    public void setIsLove(boolean isLove) {
        this.isLove = isLove;
    }
    public String getMusicSongList() {
        return this.musicSongList;
    }
    public void setMusicSongList(String musicSongList) {
        this.musicSongList = musicSongList;
    }

}
