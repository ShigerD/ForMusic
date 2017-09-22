package com.example.ningyuwen.music.model.entity.test;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 测试GreenDao
 * Created by ningyuwen on 17-9-22.
 */

@Entity
public class TestDao {
    @Id
    private long pId;
    @Property
    private String name;
    private int a;
    @Generated(hash = 1752232950)
    public TestDao(long pId, String name, int a) {
        this.pId = pId;
        this.name = name;
        this.a = a;
    }
    @Generated(hash = 1692582752)
    public TestDao() {
    }
    public long getPId() {
        return this.pId;
    }
    public void setPId(long pId) {
        this.pId = pId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getA() {
        return this.a;
    }
    public void setA(int a) {
        this.a = a;
    }

}
