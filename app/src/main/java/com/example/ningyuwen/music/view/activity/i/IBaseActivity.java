package com.example.ningyuwen.music.view.activity.i;

import com.example.ningyuwen.music.model.entity.music.MusicData;

/**
 * Created by ningyuwen on 18-1-6.
 */

public interface IBaseActivity {
    MusicData getDataFromPid(long pid);
    void setIsLoveToDB(long pid, boolean isLove);
}
