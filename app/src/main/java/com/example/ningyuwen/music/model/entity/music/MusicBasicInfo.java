package com.example.ningyuwen.music.model.entity.music;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * MusicBasicInfo保存从SD卡扫描道德音乐文件的基本信息，
 * MusicInfo保存音乐对应的pId下的 其他信息，例如：播放次数、对应歌单名、我喜爱的
 * Created by ningyuwen on 17-9-22.
 */


@Entity
public class MusicBasicInfo {
    @Id
    private Long pId;

    @Property
    private String musicName;
    private String musicPlayer;
    private int musicTime;       //歌曲长度
    private String musicAlbum;      //音乐专辑名
    private String musicFilePath;   //音乐文件路径
    private long musicFileSize;     //音乐文件大小
    @Generated(hash = 990824296)
    public MusicBasicInfo(Long pId, String musicName, String musicPlayer,
            int musicTime, String musicAlbum, String musicFilePath,
            long musicFileSize) {
        this.pId = pId;
        this.musicName = musicName;
        this.musicPlayer = musicPlayer;
        this.musicTime = musicTime;
        this.musicAlbum = musicAlbum;
        this.musicFilePath = musicFilePath;
        this.musicFileSize = musicFileSize;
    }
    @Generated(hash = 1276167751)
    public MusicBasicInfo() {
    }
    public Long getPId() {
        return this.pId;
    }
    public void setPId(Long pId) {
        this.pId = pId;
    }
    public String getMusicName() {
        return this.musicName;
    }
    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
    public String getMusicPlayer() {
        return this.musicPlayer;
    }
    public void setMusicPlayer(String musicPlayer) {
        this.musicPlayer = musicPlayer;
    }
    public int getMusicTime() {
        return this.musicTime;
    }
    public void setMusicTime(int musicTime) {
        this.musicTime = musicTime;
    }
    public String getMusicAlbum() {
        return this.musicAlbum;
    }
    public void setMusicAlbum(String musicAlbum) {
        this.musicAlbum = musicAlbum;
    }
    public String getMusicFilePath() {
        return this.musicFilePath;
    }
    public void setMusicFilePath(String musicFilePath) {
        this.musicFilePath = musicFilePath;
    }
    public long getMusicFileSize() {
        return this.musicFileSize;
    }
    public void setMusicFileSize(long musicFileSize) {
        this.musicFileSize = musicFileSize;
    }
    

}
