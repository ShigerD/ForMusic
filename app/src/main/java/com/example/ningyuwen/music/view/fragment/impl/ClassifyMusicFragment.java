package com.example.ningyuwen.music.view.fragment.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.classify.ClassifyMusicPlayer;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.i.IMainActivityToFragment;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
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
    private View classifyMusicFragmentView;     //根布局
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (classifyMusicFragmentView == null) {
            classifyMusicFragmentView = inflater.inflate(R.layout.fragment_classify_music, container, false);
            mElClassifyMusic = classifyMusicFragmentView.findViewById(R.id.el_classify_music);
        }

        mDatas = new ArrayList<>();
        showClassifyMusicInfo();
        return classifyMusicFragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
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

            }
        };

        IntentFilter filter = new IntentFilter();
        mContext.registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    /**
     * 显示列表，按歌手分类
     */
    @Override
    public void showClassifyMusicInfo() {
        List<ClassifyMusicPlayer> musicPlayers = ((MainActivity)mContext).getClassifyMusicPlayerInfo();
        mDatas.addAll(((MainActivity)mContext).getClassifyMusicInfo(musicPlayers));
        if (musicPlayers == null || mDatas == null){
            ((MainActivity)mContext).showToast(mElClassifyMusic, "没有音乐文件");
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
                //点击其中一首开始播放，将后台音乐播放列表替换为此音乐人的歌曲

                //修改为正在播放的图标
//                ImageView imageView = v.findViewById(childPosition);
//                imageView.setImageResource(R.drawable.ic_classify_play_state_play);

                //如果之前不是播放歌手分类的歌单，那么将mGroupPosition置为-1,重新判断时，就可以切换歌单了
                if (BaseActivity.MUSIC_LIST_PLAY_NOW != StaticFinalUtil.MUSIC_LIST_PLAY_CLASSIFY_PLAYER){
                    mGroupPosition = -1;
                }
                if (groupPosition != mGroupPosition) {
                    mGroupPosition = groupPosition;

                    //如果不等，则说明换了音乐人，发送广播，替换音乐列表
                    //如果不等，则说明换了音乐人，不再使用广播发送，使用接口回调
                    BaseActivity.MUSIC_LIST_PLAY_NOW = StaticFinalUtil.MUSIC_LIST_PLAY_CLASSIFY_PLAYER;  //切换到歌手分类歌单
                    if (BaseActivity.mMusicDatas == null){
                        BaseActivity.mMusicDatas = new ArrayList<>();
                    }
                    BaseActivity.mMusicDatas.clear();
                    //this.mMusicDatas已经是本歌单的数据了
                    BaseActivity.mMusicDatas.addAll(mDatas.get(groupPosition));
                    //向Service传递数据
                    ((MainActivity)mContext).initServiceData();
                    ((MainActivity)mContext).playMusicOnBackstage(childPosition);
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                    localBroadcastManager.sendBroadcast(new Intent().setAction(
                            StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST).putExtra("position",childPosition));

//                    mContext.sendBroadcast(new Intent().setAction(
//                            StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST).putExtra("position",childPosition));


//                    ArrayList<Long> musicId = new ArrayList<>();
//                    for (int i = 0;i < mDatas.get(groupPosition).size();i++){
//                        musicId.add(mDatas.get(groupPosition).get(i).getpId());
//                    }
//                    ((MainActivity)mContext).replaceMusicList(musicId, childPosition);
                    return true;
                }else {
                    //相等，重新播放这首歌曲，提高效率，不替换播放列表
                    ((MainActivity)mContext).playMusicOnBackstage(childPosition);
//                    BaseActivity.mServiceDataTrans.playMusicFromClick(childPosition);

//                    Intent intent = new Intent("PlayMusic");
//                    intent.putExtra("palyPosition", childPosition);
//                    mContext.sendBroadcast(intent);
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

    @Override
    public void refreshAllMusicDislike(MusicData musicData) {

    }

    @Override
    public void refreshCustomMusic(SongListInfo songListInfo) {

    }
}
