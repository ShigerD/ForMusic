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
    private String whichApp;          //那个app
    private int musicTime;              //歌曲长度
    private String musicAlbum;          //音乐专辑名
    private String musicAlbumId;        //音乐专辑ID，方便之后找出专辑图片
    private String musicFilePath;       //音乐文件路径
    private long musicFileSize;         //音乐文件大小
    private String musicLyricPath;      //音乐歌词文件路径
    private String musicAlbumPicUrl;    //音乐文件专辑图片，目前只能找到这个了 ，网易云音乐
    private String musicAlbumPicPath;   //音乐文件专辑图片,虾米音乐


    @Generated(hash = 69218259)
    public MusicBasicInfo(Long pId, String musicName, String musicPlayer,
            String whichApp, int musicTime, String musicAlbum, String musicAlbumId,
            String musicFilePath, long musicFileSize, String musicLyricPath,
            String musicAlbumPicUrl, String musicAlbumPicPath) {
        this.pId = pId;
        this.musicName = musicName;
        this.musicPlayer = musicPlayer;
        this.whichApp = whichApp;
        this.musicTime = musicTime;
        this.musicAlbum = musicAlbum;
        this.musicAlbumId = musicAlbumId;
        this.musicFilePath = musicFilePath;
        this.musicFileSize = musicFileSize;
        this.musicLyricPath = musicLyricPath;
        this.musicAlbumPicUrl = musicAlbumPicUrl;
        this.musicAlbumPicPath = musicAlbumPicPath;
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
    public String getMusicLyricPath() {
        return this.musicLyricPath;
    }
    public void setMusicLyricPath(String musicLyricPath) {
        this.musicLyricPath = musicLyricPath;
    }
    public String getMusicAlbumPicUrl() {
        return this.musicAlbumPicUrl;
    }
    public void setMusicAlbumPicUrl(String musicAlbumPicUrl) {
        this.musicAlbumPicUrl = musicAlbumPicUrl;
    }
    public String getMusicAlbumId() {
        return this.musicAlbumId;
    }
    public void setMusicAlbumId(String musicAlbumId) {
        this.musicAlbumId = musicAlbumId;
    }
    public String getMusicAlbumPicPath() {
        return this.musicAlbumPicPath;
    }
    public void setMusicAlbumPicPath(String musicAlbumPicPath) {
        this.musicAlbumPicPath = musicAlbumPicPath;
    }
    public String getWhichApp() {
        return this.whichApp;
    }
    public void setWhichApp(String whichApp) {
        this.whichApp = whichApp;
    }

}
