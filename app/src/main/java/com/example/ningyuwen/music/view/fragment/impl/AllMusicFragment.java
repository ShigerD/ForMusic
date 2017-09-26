package com.example.ningyuwen.music.view.fragment.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.AllMusicInfoAdapter;

import java.util.List;

/**
 * 所有歌曲页面
 * Created by ningyuwen on 17-9-26.
 */

public class AllMusicFragment extends Fragment implements AllMusicInfoAdapter.AddItemClickListener {
    private BroadcastReceiver receiver;
    private List<MusicData> mMusicDatas;
    private RecyclerView mRvAllMusicInfo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View allMusicFragmentView = inflater.inflate(R.layout.fragment_all_music, container, false);
        mRvAllMusicInfo = allMusicFragmentView.findViewById(R.id.rv_all_music_info);




        Log.i("test", "onCreateView: ");

        return allMusicFragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createBroadcastReceiver();
    }

    /**
     * 广播接收
     */
    private void createBroadcastReceiver() {
        Log.i("test", "createBroadcastReceiver: ");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("test", "onReceive: " + action);
                if ("AllMusicRefresh".equals(action)){
//                    mMusicDatas = MainActivity.mMusicDatas;

                    mMusicDatas = ((MainActivity)getActivity()).getMusicDatas();
//                    ((MainActivity)getActivity()).clearMusicData();
                    showMusicInfo();
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("AllMusicRefresh");
        getActivity().registerReceiver(receiver, filter);
    }

    private void showMusicInfo() {
        Log.i("test", "showMusicInfo: " + mMusicDatas.size());
        mRvAllMusicInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        AllMusicInfoAdapter mAdapter = new AllMusicInfoAdapter(getActivity(), mMusicDatas);
        mRvAllMusicInfo.setAdapter(mAdapter);
        mAdapter.addItemClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    /**
     * 接口回调
     * @param position position
     */
    @Override
    public void playMusic(int position) {
        ((MainActivity)getActivity()).showToast("音乐位置： " + position);
//        sendB
        ((MainActivity)getActivity()).playMusicOnBackstage(position);

    }

    /**
     * 接口回调，设置是否是喜爱
     * @param position position
     */
    @Override
    public void setIsLove(int position) {
        ((MainActivity)getActivity()).showToast("是否喜爱： " + mMusicDatas.get(position).isLove());
    }
}
