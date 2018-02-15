package com.example.ningyuwen.music.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
import com.example.ningyuwen.music.view.adapter.MusicSongListItemAdapter;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

/**
 * 播放页面显示歌单的popupwindow
 * Created by ningyuwen on 18-1-12.
 */

public class MusicSongListDialogFragment extends BottomSheetDialogFragment implements
        MusicSongListItemAdapter.ISetItemOnClickListener {

    private MusicSongListItemAdapter adapter;   //adapter
    private IMusicSongPopToPlayPop mIMusicPopListener;  //监听
    private TextView mTvMusicListNmae;      //歌单名

    public MusicSongListDialogFragment() {
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = null;
        if (window != null) {
            params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = 1000;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i("ning13", "onCreateView: " + 1);
        View view = inflater.inflate(R.layout.layout_music_popup_window, container);
//        BottomSheetBehavior.from(view).setPeekHeight(STATE_COLLAPSED);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new MusicSongListItemAdapter(getContext());
        mTvMusicListNmae = view.findViewById(R.id.tv_playlist_name);
        RecyclerView mRvSongList = view.findViewById(R.id.rv_music_list_pop);
        mRvSongList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvSongList.setAdapter(adapter);
        adapter.setListener(this);

        if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_ALL_MUSIC){
            mTvMusicListNmae.setText("歌单：所有音乐");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_CISTOM_LIST){
            mTvMusicListNmae.setText("歌单：自定歌单");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_MYLOVE){
            mTvMusicListNmae.setText("歌单：我喜爱的");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_CLASSIFY_PLAYER){
            mTvMusicListNmae.setText("歌单：歌手分类");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_SEARCH_MUSIC){
            mTvMusicListNmae.setText("歌单：搜索音乐");
        }
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
//        super.showAtLocation(parent, gravity, x, y);
        if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_ALL_MUSIC){
            mTvMusicListNmae.setText("歌单：所有音乐");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_CISTOM_LIST){
            mTvMusicListNmae.setText("歌单：自定歌单");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_MYLOVE){
            mTvMusicListNmae.setText("歌单：我喜爱的");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_CLASSIFY_PLAYER){
            mTvMusicListNmae.setText("歌单：歌手分类");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_SEARCH_MUSIC){
            mTvMusicListNmae.setText("歌单：搜索音乐");
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 点击事件，传给popupwindow,切换音乐
     * @param position position
     */
    @Override
    public void setPlayMusicPosition(int position) {
        if (mIMusicPopListener != null) {
            mIMusicPopListener.setPlayMusicPosition(position);
        }
    }

    public void setPopupWindowListener(IMusicSongPopToPlayPop listener){
        mIMusicPopListener = listener;
    }

    /**
     * popupwindow传递数据
     */
    public interface IMusicSongPopToPlayPop{
        void setPlayMusicPosition(int position);
    }
}
