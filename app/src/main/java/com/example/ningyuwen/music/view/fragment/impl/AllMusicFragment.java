package com.example.ningyuwen.music.view.fragment.impl;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.i.IMainActivityToFragment;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.AllMusicInfoAdapter;
import com.example.ningyuwen.music.view.widget.AddToPlaylistDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 所有歌曲页面
 * 2018.01.12做以下修改：
 * 因为歌单的切换是一件麻烦事，为了简单起见，所有fragment点击事件都加上判断，点击的音乐是否存在于当前歌单中，
 * 若存在，直接播放，不存在，切换歌单
 * Created by ningyuwen on 17-9-26.
 */

public class AllMusicFragment extends Fragment implements AllMusicInfoAdapter.AddItemClickListener,
        IMainActivityToFragment{

    private static final String TAG = "test22";
    private BroadcastReceiver receiver;
    private List<MusicData> mAllMusicDatas;
    private RecyclerView mRvAllMusicInfo;
    private boolean shouldRefreshList = false;  //判断是否需要刷新列表，在接收到广播时置为true
    private AllMusicInfoAdapter mAdapter;   //adapter
    private static boolean shouldRefresh = false;   //是否需要刷新
    private View allMusicFragmentView;      //根布局
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (allMusicFragmentView == null) {
            allMusicFragmentView = inflater.inflate(R.layout.fragment_all_music, container, false);
            mRvAllMusicInfo = allMusicFragmentView.findViewById(R.id.rv_all_music_info);
        }
        return allMusicFragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        createBroadcastReceiver();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && shouldRefresh){
            mAdapter.notifyDataSetChanged();
            shouldRefresh = false;
        }
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

    private void showMusicInfo() {
        Log.i("test", "showMusicInfo: " + mAllMusicDatas.size());
        mRvAllMusicInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AllMusicInfoAdapter(mContext, mAllMusicDatas);
        mRvAllMusicInfo.setAdapter(mAdapter);
        mAdapter.addItemClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    /**
     * 接口回调, 播放音乐
     * 2018.01.12新做的修改在此处进行，播放音乐时做判断
     * @param position position
     */
    @Override
    public void playMusic(int position) {
        if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_ALL_MUSIC){
            //当前歌单是所有音乐，则不切换
            ((MainActivity) mContext).showToast(mRvAllMusicInfo, "音乐名： "
                    + mAllMusicDatas.get(position).getMusicName());
            ((MainActivity) mContext).playMusicOnBackstage(position);
        }else {
            //切换歌单
            BaseActivity.MUSIC_LIST_PLAY_NOW = StaticFinalUtil.MUSIC_LIST_PLAY_ALL_MUSIC;   //切换歌单
            if (mAllMusicDatas.size() == 0){
                return;
            }
            ((MainActivity)mContext).showToast(mRvAllMusicInfo, "开始播放歌单：《所有音乐》");
            //修改BaseActivity中的mMusicDatas数据
            if (BaseActivity.mMusicDatas == null){
                BaseActivity.mMusicDatas = new ArrayList<>();
            }
            BaseActivity.mMusicDatas.clear();
            //this.mMusicDatas已经是本歌单的数据了
            BaseActivity.mMusicDatas.addAll(mAllMusicDatas);
            //向Service传递数据
            ((MainActivity)mContext).initServiceData();
//            BaseActivity.mServiceDataTrans.playMusicFromClick(position);
            ((MainActivity)mContext).playMusicOnBackstage(position);
            //刷新播放页面
            mContext.sendBroadcast(new Intent().setAction(StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST));
        }



//        int index = ((MainActivity)mContext).getPositionFromPid(mAllMusicDatas.get(position).getpId());
//        if (index == -1){
//            //-1说明不存在于当前歌单，切换歌单处理
//            //播放全部，删除之前的歌单信息，添加新的歌单信息，
//            if (mAllMusicDatas.size() == 0){
//                return;
//            }
//            ((MainActivity)mContext).showToast(mRvAllMusicInfo, "开始播放歌单：《所有音乐》");
//            //修改BaseActivity中的mMusicDatas数据
//            if (BaseActivity.mMusicDatas == null){
//                BaseActivity.mMusicDatas = new ArrayList<>();
//            }
//            BaseActivity.mMusicDatas.clear();
//            //this.mMusicDatas已经是本歌单的数据了
//            BaseActivity.mMusicDatas.addAll(mAllMusicDatas);
//            //向Service传递数据
//            ((MainActivity)mContext).initServiceData();
//            BaseActivity.mServiceDataTrans.playMusicFromClick(position);
//            //刷新播放页面
//            mContext.sendBroadcast(new Intent().setAction(StaticFinalUtil.SERVICE_RECEIVE_REFRESH_MUSICLIST));
//        }else {
//            ((MainActivity) mContext).showToast(mRvAllMusicInfo, "音乐名： "
//                    + mAllMusicDatas.get(position).getMusicName());
//            ((MainActivity) mContext).playMusicOnBackstage(position);
//        }

    }

    /**
     * 接口回调，设置是否是喜爱
     * @param position position
     */
    @Override
    public void setIsLove(int position) {
        Intent intent = new Intent("SetMyLove");
        if (mAllMusicDatas.get(position).isLove()){
            //之前是喜愛，在我喜愛的頁面刪除
            ((MainActivity)mContext).setIsLoveToDB(
                    mAllMusicDatas.get(position).getpId(), false);
            intent.putExtra("status", "delete");
            intent.putExtra("pid", mAllMusicDatas.get(position).getpId());
            mContext.sendBroadcast(intent);
        }else {
            //之前不喜愛，在我喜愛的頁面添加
            ((MainActivity)mContext).setIsLoveToDB(
                    mAllMusicDatas.get(position).getpId(), true);
            intent.putExtra("status", "add");
            intent.putExtra("pid", mAllMusicDatas.get(position).getpId());
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * 长按音乐item，弹出dialog
     * @param position positon
     */
    @Override
    public void longClickMusicItem(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("歌曲:" + mAllMusicDatas.get(position).getMusicName());
        String[] items = {"播放", "收藏到歌单", "分享音乐", "剪辑歌曲"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        //播放
                        playMusic(position);
                        break;
                    case 1:
                        //收藏到歌单
                        final AddToPlaylistDialog mAddToPlaylistDialog = new AddToPlaylistDialog(mContext);
                        mAddToPlaylistDialog.setTitle("收藏到歌单");
                        mAddToPlaylistDialog.setThisMusicPid(mAdapter.getItem(position).getpId());
                        //设置监听回调，将音乐添加到歌单中
                        mAddToPlaylistDialog.setListener(new AddToPlaylistDialog.AddItemClickListener() {
                            @Override
                            public void addMusicToSongList(long musicId, long songListId) {
                                ((MainActivity)mContext).addMusicToSongList(musicId, songListId);
                                mAddToPlaylistDialog.dismiss();
                            }
                        });
                        mAddToPlaylistDialog.show();
                        break;
//                    case 2:
//                        //添加到我喜爱的
//                        setIsLove(position);
//                        break;
                    case 2:
                        //分享音乐
                        ((MainActivity)mContext).showToast(mRvAllMusicInfo, "功能暂未开放");
                        break;
                    case 3:
                        //剪辑歌曲
                        ((MainActivity)mContext).showToast(mRvAllMusicInfo, "功能暂未开放");
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 刷新音乐列表，初始化时通知几个Fragment获取相应数据
     */
    @Override
    public void refreshAllMusic() {
        Log.i(TAG, "refreshAllMusic: 刷新列表AllMusic");
        if (mAllMusicDatas == null){
            mAllMusicDatas = new ArrayList<>();
        }
        mAllMusicDatas.clear();
        if (((MainActivity)mContext) != null) {
//            mAllMusicDatas = ((MainActivity)mContext).getMusicDatas();
            mAllMusicDatas.addAll(((MainActivity)mContext).getMusicDatas());
            showMusicInfo();
        }
    }

    /**
     * 在我喜爱的页面将音乐取消喜爱，在所有音乐页面将它置为不喜欢
     */
    @Override
    public void refreshAllMusicDislike(MusicData musicData) {
        for (int i = 0; i < mAllMusicDatas.size(); i++){
            if (Objects.equals(mAllMusicDatas.get(i).getpId(), musicData.getpId())){
                mAllMusicDatas.set(i, musicData);
                break;
            }
        }
        shouldRefresh = true;
    }

    @Override
    public void refreshCustomMusic(SongListInfo songListInfo) {

    }

}
