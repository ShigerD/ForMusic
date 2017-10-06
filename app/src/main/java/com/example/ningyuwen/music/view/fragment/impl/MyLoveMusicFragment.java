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
import com.example.ningyuwen.music.view.adapter.MyLoveMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.IMyLoveMusicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 我喜愛的音樂頁面
 * Created by ningyuwen on 17-9-26.
 */

public class MyLoveMusicFragment extends Fragment implements IMyLoveMusicFragment, MyLoveMusicAdapter.AddItemClickListener {
    private BroadcastReceiver receiver;
    private RecyclerView mRvMyLoveMusic;
    private List<MusicData> mMyLoveMusicDatas;
    private MyLoveMusicAdapter mAdapter;
    private boolean shouldRefreshList = false;  //判断是否需要刷新列表，在接收到广播时置为true

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View allMusicFragmentView = inflater.inflate(R.layout.fragment_love_music, container, false);
        mRvMyLoveMusic = (RecyclerView) allMusicFragmentView.findViewById(R.id.rv_mylove_music);
        mMyLoveMusicDatas = new ArrayList<>();
        showMyLoveMusicInfo();
        return allMusicFragmentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && shouldRefreshList){
            mAdapter.notifyDataSetChanged();
            shouldRefreshList = false;
        }
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
                if ("SetMyLove".equals(action)){
                    shouldRefreshList = true;
                    if ("delete".equals(intent.getStringExtra("status"))){
                        //刪除
                        for (int i = 0; i < mMyLoveMusicDatas.size();i++){
                            if (intent.getLongExtra("pid", 0) == mMyLoveMusicDatas.get(i).getpId()){
                                //id相同，刪除
                                mMyLoveMusicDatas.remove(mMyLoveMusicDatas.get(i));
                                break;
                            }
                        }
                    }else if ("add".equals(intent.getStringExtra("status"))){
                        //添加
                        MusicData musicData = ((MainActivity)getActivity()).getDataFromPid(
                                intent.getLongExtra("pid", 0));
                        if (musicData == null){
                            return;
                        }
                        mMyLoveMusicDatas.add(0, musicData);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("SetMyLove");
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        if (mMyLoveMusicDatas != null){
            mMyLoveMusicDatas.clear();
            mMyLoveMusicDatas = null;
        }
    }

    /**
     * 展示我喜愛的音樂列表
     */
    @Override
    public void showMyLoveMusicInfo() {
        mRvMyLoveMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        mMyLoveMusicDatas.clear();
        mMyLoveMusicDatas.addAll(((MainActivity)getActivity()).getMyLoveMusicData());
        mAdapter = new MyLoveMusicAdapter(getActivity(), mMyLoveMusicDatas);
        mRvMyLoveMusic.setAdapter(mAdapter);
        mAdapter.addItemClickListener(this);
    }

    /**
     * 我喜爱的音乐列表属于 所有音乐列表，但是此处的position不同于所有音乐列表的poisition
     * 暂时不写这个，后面确定：播放我喜爱的音乐时，后台播放列表的音乐数据
     * @param position position
     */
    @Override
    public void playMusic(int position) {
        ((MainActivity)getActivity()).showToast("音乐位置： " + position);
        ((MainActivity)getActivity()).playMusicOnBackstage(position);
    }

    @Override
    public void setIsLove(int position) {

    }
}
