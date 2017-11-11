package com.example.ningyuwen.music.view.activity.i;

/**
 * 接口，负责MainActivity到Fragment的数据传输
 * Created by ningyuwen on 17-11-11.
 */

public interface IMainActivityToFragment {
    void refreshAllMusic();         //刷新音乐列表，初始化时通知几个Fragment获取相应数据

}
