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
    private String name;
    private int number;
    @Generated(hash = 572508834)
    public SongListInfo(Long id, String name, int number) {
        this.id = id;
        this.name = name;
        this.number = number;
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

   

}
