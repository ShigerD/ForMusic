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
import com.example.ningyuwen.music.view.adapter.CustomizeMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.ICustomizeMusicFragment;

/**
 * 自定歌单
 * Created by ningyuwen on 17-9-26.
 */

public class CustomizeMusicFragment extends Fragment implements ICustomizeMusicFragment {

    private RecyclerView mRvCustomizeMusic;   //RecyclerView 用于显示自定义歌单

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View customizeMusicFragment = inflater.inflate(R.layout.fragment_customize_music, container, false);
        mRvCustomizeMusic = customizeMusicFragment.findViewById(R.id.rv_customize_music);

        showCustomizeMusicInfo();

        return customizeMusicFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * 显示歌单信息，从数据库中读取
     */
    @Override
    public void showCustomizeMusicInfo() {
        mRvCustomizeMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        CustomizeMusicAdapter mAdapter = new CustomizeMusicAdapter(getActivity(),
                ((MainActivity)getActivity()).getSongListInfo());
        mRvCustomizeMusic.setAdapter(mAdapter);
    }
}
