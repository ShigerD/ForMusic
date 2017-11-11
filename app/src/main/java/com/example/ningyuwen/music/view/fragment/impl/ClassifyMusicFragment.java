package com.example.ningyuwen.music.view.fragment.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.ningyuwen.music.view.activity.i.IMainActivityToFragment;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.ClassifyMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.IClassifyMusicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌手分类页面
 * Created by ningyuwen on 17-9-26.
 */

public class ClassifyMusicFragment extends Fragment implements IClassifyMusicFragment,
        IMainActivityToFragment {

    private static final String TAG = "test22";
    private BroadcastReceiver receiver;
    private ExpandableListView mElClassifyMusic;
    private ClassifyMusicAdapter mAdapter;
    private List<List<MusicData>> mDatas;
    private int mGroupPosition = -1;  //记录当前播放的音乐人
    private boolean shouldRefreshList = false;  //判断是否需要刷新列表，在接收到广播时置为true

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View classifyMusicFragmentView = inflater.inflate(R.layout.fragment_classify_music, container, false);
        mElClassifyMusic = (ExpandableListView) classifyMusicFragmentView.findViewById(R.id.el_classify_music);

        mDatas = new ArrayList<>();
        showClassifyMusicInfo();
        return classifyMusicFragmentView;
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
                    Log.i(TAG, "onReceive: classify");
                    mDatas.clear();
                    showClassifyMusicInfo();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("AllMusicRefresh");  //重新导入音乐数据时，刷新列表
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
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
                    //如果不等，则说明换了音乐人，不再使用广播发送，使用接口回调
                    ArrayList<Long> musicId = new ArrayList<>();
                    for (int i = 0;i < mDatas.get(groupPosition).size();i++){
                        musicId.add(mDatas.get(groupPosition).get(i).getpId());
                    }
                    ((MainActivity)getActivity()).replaceMusicList(musicId, childPosition);

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

    /**
     * 刷新音乐列表，初始化时通知几个Fragment获取相应数据
     */
    @Override
    public void refreshAllMusic() {
        Log.i(TAG, "refreshAllMusic: 刷新列表class");
        if (mDatas == null){
            mDatas = new ArrayList<>();
        }
        mDatas.clear();
        showClassifyMusicInfo();
    }
}
