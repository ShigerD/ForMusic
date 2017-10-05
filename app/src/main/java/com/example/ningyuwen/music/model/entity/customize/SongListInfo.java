package com.example.ningyuwen.music.model.entity.customize;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 自定义歌单 存储数据库的内容
 * Created by ningyuwen on 17-10-5.
 */

@Entity
public class SongListInfo {

    @Property
    private String name;
    private int number;
    @Generated(hash = 1502950933)
    public SongListInfo(String name, int number) {
        this.name = name;
        this.number = number;
    }
    @Generated(hash = 1610687539)
    public SongListInfo() {
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

}
