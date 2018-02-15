package com.example.ningyuwen.music.view.fragment.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.i.IMainActivityToFragment;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.MyLoveMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.IMyLoveMusicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 我喜愛的音樂頁面
 * Created by ningyuwen on 17-9-26.
 */

public class MyLoveMusicFragment extends Fragment implements IMyLoveMusicFragment,
        MyLoveMusicAdapter.AddItemClickListener, IMainActivityToFragment {
    private static final String TAG = "test22";
    private BroadcastReceiver receiver;
    private RecyclerView mRvMyLoveMusic;
    private List<MusicData> mMyLoveMusicDatas;
    private MyLoveMusicAdapter mAdapter;
    private static boolean shouldRefreshList = false;  //判断是否需要刷新列表，在接收到广播时置为true
    private View allMusicFragmentView;      //根布局
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (allMusicFragmentView == null) {
            allMusicFragmentView = inflater.inflate(R.layout.fragment_love_music, container, false);
            mRvMyLoveMusic = allMusicFragmentView.findViewById(R.id.rv_mylove_music);
        }

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
                        MusicData musicData = ((MainActivity)mContext).getDataFromPidFromDB(
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
        mContext.registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
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
        mMyLoveMusicDatas.addAll(((MainActivity)mContext).getMyLoveMusicData());
        mAdapter = new MyLoveMusicAdapter(mContext, mMyLoveMusicDatas);
        mRvMyLoveMusic.setAdapter(mAdapter);
        mAdapter.addItemClickListener(this);
    }

    /**
     * 我喜爱的音乐列表属于 所有音乐列表，但是此处的position不同于所有音乐列表的poisition
     * 暂时不写这个，后面确定：播放我喜爱的音乐时，后台播放列表的音乐数据
     * 2018.01.12确定，点击其中的音乐播放时，只播放我喜爱的音乐歌单
     * @param position position
     */
    @Override
    public void playMusic(int position) {
        if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_MYLOVE){
            //当前歌单是我喜爱的音乐，则不切换
            ((MainActivity) mContext).showToast(mRvMyLoveMusic, "音乐名： "
                    + mMyLoveMusicDatas.get(position).getMusicName());
            ((MainActivity) mContext).playMusicOnBackstage(position);
        }else {
            //切换歌单
            BaseActivity.MUSIC_LIST_PLAY_NOW = StaticFinalUtil.MUSIC_LIST_PLAY_MYLOVE;  //切换到我喜爱的歌单
            if (mMyLoveMusicDatas.size() == 0){
                return;
            }
            ((MainActivity)mContext).showToast(mRvMyLoveMusic, "开始播放歌单：《我喜爱的音乐》");
            //修改BaseActivity中的mMusicDatas数据
            if (BaseActivity.mMusicDatas == null){
                BaseActivity.mMusicDatas = new ArrayList<>();
            }
            BaseActivity.mMusicDatas.clear();
            //this.mMusicDatas已经是本歌单的数据了
            BaseActivity.mMusicDatas.addAll(mMyLoveMusicDatas);
            //向Service传递数据
            ((MainActivity)mContext).initServiceData();
            ((MainActivity)mContext).playMusicOnBackstage(position);
//            BaseActivity.mServiceDataTrans.playMusicFromClick(position);
            //刷新播放页面
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            localBroadcastManager.sendBroadcast(new Intent().setAction(
                    StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST));
//            mContext.sendBroadcast(new Intent().setAction(StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST));
        }


//        int index = ((MainActivity)mContext).getPositionFromPid(mMyLoveMusicDatas.get(position).getpId());
//        if (index == -1){
//            //-1说明不存在于当前歌单，切换歌单处理
//            //播放全部，删除之前的歌单信息，添加新的歌单信息，
//            if (mMyLoveMusicDatas.size() == 0){
//                return;
//            }
//            ((MainActivity)mContext).showToast(mRvMyLoveMusic, "开始播放歌单：《我喜爱的音乐》");
//            //修改BaseActivity中的mMusicDatas数据
//            if (BaseActivity.mMusicDatas == null){
//                BaseActivity.mMusicDatas = new ArrayList<>();
//            }
//            BaseActivity.mMusicDatas.clear();
//            //this.mMusicDatas已经是本歌单的数据了
//            BaseActivity.mMusicDatas.addAll(mMyLoveMusicDatas);
//            //向Service传递数据
//            ((MainActivity)mContext).initServiceData();
//            BaseActivity.mServiceDataTrans.playMusicFromClick(position);
//            //刷新播放页面
//            mContext.sendBroadcast(new Intent().setAction(StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST));
//        }else {
//            //存在，直接播放
//            ((MainActivity) mContext).showToast(mRvMyLoveMusic, "音乐名： "
//                    + mMyLoveMusicDatas.get(position).getMusicName());
//            ((MainActivity) mContext).playMusicOnBackstage(position);
//        }
    }

    /**
     * 取消喜爱
     * @param position position
     */
    @Override
    public void setNotLove(int position) {
        //通知AllMusicFragment更新，将这首音乐变为不喜欢
        ((MainActivity)mContext).updateLoveMusic(mMyLoveMusicDatas.get(position));

        mMyLoveMusicDatas.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新音乐列表，初始化时通知几个Fragment获取相应数据
     */
    @Override
    public void refreshAllMusic() {
        Log.i(TAG, "refreshAllMusic: 刷新列表MyLove");
        //重新导入音乐数据时，刷新列表
        if (mMyLoveMusicDatas == null){
            mMyLoveMusicDatas = new ArrayList<>();
        }
        mMyLoveMusicDatas.clear();
        mMyLoveMusicDatas.addAll(((MainActivity)mContext).getMyLoveMusicData());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshAllMusicDislike(MusicData musicData) {

    }

    @Override
    public void refreshCustomMusic(SongListInfo songListInfo) {

    }

}
