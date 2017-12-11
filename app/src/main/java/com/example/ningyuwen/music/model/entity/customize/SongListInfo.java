package com.example.ningyuwen.music.model.entity.customize;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 自定义歌单 存储数据库的内容
 * Created by ningyuwen on 17-10-5.
 */

@Entity
public class SongListInfo {
    @Id
    private Long id;

    @Property
    private String name;    //歌单名
    private int number;     //歌单中的音乐数目
    private String songlistImgUrl;  //歌单封面图，取歌单第一首的图片

    @Generated(hash = 2096882423)
    public SongListInfo(Long id, String name, int number, String songlistImgUrl) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.songlistImgUrl = songlistImgUrl;
    }
    @Generated(hash = 1610687539)
    public SongListInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getNumber() {
        return this.number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public String getSonglistImgUrl() {
        return this.songlistImgUrl;
    }
    public void setSonglistImgUrl(String songlistImgUrl) {
        this.songlistImgUrl = songlistImgUrl;
    }

   

}
