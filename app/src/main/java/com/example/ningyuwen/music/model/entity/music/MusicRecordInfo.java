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
    private Long pId;   //id对应到MusicBasicInfo 中的音乐文件id

    @Property
    private int musicPlayTimes = 0;  //音乐播放次数
    private boolean isLove = false;    //是否添加到我喜爱的
    private String musicSongList = "";    //被添加到的歌单名,用户自定义歌单
    private long musicSongListId;       //被添加到的歌单id

    @Generated(hash = 179489123)
    public MusicRecordInfo(Long pId, int musicPlayTimes, boolean isLove,
            String musicSongList, long musicSongListId) {
        this.pId = pId;
        this.musicPlayTimes = musicPlayTimes;
        this.isLove = isLove;
        this.musicSongList = musicSongList;
        this.musicSongListId = musicSongListId;
    }
    @Generated(hash = 1587270878)
    public MusicRecordInfo() {
    }
    public Long getPId() {
        return this.pId;
    }
    public void setPId(Long pId) {
        this.pId = pId;
    }
    public int getMusicPlayTimes() {
        return this.musicPlayTimes;
    }
    public void setMusicPlayTimes(int musicPlayTimes) {
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
    public long getMusicSongListId() {
        return this.musicSongListId;
    }
    public void setMusicSongListId(long musicSongListId) {
        this.musicSongListId = musicSongListId;
    }

    

}
