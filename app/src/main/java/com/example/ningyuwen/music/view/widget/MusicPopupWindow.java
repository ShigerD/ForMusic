package com.example.ningyuwen.music.view.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.view.adapter.SongListItemAdapter;

/**
 * MusicPopupWindow 歌单，音乐等等需要
 * Created by ningyuwen on 17-12-21.
 */

public class MusicPopupWindow extends PopupWindow {

    private String title;
    private View mRootView;

    public MusicPopupWindow(Context context) {
        super(context);
        initView(context);
    }

    public MusicPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MusicPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MusicPopupWindow() {
        super();
    }

    public MusicPopupWindow(View contentView) {
        super(contentView);
    }

    public MusicPopupWindow(int width, int height) {
        super(width, height);
    }

    public MusicPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public MusicPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        mRootView = contentView;
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_music_popup_window, null);
        setContentView(view);
    }

    public MusicPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        ((TextView) mRootView.findViewById(R.id.tv_playlist_name)).setText("歌单：" + title);
//        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_music_list_pop);
//        recyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
//        recyclerView.setAdapter(new SongListItemAdapter(parent.getContext(), 1));
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    public void setTitle(String listName) {
        title = listName;
    }
}
