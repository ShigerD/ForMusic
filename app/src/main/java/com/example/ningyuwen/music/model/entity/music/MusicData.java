package com.example.ningyuwen.music.model.entity.music;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * MusicData,保存音乐文件信息,包括基本信息和记录信息，中间的纽带是pId
 * Created by ningyuwen on 17-9-22.
 */

public class MusicData {
    private long pId;
    private String musicName;
    private String musicPlayer;
    private int musicTime;       //歌曲长度
    private String musicAlbum;      //音乐专辑名
    private String musicFilePath;   //音乐文件路径
    private long musicFileSize;     //音乐文件大小
    private int musicPlayTimes;  //音乐播放次数
    private boolean isLove;    //是否添加到我喜爱的
    private String musicSongList;    //被添加到的歌单名,用户自定义歌单

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicPlayer() {
        return musicPlayer;
    }

    public void setMusicPlayer(String musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public int getMusicTime() {
        return musicTime;
    }

    public void setMusicTime(int musicTime) {
        this.musicTime = musicTime;
    }

    public String getMusicAlbum() {
        return musicAlbum;
    }

    public void setMusicAlbum(String musicAlbum) {
        this.musicAlbum = musicAlbum;
    }

    public String getMusicFilePath() {
        return musicFilePath;
    }

    public void setMusicFilePath(String musicFilePath) {
        this.musicFilePath = musicFilePath;
    }

    public long getMusicFileSize() {
        return musicFileSize;
    }

    public void setMusicFileSize(long musicFileSize) {
        this.musicFileSize = musicFileSize;
    }

    public int getMusicPlayTimes() {
        return musicPlayTimes;
    }

    public void setMusicPlayTimes(int musicPlayTimes) {
        this.musicPlayTimes = musicPlayTimes;
    }

    public boolean isLove() {
        return isLove;
    }

    public void setLove(boolean love) {
        isLove = love;
    }

    public String getMusicSongList() {
        return musicSongList;
    }

    public void setMusicSongList(String musicSongList) {
        this.musicSongList = musicSongList;
    }
}
