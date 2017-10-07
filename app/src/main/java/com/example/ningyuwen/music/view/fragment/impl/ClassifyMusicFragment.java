package com.example.ningyuwen.music.view.fragment.impl;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.ClassifyMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.IClassifyMusicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningyuwen on 17-9-26.
 */

public class ClassifyMusicFragment extends Fragment implements IClassifyMusicFragment {

    private ExpandableListView mElClassifyMusic;
    private ClassifyMusicAdapter mAdapter;
    private List<List<MusicData>> mDatas;

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
        mDatas = ((MainActivity)getActivity()).getClassifyMusicInfo();
        mAdapter = new ClassifyMusicAdapter(getContext(), new ArrayList<String>(), mDatas);
        mElClassifyMusic.setAdapter(mAdapter);
    }
}
