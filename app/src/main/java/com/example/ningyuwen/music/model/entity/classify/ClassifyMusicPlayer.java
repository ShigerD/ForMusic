package com.example.ningyuwen.music.model.entity.classify;

/**
 * 歌手分类页面的group列表数据
 * Created by ningyuwen on 17-10-7.
 */

public class ClassifyMusicPlayer {
    private String musicPlayerName;  //音乐人
    private int musicNumber;         //数量

    public String getMusicPlayerName() {
        return musicPlayerName;
    }

    public void setMusicPlayerName(String musicPlayerName) {
        this.musicPlayerName = musicPlayerName;
    }

    public int getMusicNumber() {
        return musicNumber;
    }

    public void setMusicNumber(int musicNumber) {
        this.musicNumber = musicNumber;
    }
}
