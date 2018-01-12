package com.example.ningyuwen.music.view.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.presenter.i.IMusicSongListPresenter;
import com.example.ningyuwen.music.view.adapter.MusicSongListItemAdapter;

/**
 * 播放页面显示歌单的popupwindow
 * Created by ningyuwen on 18-1-12.
 */

public class MusicSongListPopupWindow extends PopupWindow implements
        MusicSongListItemAdapter.ISetItemOnClickListener {

    private RecyclerView mRvSongList;   //歌单
    private MusicSongListItemAdapter adapter;   //adapter
    private Context mContext;
    private IMusicSongPopToPlayPop mIMusicPopListener;  //监听

    public MusicSongListPopupWindow(Context context) {
//        super(context);
        this(LayoutInflater.from(context)
                        .inflate(R.layout.layout_music_popup_window, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                900, true);
        mContext = context;
    }

    public MusicSongListPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicSongListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MusicSongListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MusicSongListPopupWindow() {
        super();
    }

    public MusicSongListPopupWindow(View contentView) {
        super(contentView);
    }

    public MusicSongListPopupWindow(int width, int height) {
        super(width, height);
    }

    public MusicSongListPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public MusicSongListPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
        adapter = new MusicSongListItemAdapter(contentView.getContext());
        mRvSongList = contentView.findViewById(R.id.rv_music_list_pop);
        mRvSongList.setLayoutManager(new LinearLayoutManager(contentView.getContext()));
        mRvSongList.setAdapter(adapter);
        adapter.setListener(this);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

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
