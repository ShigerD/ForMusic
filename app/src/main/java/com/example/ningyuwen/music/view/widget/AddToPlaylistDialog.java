package com.example.ningyuwen.music.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.CustomizeMusicAdapter;

import java.util.List;

/**
 * 收藏到歌单Dialog
 * Created by ningyuwen on 17-12-11.
 */

public class AddToPlaylistDialog extends Dialog implements CustomizeMusicAdapter.AddItemClickListener {

    private RecyclerView mRvPlayList;    //歌单列表
    private CustomizeMusicAdapter mAdapter;   //adapter,歌单列表
    private Context mContext;
    private List<SongListInfo> mSongListInfos; //歌单List

    public AddToPlaylistDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public AddToPlaylistDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected AddToPlaylistDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_add_playlist);

        findViews();
        initView();

    }

    private void initView() {
        mRvPlayList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSongListInfos = ((MainActivity) mContext).getSongListInfo();
        mAdapter = new CustomizeMusicAdapter(mContext, mSongListInfos, true);
        mRvPlayList.setAdapter(mAdapter);
        mAdapter.addItemClickListener(this);
    }

    private void findViews() {
        mRvPlayList = (RecyclerView) findViewById(R.id.rv_playlist);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void jumpSongList(int position) {

    }

    @Override
    public void addSongList(int position) {

    }
}
