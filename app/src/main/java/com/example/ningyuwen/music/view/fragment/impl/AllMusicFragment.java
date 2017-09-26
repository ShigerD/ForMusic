package com.example.ningyuwen.music.view.fragment.impl;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.AllMusicInfoAdapter;

/**
 * 所有歌曲页面
 * Created by ningyuwen on 17-9-26.
 */

public class AllMusicFragment extends Fragment {

    private RecyclerView mRvAllMusicInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View allMusicFragmentView = inflater.inflate(R.layout.fragment_all_music, container, false);
        mRvAllMusicInfo = allMusicFragmentView.findViewById(R.id.rv_all_music_info);


//        mRvAllMusicInfo.setLayoutManager(new LinearLayoutManager(getContext()));
//        AllMusicInfoAdapter mAdapter = new AllMusicInfoAdapter((MainActivity)getActivity().get);
//        mRvAllMusicInfo.setAdapter(mAdapter);



        return allMusicFragmentView;
    }
}
