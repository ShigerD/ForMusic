package com.example.ningyuwen.music.view.fragment.impl;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.classify.ClassifyMusicPlayer;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.ClassifyMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.IClassifyMusicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌手分类页面
 * Created by ningyuwen on 17-9-26.
 */

public class ClassifyMusicFragment extends Fragment implements IClassifyMusicFragment {

    private ExpandableListView mElClassifyMusic;
    private ClassifyMusicAdapter mAdapter;
    private List<List<MusicData>> mDatas;
    private int mGroupPosition = -1;  //记录当前播放的音乐人

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View classifyMusicFragmentView = inflater.inflate(R.layout.fragment_classify_music, container, false);
        mElClassifyMusic = (ExpandableListView) classifyMusicFragmentView.findViewById(R.id.el_classify_music);
        showClassifyMusicInfo();
        return classifyMusicFragmentView;
    }

    /**
     * 显示列表，按歌手分类
     */
    @Override
    public void showClassifyMusicInfo() {
        List<ClassifyMusicPlayer> musicPlayers = ((MainActivity)getActivity()).getClassifyMusicPlayerInfo();
        mDatas = ((MainActivity)getActivity()).getClassifyMusicInfo(musicPlayers);
        if (musicPlayers == null || mDatas == null){
            ((MainActivity)getActivity()).showToast(mElClassifyMusic, "没有音乐文件");
            return;
        }
        mAdapter = new ClassifyMusicAdapter(getContext(), musicPlayers, mDatas);
        mElClassifyMusic.setAdapter(mAdapter);
        setChildItemClickListener();
    }

    /**
     * 音乐条目设置监听
     */
    private void setChildItemClickListener() {
        mElClassifyMusic.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //点击了其中一条，则将对应的音乐人的所有音乐替换后台播放列表
                if (groupPosition != mGroupPosition) {
                    mGroupPosition = groupPosition;
                    //如果不等，则说明换了音乐人，发送广播，替换音乐列表
                    Intent intent = new Intent("ReplaceMusicList");
                    ArrayList<String> paths = new ArrayList<String>();
                    for (int i = 0;i < mDatas.get(groupPosition).size();i++){
                        paths.add(mDatas.get(groupPosition).get(i).getMusicFilePath());
                    }
                    intent.putStringArrayListExtra("musicInfoList", paths);
                    intent.putExtra("position", childPosition);
                    getActivity().sendBroadcast(intent);
                    return true;
                }else {
                    //相等，重新播放这首歌曲，提高效率，不替换播放列表
                    Intent intent = new Intent("PlayMusic");
                    intent.putExtra("palyPosition", childPosition);
                    getActivity().sendBroadcast(intent);
                }
                return false;
            }
        });
    }
}
