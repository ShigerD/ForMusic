package com.example.ningyuwen.music.presenter.i;

import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;

import java.util.List;

/**
 * 主页面Presenter接口
 * Created by ningyuwen on 17-9-22.
 */

public interface IMainActivityPresenter {
    void saveMusicInfoFromSD(List<MusicBasicInfo> musicDatas);
}
